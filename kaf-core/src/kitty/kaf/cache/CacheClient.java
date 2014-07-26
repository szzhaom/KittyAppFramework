package kitty.kaf.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import kitty.kaf.GafUtil;
import kitty.kaf.cache.clients.CacheConnectionPoolFactory;
import kitty.kaf.exceptions.NoConfigDefFoundError;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.io.Readable;
import kitty.kaf.io.Writable;
import kitty.kaf.logging.Logger;
import kitty.kaf.util.DateTime;

/**
 * 缓存客户端
 * <p>
 * 缓存客户端是基础框架提供的标准缓存服务客户端，支持通过配置的方法，适应不同的缓存服务器，如：Memcached，Redis，阿里云OCS等。
 * </p>
 * <p>
 * 本类具备多线程共享访问的能力，通常来说，一个配置项，创建一个实例即可
 * </p>
 * 
 * @author 赵明
 * @version 1.0
 */
public class CacheClient implements ICacheClient {
	static final Logger logger = Logger.getLogger(CacheClient.class);

	static class CacheRemoteConfigItem {
		public String refName;
		public String type;
	}

	/**
	 * 缓存配置Map，静态变量
	 */
	static final ConcurrentHashMap<String, CacheRemoteConfigItem> configMap = new ConcurrentHashMap<String, CacheClient.CacheRemoteConfigItem>();
	static {
		try {
			LoadConfig();
		} catch (Throwable e) {
			logger.error("Load cache configuration fails: ", e);
		}
	}

	private static void LoadConfig() throws ParserConfigurationException, SAXException, IOException {
		NodeList ls = GafUtil.getBasicConfigRoot().getElementsByTagName("cacheservices");
		if (ls != null && ls.getLength() > 0) {
			ls = ((Element) ls.item(0)).getElementsByTagName("cacheitem");
			for (int i = 0; i < ls.getLength(); i++) {
				Element el = (Element) ls.item(i);
				CacheRemoteConfigItem item = new CacheRemoteConfigItem();
				item.refName = el.getAttribute("refname");
				item.type = el.getAttribute("type");
				configMap.put(el.getAttribute("name"), item);
			}
		}
	}

	/**
	 * 绑定的缓存远程接口，缓存客户端通过此接口与缓存服务器通讯
	 */
	ICacheClient cacheRemote;

	/**
	 * 通过指定缓存远程接口，创建一个缓存客户端。
	 * 
	 * @param cacheRemote
	 *            缓存远程接口
	 */
	public CacheClient(ICacheClient cacheRemote) {
		super();
		this.cacheRemote = cacheRemote;
	}

	/**
	 * 从配置中获取缓存客户端
	 * 
	 * @param configName
	 *            配置项目名称
	 * @throws IOException
	 * @throws NoConfigDefFoundError
	 *             找不到配置项时抛出
	 * 
	 */
	public CacheClient(String configName) {
		try {
			cacheRemote = CacheConnectionPoolFactory.getClientInstance(this, configName);
		} catch (IOException e) {
			logger.debug("Failure to get a cached instance:", e);
		}
		if (cacheRemote == null)
			throw new NoConfigDefFoundError(configName + " not found");
		// CacheRemoteConfigItem item = configMap.get(configName);
		// if (item == null)
		// throw new NoConfigDefFoundError(configName + " not found");
		// if (item.type.equals("memcached"))
		// cacheRemote = MemcachedClient.newInstance(this, item.refName);
		// else
		// throw new UnsupportedConfigurationError("Unsupported Type:" +
		// item.type);
	}

	@Override
	public void set(String key, Object value, Date expiry) throws IOException, InterruptedException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		cacheRemote.set(key, value, expiry);
	}

	@Override
	public Object get(String key) throws IOException, InterruptedException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		return cacheRemote.get(key);
	}

	@Override
	public void get(List<String> keys, Map<String, Object> map) throws InterruptedException, IOException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		cacheRemote.get(keys, map);
	}

	@Override
	public Long incrdecr(String key, long stepValue) throws IOException, InterruptedException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		return cacheRemote.incrdecr(key, stepValue);
	}

	@Override
	public boolean delete(String key) throws IOException, InterruptedException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		return cacheRemote.delete(key);
	}

	@Override
	public void delete(Object[] keys) throws IOException, InterruptedException {
		if (cacheRemote == null)
			throw new NullPointerException("Has not been correctly initialized");
		cacheRemote.delete(keys);
	}

	/**
	 * 获取数据，并转换成对象返回，出现错误的时候，则会删除缓存服务器上的key
	 * 
	 * @param key
	 *            数据的key
	 * @param valueClazz
	 *            要返回的对象类
	 * @return 读取的对象
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public <E extends Readable> E get(String key, Class<E> valueClazz) throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		E cl;
		try {
			cl = valueClazz.newInstance();
			cl.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
		} catch (IOException e) {
			delete(key);
			throw e;
		} catch (Throwable e) {
			delete(key);
			throw new IOException(e);
		}
		return cl;
	}

	/**
	 * 装入数据至o中，如果装载失败，则会删除缓存服务器上的key
	 * 
	 * @param key
	 *            数据的key
	 * @param o
	 *            要装入数据的对象类
	 * @return 读取的对象
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public <E extends Readable> void load(String key, E o) throws InterruptedException, IOException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return;
		try {
			o.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
		} catch (IOException e) {
			delete(key);
			throw e;
		} catch (Throwable e) {
			delete(key);
			throw new IOException(e);
		}
	}

	/**
	 * 获取多项数据，并转换成对象返回，如果获取某些数据失败，会删除缓存服务器上的对应项。本方法不确保keys中所有的key都返回至结果集中，
	 * 只返回缓存服务器中存在的key
	 * 
	 * @param keys
	 *            要返回数据的key列表
	 * @param valueClazz
	 *            要返回的对象灰
	 * @return 读取的对象Map
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public <E extends Readable> Map<String, E> get(List<String> keys, Class<E> valueClazz) throws InterruptedException,
			IOException {
		Map<String, E> rmap = new HashMap<String, E>();
		if (keys == null || keys.size() == 0)
			return rmap;
		Map<String, Object> map = new HashMap<String, Object>();
		get(keys, map);
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			byte[] b = (byte[]) map.get(key);
			E cl;
			try {
				cl = valueClazz.newInstance();
				cl.readFromStream(new DataReadStream(new ByteArrayInputStream(b), 3000));
				rmap.put(key, cl);
			} catch (IOException e) {
				delete(key);
				throw e;
			} catch (Throwable e) {
				delete(key);
				throw new IOException(e);
			}
		}
		return rmap;
	}

	/**
	 * 获取用setPacketByteLenStringList设置的打包字串列表
	 * 
	 * @param key
	 *            数据key
	 * @return 字串列表
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public List<String> getPacketByteLenStringList(String key) throws IOException, InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b != null) {
			DataReadStream stream = new DataReadStream(new ByteArrayInputStream(b), 3000);
			try {
				return stream.readPacketByteLenStringList();
			} finally {
				stream.getInputStream().close();
			}
		} else
			return null;
	}

	/**
	 * 获取用setPacketShortLenStringList设置的打包字串列表
	 * 
	 * @param key
	 *            数据key
	 * @return 字串列表
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public List<String> getPacketShortLenStringList(String key) throws IOException, InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b != null) {
			DataReadStream stream = new DataReadStream(new ByteArrayInputStream(b), 3000);
			try {
				return stream.readPacketShortLenStringList();
			} finally {
				stream.getInputStream().close();
			}
		} else
			return null;
	}

	/**
	 * 将字符串列表保存至缓存服务器中，先保存ls.size()，再依次保存列表中字串，字串写入方式是：1字节长度+字串内容
	 * 
	 * @param key
	 *            数据key
	 * @param ls
	 *            字串列表
	 * @param expiry
	 *            过期时间
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public void setPacketByteLenStringList(String key, List<String> ls, Date expiry) throws IOException,
			InterruptedException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writePacketByteLenStringList(ls);
		set(key, out.toByteArray(), expiry);
	}

	/**
	 * 将字符串列表保存至缓存服务器中，先保存ls.size()，再依次保存列表中字串，字串写入方式是：2字节长度+字串内容
	 * 
	 * @param key
	 *            数据key
	 * @param ls
	 *            字串列表
	 * @param expiry
	 *            过期时间
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public void setPacketShortLenStringList(String key, List<String> ls, Date expiry) throws IOException,
			InterruptedException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writePacketShortLenStringList(ls);
		set(key, out.toByteArray(), expiry);
	}

	/**
	 * 从缓存服务器读取用Writable写入的数据，与setWritableList对应
	 * 
	 * @param key
	 *            数据key
	 * @param clazz
	 *            Readable的类，用于生成Readable对象
	 * @return 读取的数据列表
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public <E extends Readable> List<E> getReadableList(String key, Class<E> clazz) throws IOException,
			InterruptedException {
		byte[] b = (byte[]) get(key);
		if (b == null)
			return null;
		List<E> ret = new ArrayList<E>();
		ByteArrayInputStream in = new ByteArrayInputStream(b);
		DataReadStream stream = new DataReadStream(in, 3000);
		int c = stream.readInt();
		for (int i = 0; i < c; i++) {
			try {
				E o = clazz.newInstance();
				o.readFromStream(stream);
				ret.add(o);
			} catch (Throwable e) {
				throw new IOException(e);
			}
		}
		return ret;
	}

	/**
	 * 将ls按顺序写入到缓存中，先写入ls.size()，再依次调用ls.get(i).writeToStream()写入各列表项的数据。
	 * 
	 * @param key
	 *            缓存key
	 * @param ls
	 *            对象列表
	 * @param expiry
	 *            过期时间
	 * @throws InterruptedException
	 *             如果服务线程中断
	 * @throws IOException
	 *             如果与缓存服务器出现通讯故障
	 */
	public <E extends Writable> void setWritableList(String key, List<E> ls, Date expiry) throws IOException,
			InterruptedException {
		if (ls == null)
			return;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DataWriteStream stream = new DataWriteStream(out, 3000);
		stream.writeInt(ls.size());
		for (E o : ls)
			o.writeToStream(stream);
		set(key, out.toByteArray(), expiry);
	}

	public static void main(String[] args) {
		try {
			CacheClient client = new CacheClient("session");
			// System.out.println(client.get("foo"));
			System.out.println(client.incrdecr("aaa", 10));
			System.out.println(client.incrdecr("aaa", 10));
			client.delete(new Object[] { "aaa", "bbb", "ccc" });
			System.out.println(client.delete("aaa"));
			client.set("asdf", 1, new DateTime().addSeconds(5).getTime());
			for (int i = 0; i < 1000; i++) {
				System.out.println(client.get("asdf"));
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
