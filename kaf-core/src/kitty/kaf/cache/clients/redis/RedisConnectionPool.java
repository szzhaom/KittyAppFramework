package kitty.kaf.cache.clients.redis;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.logging.Logger;
import kitty.kaf.pools.tcp.TcpConnectionPool;
import kitty.kaf.util.DateTime;

/**
 * 缓存TCP连接池
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class RedisConnectionPool extends TcpConnectionPool<RedisConnection> {
	final static Logger logger = Logger.getLogger(RedisConnectionPool.class);
	String masterName;
	InetSocketAddress[] monitorAddressList;
	boolean masterSlavesFirstFinded;
	String masterChangeFlag;

	public RedisConnectionPool(Element config) {
		super(config);
		NodeList list = config.getElementsByTagName("param");
		for (int i = 0; i < list.getLength(); i++) {
			Element el = (Element) list.item(i);
			String name = el.getAttribute("name");
			String value = el.getAttribute("value");
			if (name.equals("mastername"))
				masterName = value;
			else if (name.equals("monitorurls")) {
				String s[] = StringHelper.splitToStringArray(value, ";");
				monitorAddressList = new InetSocketAddress[s.length];
				int j = 0;
				for (String str : s) {
					String ss[] = StringHelper.splitToStringArray(str, ":");
					monitorAddressList[j++] = new InetSocketAddress(ss[0], Integer.valueOf(ss[1]));
				}
			}
		}
	}

	@Override
	protected RedisConnection createConnection() throws ConnectException {
		if (!masterSlavesFirstFinded)

			findMasterSlaves();
		return new RedisConnection(this);
	}

	public String getMasterName() {
		return masterName;
	}

	public InetSocketAddress[] getMonitorAddressList() {
		return monitorAddressList;
	}

	private long lastFindMasterSlavesTime = 0;

	/**
	 * 从redis的sentinel服务器获取master的地址
	 * 
	 */
	public synchronized boolean findMasterSlaves() {
		double s = DateTime.secondsBetween(lastFindMasterSlavesTime, System.currentTimeMillis());
		if (s < 10)
			return false;
		lastFindMasterSlavesTime = System.currentTimeMillis();
		try {
			if (getMonitorAddressList() != null && getMonitorAddressList().length > 0) {
				boolean listChanged = false;
				List<InetSocketAddress> list = new ArrayList<InetSocketAddress>();
				for (InetSocketAddress address : getMonitorAddressList())
					list.add(address);
				for (int k = 0; k < getMonitorAddressList().length; k++) {
					InetSocketAddress address = getMonitorAddressList()[k];
					Socket socket = new Socket();
					try {
						socket.connect(address, getConnectionTimeout());
						socket.setSoTimeout(getSoTimeout());
						RedisProtocol protocol = new RedisProtocol(new DataReadStream(socket.getInputStream(),
								getDataTimeout()), new DataWriteStream(socket.getOutputStream(), getDataTimeout()),
								false);
						protocol.sendCommand(RedisProtocol.Command.SENTINEL, "get-master-addr-by-name", getMasterName());
						List<?> ls = (List<?>) protocol.readReply();
						InetSocketAddress masterAddress = new InetSocketAddress(new String((byte[]) ls.get(0)),
								Integer.valueOf(new String((byte[]) ls.get(1))));
						protocol.sendCommand(RedisProtocol.Command.SENTINEL, "slaves", getMasterName());
						ls = (List<?>) protocol.readReply();
						List<InetSocketAddress> slaveAddrs = new ArrayList<InetSocketAddress>();
						for (Object o : ls) {
							List<?> ls1 = (List<?>) o;
							String url = null;
							String flags = null;
							for (int i = 0; i < ls1.size(); i += 2) {
								String key = new String((byte[]) ls1.get(i));
								String value = new String((byte[]) ls1.get(i + 1));
								if (url == null) {
									if (key.equals("name"))
										url = value;
								}
								if (flags == null) {
									if (key.equals("flags"))
										flags = value;
								}
							}
							if (flags != null && !flags.trim().endsWith(",disconnected")) {// 只添加在线的
								String ss[] = StringHelper.splitToStringArray(url, ":");
								slaveAddrs.add(new InetSocketAddress(ss[0], Integer.valueOf(ss[1])));
							}
						}
						InetSocketAddress addressList[] = new InetSocketAddress[slaveAddrs.size() + 1];
						addressList[0] = masterAddress;
						int n = 1;
						for (InetSocketAddress a : slaveAddrs)
							addressList[n++] = a;
						boolean changed = false;
						if (this.addressList.length != addressList.length)
							changed = true;
						else if (!this.addressList[0].equals(addressList[0]))
							changed = true;
						else {
							ArrayList<String> ls1 = new ArrayList<String>();
							ArrayList<String> ls2 = new ArrayList<String>();
							for (int i = 1; i < this.addressList.length; i++) {
								ls1.add(this.addressList[i].toString());
							}
							for (int i = 1; i < addressList.length; i++) {
								ls2.add(addressList[i].toString());
							}
							Collections.sort(ls1);
							Collections.sort(ls2);
							for (int i = 0; i < ls1.size(); i++) {
								if (!ls1.get(i).equals(ls2.get(i))) {
									changed = true;
									break;
								}
							}
						}
						if (changed) {
							this.addressList = addressList;
							masterChangeFlag = UUID.randomUUID().toString();
							logger.error("redis[" + getMasterName() + "] master changed:");
							for (InetSocketAddress addr : addressList) {
								logger.error("\t" + addr.toString());
							}
						}
						break;
					} catch (Throwable e) {
						logger.error(address + " error:", e);
						list.remove(address);
						list.add(address);
						listChanged = true;
						if (k == getMonitorAddressList().length)
							return false;
					} finally {
						try {
							socket.close();
						} catch (Throwable e) {
						}
					}
				}
				if (listChanged)
					this.monitorAddressList = list.toArray(new InetSocketAddress[0]);
				return true;
			}
			return false;
		} catch (Throwable e) {
			logger.error("error:", e);
			return false;
		} finally {
			if (!masterSlavesFirstFinded)
				masterSlavesFirstFinded = true;
		}
	}

	public String getMasterChangeFlag() {
		return masterChangeFlag;
	}
}
