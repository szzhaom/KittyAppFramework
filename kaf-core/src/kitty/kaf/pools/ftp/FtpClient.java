package kitty.kaf.pools.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import kitty.kaf.GafUtil;
import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.NoConfigDefFoundError;
import kitty.kaf.file.File;
import kitty.kaf.file.IFileClient;
import kitty.kaf.logging.Logger;

/**
 * FTP客户端
 * 
 * @author 赵明
 * @version 1.0
 */
public class FtpClient implements IFileClient {
	static final Logger logger = Logger.getLogger(FtpClient.class);
	FtpConnection con;
	FtpConfigItem item;

	static class FtpConfigItem {
		public String name;
		public String host, user, password, ftpRoot, charset;
		public int port;
	}

	/**
	 * 缓存配置Map，静态变量
	 */
	static final ConcurrentHashMap<String, FtpConfigItem> configMap = new ConcurrentHashMap<String, FtpClient.FtpConfigItem>();
	static {
		try {
			LoadConfig();
		} catch (Throwable e) {
			logger.error("Load cache configuration fails: ", e);
		}
	}

	private static void LoadConfig() throws ParserConfigurationException, SAXException, IOException {
		NodeList ls = GafUtil.getBasicConfigRoot().getElementsByTagName("ftp");
		if (ls != null && ls.getLength() > 0) {
			ls = ((Element) ls.item(0)).getElementsByTagName("ftphost");
			for (int i = 0; i < ls.getLength(); i++) {
				Element el = (Element) ls.item(i);
				FtpConfigItem item = new FtpConfigItem();
				item.name = el.getAttribute("name");
				item.user = el.getAttribute("user");
				item.port = Integer.valueOf(el.getAttribute("port"));
				item.host = el.getAttribute("host");
				item.password = el.getAttribute("password");
				item.ftpRoot = el.getAttribute("ftproot");
				item.charset = el.getAttribute("charset");
				configMap.put(el.getAttribute("name"), item);
			}
		}
	}

	public FtpClient(FtpConnection con) {
		super();
		this.con = con;
	}

	public FtpClient(String name) throws ConnectException {
		item = configMap.get(name);
		if (item == null)
			throw new NoConfigDefFoundError(name + " not found");
		con = new FtpConnection(new InetSocketAddress(item.host, item.port), 30000, 30000);
		con.setCharset(item.charset);
		con.setUser(item.user);
		con.setPwd(item.password);
		try {
			con.open();
		} catch (Throwable e) {
			con.close();
			throw new ConnectException(e);
		}
	}

	public String getFtpRootPath(String fileName) {
		String f = item.ftpRoot;
		if (f != null) {
			f = f.trim();
			if (!f.isEmpty()) {
				f = f.replace("\\", "/");
				f = f.replace("//", "/");
				fileName = f + fileName;
			}
			fileName = fileName.replace("\\", "/");
			fileName = fileName.replace("//", "/");
			if (!fileName.startsWith("/"))
				fileName = "/" + fileName;
		}
		return fileName;
	}

	@Override
	public void upload(String remoteFile, InputStream stream) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		con.upload(remoteFile, stream);
	}

	@Override
	public void append(String remoteFile, InputStream stream) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		con.append(remoteFile, stream);
	}

	@Override
	public void download(String remoteFile, OutputStream stream) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		con.download(remoteFile, stream);

	}

	@Override
	public File getFileAttribute(String remoteFile) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		long[] r = con.getLastModifiedAndSize(remoteFile);
		File f = new File();
		f.setFileName(remoteFile);
		f.setLastModified(new Date(r[0]));
		f.setLength(r[1]);
		return f;
	}

	@Override
	public List<File> listFiles(String remoteFile, String wildcards) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		List<File> ls = new ArrayList<File>();
		for (FtpFile o : con.listFiles(remoteFile)) {
			File f = new File();
			f.setFileName(o.getFileName());
			f.setDirectory(f.isDirectory());
			f.setLastModified(o.getLastModified());
			f.setLength(o.getSize());
			ls.add(f);
		}
		return ls;
	}

	@Override
	public boolean delete(String remoteFile, boolean isDirectory) throws InterruptedException, IOException {
		if (con == null)
			throw new NullPointerException("ftp connection not initialize.");
		remoteFile = getFtpRootPath(remoteFile);
		try {
			con.delete(remoteFile, isDirectory);
			return true;
		} catch (FtpReplyError e) {
			return false;
		}
	}

	@Override
	public void close() {
		if (con != null) {
			con.close();
			con = null;
		}
	}

}
