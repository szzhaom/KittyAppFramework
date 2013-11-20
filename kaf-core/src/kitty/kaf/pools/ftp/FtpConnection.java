package kitty.kaf.pools.ftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import kitty.kaf.exceptions.ConnectException;
import kitty.kaf.exceptions.DataException;
import kitty.kaf.io.DataReadStream;
import kitty.kaf.io.DataWriteStream;
import kitty.kaf.logging.KafLogger;
import kitty.kaf.pools.ConnectionPool;
import kitty.kaf.pools.tcp.TcpConnection;

/**
 * FTP连接
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class FtpConnection extends TcpConnection {
	String user, pwd;
	List<String> results = new ArrayList<String>();
	int resultNo;
	String systemDesc;
	final static KafLogger logger = KafLogger
			.getLogger(FtpConnection.class);

	public FtpConnection() {
		super();
	}

	public FtpConnection(ConnectionPool<?> pool, InetSocketAddress address,
			int connectTimeout, int dataTimeout) {
		super(pool, address, connectTimeout, dataTimeout);
	}

	public FtpConnection(ConnectionPool<?> pool) {
		super(pool);
	}

	public FtpConnection(InetSocketAddress address, int connectTimeout,
			int dataTimeout) {
		super(address, connectTimeout, dataTimeout);
	}

	/**
	 * 执行FTP的list或nlst命令，得到文件/目录列表
	 * 
	 * @param specifier
	 *            通配符
	 * @param detail
	 *            是否获取详细信息
	 * @param o
	 *            获取的信息输出流
	 * @throws IOException
	 *             如果FTP连接出现异常
	 * @throws FtpReplyError
	 *             如果FTP响应不正确
	 */
	protected void list(String specifier, boolean detail, OutputStream o)
			throws IOException, FtpReplyError {
		DataWriteStream st = new DataWriteStream(o, dataTimeout);
		if (detail)
			dataComm("list " + specifier, null, st);
		else
			dataComm("nlst " + specifier, null, st);
	}

	/**
	 * 数据通讯
	 * 
	 * @param command
	 *            FTP数据命令
	 * @param in
	 *            数据输入流
	 * @param out
	 *            数据输出流
	 * @throws IOException
	 *             如果FTP连接出现异常
	 * @throws FtpReplyError
	 *             如果FTP响应不正确
	 */
	protected void dataComm(String command, DataReadStream in,
			DataWriteStream out) throws IOException, FtpReplyError {
		ServerSocket serverSocket = new ServerSocket();
		try {
			serverSocket.bind(null);
			String ip = getSocket().getLocalAddress().getHostAddress()
					.replace(".", ",");
			int port = serverSocket.getLocalPort();
			sendCmd("PORT " + ip + ',' + (port / 256) + ',' + (port % 256),
					new int[] { 200 });
			sendCmd(command, new int[] { 125, 150 });
			serverSocket.setSoTimeout(getConnectTimeout());
			Socket socket = serverSocket.accept();
			socket.setSoTimeout(getConnectTimeout());
			port = socket.getPort();
			int len;
			byte[] data = new byte[1024];
			if (out.getOutputStream() != null) {
				while ((len = socket.getInputStream().read(data)) > 0) {
					out.write(data, 0, len);
				}
			} else {
				while ((len = in.read(data)) > 0) {
					socket.getOutputStream().write(data, 0, len);
				}
			}
			try {
				socket.close();
				serverSocket.close();
			} catch (Throwable e) {
			}
			if (getResponse(new int[] { 225, 226, 250, 426 }) == 426)
				getResponse(new int[] { 226 });
		} finally {
			if (!serverSocket.isClosed())
				serverSocket.close();
		}
	}

	/**
	 * 从FTP服务器上读取一行字串
	 * 
	 * @return 读取的字串
	 * @throws IOException
	 *             如果FTP连接出现异常
	 */
	protected String readln() throws IOException {
		String r = new String(readStream.readln("\n".getBytes(), false)).trim();
		if (logger.isDebugEnabled())
			logger.debug("recv: [" + r + "]");
		return r;
	}

	/**
	 * 执行一次FTP命令
	 * 
	 * @param cmd
	 *            FTP命令字
	 * @param allowResponses
	 *            允许的响应码
	 * @return FTP响应码
	 * @throws FtpReplyError
	 *             如果FTP响应不正确
	 * @throws IOException
	 *             如果通讯异常
	 */
	protected int sendCmd(String cmd, int[] allowResponses) throws IOException,
			FtpReplyError {
		if (cmd != null) {
			if (logger.isDebugEnabled())
				logger.debug("send: [" + cmd + "]");
			writeStream.writeString(cmd + "\r\n");
		}
		return getResponse(allowResponses);
	}

	/**
	 * 从服务器返回结果中获取响应
	 * 
	 * @return 取到的响应
	 */
	protected String getReply() {
		if (results.size() > 0)
			return results.get(results.size() - 1);
		else
			return "";
	}

	/**
	 * 获取FTP服务器的响应。从FTP服务器中获取响应的内容，并解析响应码，调用checkResponse检查响应码
	 * 
	 * @param allowResponses
	 *            所有允许的响应码
	 * @return 响应码
	 * @throws FtpReplyError
	 *             如果响应码不是允许的响应码
	 */

	protected int getResponse(int[] allowResponses) throws IOException,
			FtpReplyError {
		String sLine, sTerm;
		sLine = readln();
		results.clear();
		results.add(sLine);
		if (sLine.length() > 3) {
			if (sLine.charAt(3) == '-') {
				sTerm = sLine.substring(0, 3) + " ";
				do {
					sLine = readln();
					results.add(sLine);
				} while (sLine.length() > 3 && !sLine.startsWith(sTerm));
			}
		}
		String result = getReply();
		if (result.startsWith("+OK"))
			resultNo = 0;
		else if (result.startsWith("-ERR"))
			resultNo = 1;
		else {
			try {
				resultNo = Integer.valueOf(result.substring(0, 3));
			} catch (Throwable e) {
				resultNo = 0;
			}
		}

		return checkResponse(resultNo, allowResponses);
	}

	/**
	 * 检查FTP服务器的响应
	 * 
	 * @param response
	 *            响应码
	 * @param allowResponses
	 *            所有允许的响应码
	 * @return 响应码
	 * @throws FtpReplyError
	 *             如果响应码不是允许的响应码
	 */
	protected int checkResponse(int response, int[] allowResponses)
			throws FtpReplyError {
		for (int i = 0; i < allowResponses.length; i++) {
			if (allowResponses[i] == response)
				return response;
		}
		String result = getReply();
		throw new FtpReplyError(response, result.substring(4));
	}

	String savedCurDir = null;

	/**
	 * 切换工作目录至文件所在目录，此操作如果更改了当前目录，则在更改之前，会先保存当前目录，可调用restoreWorkingDirectory来恢复
	 * 
	 * @param remoteFile
	 *            远程文件名,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param makeDirsIfNotExists
	 *            如果目录不存在，则创建
	 * @return 去除目录之后的文件名
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	protected String switchWorkingDirectoryToFile(String remoteFile,
			boolean makeDirsIfNotExists) throws IOException, FtpReplyError {
		int index = remoteFile.lastIndexOf("/");
		if (index > 0) {
			savedCurDir = pwd();
			String dir = remoteFile.substring(0, index);
			try {
				changeWorkingDirectory(dir);
			} catch (FtpReplyError e) {
				if (makeDirsIfNotExists) {
					mkdirs(dir);
					changeWorkingDirectory(dir);
				} else
					throw e;
			}
			remoteFile = remoteFile.substring(index + 1);
		} else
			savedCurDir = null;
		return remoteFile;
	}

	/**
	 * 恢复工作目录，此操作在调用switchWorkingDirectoryToFile之后的操作完成后调用
	 * 
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	protected void restoryWorkingDirectory() throws IOException, FtpReplyError {
		if (savedCurDir != null)
			changeWorkingDirectory(savedCurDir);
	}

	@Override
	public void open() throws ConnectException {
		super.open();
		try {
			getResponse(new int[] { 220 });
			login();
			sendCmd("type I", new int[] { 200 });
			if (sendCmd("syst", new int[] { 200, 215, 500 }) == 500)
				systemDesc = "Unknown host";
			else
				systemDesc = getReply().substring(4);
		} catch (IOException e) {
			throw new ConnectException(e);
		} catch (FtpReplyError e) {
			throw new ConnectException(e);
		}
	}

	/**
	 * 列出指定目录下的文件
	 * 
	 * @param remoteFile
	 *            要list文件的目录,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	public FtpFile[] listFiles(String remoteFile) throws IOException,
			FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			String cp = pwd();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			list(remoteFile, true, o);
			String str = new String(o.toByteArray()).trim();
			if (!str.isEmpty()) {
				String r[] = str.split("\r\n");
				FtpFile[] s = new FtpFile[r.length];
				for (int i = 0; i < r.length; i++) {
					FtpFile f = new FtpFile(r[i]);
					f.setFileName(cp + "/" + f.getFileName());
					if (!f.isDirectory()) {
						f.setLastModified(getLastModified(f.getFileName()));
					}
					s[i] = f;
				}
				return s;
			} else
				return null;
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 登录至FTP服务器
	 * 
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	public void login() throws IOException, FtpReplyError {
		if (sendCmd("USER " + user, new int[] { 220, 331 }) == 331) {
			sendCmd("pass " + pwd, new int[] { 230 });
		}
	}

	/**
	 * 更改工作路径
	 * 
	 * @param remoteFile
	 *            新的工作路径,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	public void changeWorkingDirectory(String remoteFile) throws IOException,
			FtpReplyError {
		sendCmd("CWD " + remoteFile, new int[] { 250 });
	}

	/**
	 * 获取文件最后修改时间
	 * 
	 * @param remoteFile
	 *            新的工作路径,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @throws FtpReplyError
	 *             如果文件或目录不存在
	 * @throws IOException
	 *             如果通讯异常
	 */
	public Date getLastModified(String remoteFile) throws IOException,
			FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			sendCmd("MDTM " + remoteFile, new int[] { 213 });
			String r = getReply().substring(4).trim();
			SimpleDateFormat utcFormater = new SimpleDateFormat(
					"yyyyMMddHHmmss");
			utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date d;
			try {
				d = utcFormater.parse(r);
			} catch (ParseException e) {
				throw new IOException(e);
			}
			return d;
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 获取文件大小
	 * 
	 * @param remoteFile
	 *            新的工作路径,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public long getFileSize(String remoteFile) throws IOException,
			FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			sendCmd("SIZE " + remoteFile, new int[] { 213 });
			return Long.valueOf(getReply().substring(4).trim());
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 获取文件大小和时间
	 * 
	 * @param remoteFile
	 *            新的工作路径,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @return 长型整数数组，第0个元素表示时间，第1个元素表示大小
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public long[] getLastModifiedAndSize(String remoteFile) throws IOException,
			FtpReplyError {
		long[] r = new long[2];
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			sendCmd("MDTM " + remoteFile, new int[] { 213 });
			SimpleDateFormat utcFormater = new SimpleDateFormat(
					"yyyyMMddHHmmss");
			utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date d;
			try {
				d = utcFormater.parse(getReply().substring(4).trim());
			} catch (ParseException e) {
				throw new IOException(e);
			}
			r[0] = d.getTime();
			sendCmd("SIZE " + remoteFile, new int[] { 213 });
			r[1] = Long.valueOf(getReply().substring(4).trim());
			return r;
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 删除文件或目录
	 * 
	 * @param remoteFile
	 *            新的工作路径,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param isDirectory
	 *            是否是目录
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void delete(String remoteFile, boolean isDirectory)
			throws IOException, FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			if (!isDirectory)
				sendCmd("DELE " + remoteFile, new int[] { 250 });
			else
				sendCmd("RMD " + remoteFile, new int[] { 250 });
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 获取当前目录
	 * 
	 * @throws IOException
	 *             通讯异常时招聘
	 * @throws FtpReplyError
	 */
	public String pwd() throws IOException, FtpReplyError {
		sendCmd("PWD", new int[] { 257 });
		String r = getReply().substring(5).trim();
		int index = r.indexOf("\"");
		return r.substring(0, index);
	}

	/**
	 * 创建目录
	 * 
	 * @param remoteFile
	 *            目录文件名,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param isDirectory
	 *            是否是目录
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void mkdir(String remoteFile) throws IOException, FtpReplyError {
		remoteFile = remoteFile.trim();
		sendCmd("MKD " + remoteFile, new int[] { 257 });
	}

	/**
	 * 发一个NOOP指令至服务端，收到表示链路畅通
	 * 
	 * @throws IOException
	 *             通讯异常时招聘
	 * @throws FtpReplyError
	 */
	public void noop() throws IOException, FtpReplyError {
		sendCmd("NOOP", new int[] { 200 });
	}

	/**
	 * 在同一路径下重命名
	 * 
	 * @param from
	 *            源文件,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param to
	 *            目录文件名，不包含目录
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void rename(String from, String to) throws IOException,
			FtpReplyError {
		// from = from.trim();
		// if (!from.startsWith("/"))
		// from = "/" + from;
		// to = to.trim();
		// if (!to.startsWith("/"))
		// to = "/" + to;
		// from = switchWorkingDirectoryToFile(from, false);
		try {
			sendCmd("RNFR " + from, new int[] { 350 });
			sendCmd("RNTO " + to, new int[] { 250 });
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 至上层目录
	 * 
	 * @throws IOException
	 *             通讯异常时招聘
	 * @throws FtpReplyError
	 */
	public void changeToUpDirectory() throws IOException, FtpReplyError {
		sendCmd("CDUP", new int[] { 250 });
	}

	/**
	 * 下载文件
	 * 
	 * @param remoteFile
	 *            要下载的远程文件名,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param stream
	 *            下载后保存的输出流
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void download(String remoteFile, OutputStream stream)
			throws IOException, FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, false);
		try {
			dataComm("retr " + remoteFile, null, new DataWriteStream(stream,
					dataTimeout));
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 上传文件
	 * 
	 * @param remoteFile
	 *            要保存的远程文件名,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param stream
	 *            需要上传的输入流
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void upload(String remoteFile, InputStream stream)
			throws IOException, FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, true);
		try {
			dataComm("stor " + remoteFile, new DataReadStream(stream,
					dataTimeout), null);
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 上传文件，添加至已有文件末尾
	 * 
	 * @param remoteFile
	 *            要保存的远程文件名,用/开始的文件名表示绝对路径，否则表示相对路径
	 * @param stream
	 *            需要上传的输入流
	 * @throws FtpReplyError
	 *             当文件或目录不存在抛出
	 * @throws IOException
	 *             通讯异常时招聘
	 */
	public void append(String remoteFile, InputStream stream)
			throws IOException, FtpReplyError {
		remoteFile = switchWorkingDirectoryToFile(remoteFile, true);
		try {
			dataComm("appe " + remoteFile, new DataReadStream(stream,
					dataTimeout), null);
		} finally {
			restoryWorkingDirectory();
		}
	}

	/**
	 * 创建目录，如果存在则不创建，如果父目录不存在，则一并创建
	 * 
	 * @param remotePath
	 *            从根目录开始的目录完整路径
	 * @throws IOException
	 * @throws FtpReplyError
	 */
	public void mkdirs(String remotePath) throws IOException, FtpReplyError {
		ArrayList<String> s = new ArrayList<String>();
		String cdir = pwd();
		boolean changed = false;
		try {
			while (!remotePath.isEmpty()) {
				try {
					changeWorkingDirectory(remotePath);
					changed = true;
					break;
				} catch (FtpReplyError e) {
				}
				int index = remotePath.lastIndexOf("/");
				if (index > -1) {
					s.add(0, remotePath.substring(index + 1));
					remotePath = remotePath.substring(0, index);
				} else {
					s.add(remotePath);
					remotePath = "";
					break;
				}
			}
			for (String str : s) {
				remotePath += "/" + str;
				mkdir(remotePath);
			}
		} finally {
			if (changed)
				changeWorkingDirectory(cdir);
		}
	}

	/**
	 * 获取FTP登录用户
	 */
	public String getUser() {
		return user;
	}

	/**
	 * 设置FTP登录用户
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * 获取FTP登录密码
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * 设置FTP登录密码
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public static void main(String[] args) {
		try {
			FtpConnection con = new FtpConnection(null, new InetSocketAddress(
					"localhost", 21), 60000, 30000);
			con.setUser("zhaom");
			con.setPwd("zhaom");
			con.open();
			try {
				con.changeWorkingDirectory("1");
				con.mkdirs("/1/new/aaaa/bbbb");
				con.changeWorkingDirectory("new");
				System.out.println(con.pwd());
				con.getFileSize("/1/new/aaaa/b.txt");
				con.mkdirs("/1/new/aaaa/bbbb");
				System.out.println(con.pwd());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				stream.write("asdfasdfsadfsadf".getBytes());
				ByteArrayInputStream in = new ByteArrayInputStream(
						stream.toByteArray());
				con.upload("1/new/aaaa/a.txt", in);
				con.rename("1/new/aaaa/a.txt", "b.txt");
				for (FtpFile o : con.listFiles(""))
					System.out.println(o.getFileName() + "," + o.getSize()
							+ "," + o.getLastModified());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				con.download("电子渠道.txt", out);
				System.out.println(con.getFileSize("1/new/aaaa/b.txt"));
				System.out.println(new String(out.toByteArray()));
			} finally {
				con.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keepAlive() throws DataException {
		try {
			noop();
		} catch (Throwable e) {
			throw new DataException(e);
		}
	}

}
