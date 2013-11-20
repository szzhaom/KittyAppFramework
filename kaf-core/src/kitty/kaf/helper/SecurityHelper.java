/*
 * DataUtil.java
 * 
 * Created on 2007-11-17, 14:24:21
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kitty.kaf.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 安全相关的助手程序
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class SecurityHelper {
	static byte[] mainKey = StringHelper.hexToBytes("A30D1C50E4893CA5");

	/**
	 * 生成MD5，获取低8位
	 * 
	 * @param data
	 * @return
	 */
	public static byte[] checksum(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			return md.digest();
		} catch (Throwable e) {
			return new byte[16];
		}
	}

	/**
	 * 生成md5摘要
	 * 
	 * @param input
	 *            byte[] 原始数据
	 * @return byte[] md5摘要
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	public static String md5(byte[] data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data);
		return StringHelper.bytesToHex(md.digest());
	}

	/**
	 * 生成md5摘要
	 * 
	 * @param input
	 *            byte[] 原始数据
	 * @return byte[] md5摘要
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	public static String md5(byte[] data, int offset, int len)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data, offset, len);
		return StringHelper.bytesToHex(md.digest());
	}

	/**
	 * 生成md5摘要
	 * 
	 * @param input
	 *            byte[] 原始数据
	 * @return byte[] md5摘要
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	public static String md5(String data) throws NoSuchAlgorithmException {
		return md5(data.getBytes());
	}

	/**
	 * DES 加密
	 * 
	 * @param keyPrefix
	 *            byte[] 8位密钥
	 * @param value
	 *            byte[] 要加密的数据
	 * @return byte[] 加密后的数据
	 * @throws Exception
	 */
	static String DES = "DES/ECB/NoPadding";
	static String TriDes = "DESede/ECB/NoPadding";

	public static byte[] generateDesKey() throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance("DES");
		SecretKey deskey = keygen.generateKey();
		return deskey.getEncoded();
	}

	/**
	 * DES加密
	 * 
	 * @param key
	 *            8位密钥
	 * @param data
	 *            要加密的数据
	 * @return 加密后的数据
	 * @throws Exception
	 *             如果解密失败
	 */
	public static byte[] desEncrypt(byte[] key, byte[] data) throws Exception {
		KeySpec ks = new DESKeySpec(key);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		SecretKey ky = kf.generateSecret(ks);
		int m = 8 - data.length % 8;
		if (m != 8) {
			byte[] d = data;
			data = new byte[data.length + m];
			System.arraycopy(d, 0, data, 0, d.length);
			for (int i = 0; i < m; i++)
				data[i + d.length] = 0;
		}
		Cipher c = Cipher.getInstance(DES);
		c.init(Cipher.ENCRYPT_MODE, ky);
		return c.doFinal(data);
	}

	/**
	 * DES 解密
	 * 
	 * @param key
	 *            byte[] 8位密钥
	 * @param data
	 *            byte[] 要解密的数据
	 * @return byte[] 解密后的数据
	 * @throws Exception
	 *             如果解密失败
	 */
	public static byte[] desDecrypt(byte[] key, byte[] data) throws Exception {
		KeySpec ks = new DESKeySpec(key);
		SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
		SecretKey ky = kf.generateSecret(ks);

		Cipher c = Cipher.getInstance(DES);
		c.init(Cipher.DECRYPT_MODE, ky);
		return c.doFinal(data);
	}

	/**
	 * 3DES 加密
	 * 
	 * @param key
	 *            byte[] 24位密钥
	 * @param data
	 *            byte[] 要加密的数据
	 * @return byte[] 加密后的数据
	 * @throws Exception
	 *             如果加密失败
	 */
	public static byte[] des3Encrypt(byte[] key, byte[] data) throws Exception {
		SecretKeySpec deskey = new SecretKeySpec(key, "DESede");
		Cipher c1 = Cipher.getInstance("DESede");
		c1.init(Cipher.ENCRYPT_MODE, deskey);
		return c1.doFinal(data);
	}

	/**
	 * 3DES 解密
	 * 
	 * @param key
	 *            byte[] 24位密钥
	 * @param data
	 *            byte[] 要解密的数据
	 * @return byte[] 争密后的数据
	 * @throws Exception
	 *             如果解密失败
	 */
	public static byte[] des3Decrypt(byte[] key, byte[] data) throws Exception {
		SecretKeySpec deskey = new SecretKeySpec(key, "DESede");
		Cipher c1 = Cipher.getInstance("DESede");
		c1.init(Cipher.DECRYPT_MODE, deskey);
		return c1.doFinal(data);
	}

	/**
	 * zip压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @param out
	 *            - 输出数据流
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public void zipCompress(byte[] in_buffer, int offset, int len,
			OutputStream out) throws IOException {
		DeflaterOutputStream zipos = new DeflaterOutputStream(out);
		try {
			if (offset < 0)
				offset = 0;
			if (len == 0)
				len = in_buffer.length - offset;
			zipos.write(in_buffer, offset, len);
			zipos.finish();
			zipos.close();
		} finally {
			try {
				zipos.finish();
			} catch (Exception ee) {
			}
			try {
				zipos.close();
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * 压缩数据
	 * 
	 * @param in
	 *            - 输入数据流
	 * @param out
	 *            - 输出数据流
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public void zipCompress(InputStream in, OutputStream out)
			throws IOException {
		DeflaterOutputStream zipos = new DeflaterOutputStream(out);
		try {
			byte[] b = new byte[4096];
			int count;
			while ((count = in.read(b)) > 0)
				zipos.write(b, 0, count);
			zipos.finish();
			zipos.close();
		} finally {
			try {
				zipos.finish();
			} catch (Exception ee) {
			}
			try {
				zipos.close();
			} catch (Exception ee) {
			}
		}
	}

	/**
	 * 压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @return 输出数据
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipCompress(byte[] in_buffer, int offset, int len)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		zipCompress(in_buffer, offset, len, out);
		return out.toByteArray();
	}

	/**
	 * 压缩数据，压缩数据的前4位为未压缩数据的长度
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @return 输出数据
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipCompressIncludeUncompressLen(byte[] in_buffer,
			int offset, int len) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		zipCompress(in_buffer, offset, len, out);
		byte[] cb = out.toByteArray();
		byte[] b = new byte[cb.length + 4];
		System.arraycopy(BytesHelper.intToBytes(len), 0, b, 0, 4);
		System.arraycopy(cb, 0, b, 4, cb.length);
		return b;
	}

	/**
	 * 压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @return 输出数据
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipCompress(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		zipCompress(in, out);
		return out.toByteArray();
	}

	/**
	 * 解压缩数据
	 * 
	 * @param in
	 *            - 输入数据流
	 * @param out
	 *            - 输出数据流
	 * @throws IOException
	 *             - 压缩失败时抛出
	 */
	static public void zipDecompress(InputStream in, OutputStream out)
			throws IOException {
		InflaterInputStream zipos = new InflaterInputStream(in);
		byte[] b = new byte[4096];
		try {
			int count;
			while ((count = zipos.read(b)) > 0) {
				out.write(b, 0, count);
			}
		} finally {
			zipos.close();
		}
	}

	/**
	 * 解压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @param out
	 *            - 输出数据流
	 * @throws Exception
	 *             - 压缩失败时抛出
	 */
	static public void zipDecompress(byte[] in_buffer, int offset, int len,
			OutputStream out) throws IOException {
		if (offset <= 0)
			offset = 0;
		if (len == 0)
			len = in_buffer.length - offset;
		zipDecompress(new ByteArrayInputStream(in_buffer, offset,
				in_buffer.length - offset), out);
	}

	/**
	 * 解压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @return 输出数据
	 * @throws Exception
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipDecompress(byte[] in_buffer, int offset, int len)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		zipDecompress(in_buffer, offset, len, out);
		return out.toByteArray();
	}

	/**
	 * 解压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @param offset
	 *            - 输入数据偏移量
	 * @param len
	 *            - 输入数据长度
	 * @return 输出数据
	 * @throws Exception
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipDecompressIncludeUncompressLen(byte[] in_buffer,
			int offset, int len) throws IOException {
		int b = BytesHelper.bytesToInt(in_buffer, offset);
		ByteArrayOutputStream out = new ByteArrayOutputStream(b);
		zipDecompress(in_buffer, offset + 4, len - 4, out);
		return out.toByteArray();
	}

	/**
	 * 解压缩数据
	 * 
	 * @param in_buffer
	 *            - 输入数据
	 * @return 输出数据
	 * @throws Exception
	 *             - 压缩失败时抛出
	 */
	static public byte[] zipDecompress(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		zipDecompress(in, out);
		return out.toByteArray();
	}

	/**
	 * 压缩文件
	 * 
	 * @param files
	 *            要压缩的文件列表
	 * @param rootDir
	 *            根目录
	 * @param out
	 *            输入的ZIP文件
	 * @throws IOException
	 *             如果处理失败
	 */
	static public void zipCompress(String[] files, String rootDir,
			ZipOutputStream out) throws IOException {
		for (String fn : files) {
			File f = new File(fn);
			if (f.isFile()) {
				String path = f.getAbsolutePath().substring(rootDir.length());
				out.putNextEntry(new ZipEntry(path));
				FileInputStream in = new FileInputStream(f);
				byte[] b = new byte[4096];
				int len;
				while ((len = in.read(b)) != -1) {
					out.write(b, 0, len);
				}
				in.close();
			}
		}
	}

}
