package kitty.kaf.cache.clients;

import java.security.NoSuchAlgorithmException;

import kitty.kaf.helper.SecurityHelper;

public class CacheBytesValue {
	/**
	 * 键值
	 */
	private byte[] value;
	/**
	 * 键操作标记
	 */
	private int flags;
	private String md5;

	public CacheBytesValue() {
		super();
	}

	public CacheBytesValue(byte[] value) {
		super();
		this.value = value;
	}

	public CacheBytesValue(byte[] value, int flags) {
		super();
		this.value = value;
		this.flags = flags;
	}

	public CacheBytesValue(byte[] value, int flags, String md5) {
		super();
		this.value = value;
		this.flags = flags;
		this.md5 = md5;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public String getMd5() throws NoSuchAlgorithmException {
		if (md5 == null)
			md5 = SecurityHelper.md5(value);
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
