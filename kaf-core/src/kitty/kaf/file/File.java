package kitty.kaf.file;

import java.util.Date;

/**
 * 文件属性类
 * 
 * @author 赵明
 * @version 1.0
 */
public class File {
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 最后修改时间
	 */
	private Date lastModified;
	/**
	 * 文件长度
	 */
	private long length;
	/**
	 * 是否是目录
	 */
	boolean isDirectory;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

}
