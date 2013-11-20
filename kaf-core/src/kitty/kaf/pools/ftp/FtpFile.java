package kitty.kaf.pools.ftp;

import java.util.Date;

import kitty.kaf.helper.StringHelper;

public class FtpFile {
	private String fileName;
	private boolean isDirectory;
	private long size;
	private Date lastModified;

	public FtpFile(String reply) {
		String[] s = StringHelper.splitBlank(reply.trim());
		fileName = s[s.length - 1];
		isDirectory = !s[0].startsWith("-");
		size = Long.valueOf(s[s.length - 5]);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isDirectory() {
		return isDirectory;
	}

	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
}
