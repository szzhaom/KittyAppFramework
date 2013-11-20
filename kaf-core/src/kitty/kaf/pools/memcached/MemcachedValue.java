package kitty.kaf.pools.memcached;

import java.security.NoSuchAlgorithmException;

import kitty.kaf.helper.SecurityHelper;

public class MemcachedValue {
	/**
	 * 键值
	 */
	private byte[] value;
	/**
	 * 键操作标记
	 */
	private int flags;
	private String md5;

	public MemcachedValue() {
		super();
	}

	public MemcachedValue(byte[] value) {
		super();
		this.value = value;
	}

	public MemcachedValue(byte[] value, int flags) {
		super();
		this.value = value;
		this.flags = flags;
	}

	public MemcachedValue(byte[] value, int flags, String md5) {
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

	public String getMd5() throws MemcachedException {
		if (md5 == null)
			try {
				md5 = SecurityHelper.md5(value);
			} catch (NoSuchAlgorithmException e) {
				throw new MemcachedException(e);
			}
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
}
