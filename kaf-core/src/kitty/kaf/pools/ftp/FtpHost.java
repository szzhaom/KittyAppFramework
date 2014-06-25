package kitty.kaf.pools.ftp;

/**
 * FTP主机配置
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class FtpHost {
	/**
	 * 配置名称
	 */
	String name;
	/**
	 * FTP主机名
	 */
	String host;
	/**
	 * FTP端口
	 */
	int port;
	/**
	 * FTP登录用户名
	 */
	String user;
	/**
	 * FTP登录密码
	 */
	String password;
	/**
	 * FTP根目录
	 */
	String ftpRoot;
	/**
	 * 字符集
	 */
	String charset;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFtpRoot() {
		return ftpRoot;
	}

	public void setFtpRoot(String ftpRoot) {
		this.ftpRoot = ftpRoot;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
