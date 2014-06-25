package kitty.kaf.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import kitty.kaf.exceptions.UnsupportedConfigurationError;
import kitty.kaf.pools.ftp.FtpClient;

/**
 * 文件客户端，为系统提供标准的文件上传下载服务，后端可配置为FTP，阿里云OSS等
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public class FileClient implements IFileClient {
	/**
	 * 当前的文件客户端接口
	 */
	IFileClient fc;

	/**
	 * 根据文件客户端接口，构建文件客户端对象
	 * 
	 * @param fc
	 *            文件客户端接口
	 */
	public FileClient(IFileClient fc) {
		this.fc = fc;
	}

	/**
	 * 根据文件主机配置，构建文件客户端对象
	 * 
	 * @param fh
	 *            文件主机配置
	 * @throws IOException
	 *             如果连接失败
	 */
	public FileClient(IFileHost fh) throws IOException {
		if (fh.getType().equalsIgnoreCase("ftp")) {
			fc = new FtpClient(fh.getConfigName());
		} else
			throw new UnsupportedConfigurationError("Unsupported Type:" + fh.getType());
	}

	@Override
	public void upload(String remoteFile, InputStream stream) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		fc.upload(remoteFile, stream);
	}

	@Override
	public void append(String remoteFile, InputStream stream) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		fc.append(remoteFile, stream);
	}

	@Override
	public File getFileAttribute(String remoteFile) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		return fc.getFileAttribute(remoteFile);
	}

	@Override
	public List<File> listFiles(String remoteFile, String wildcards) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		return fc.listFiles(remoteFile, wildcards);
	}

	@Override
	public void close() {
		if (fc != null)
			fc.close();
	}

	@Override
	public void download(String remoteFile, OutputStream stream) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		fc.download(remoteFile, stream);
	}

	@Override
	public boolean delete(String remoteFile, boolean isDirectory) throws InterruptedException, IOException {
		if (fc == null)
			throw new NullPointerException("Has not been correctly initialized");
		return fc.delete(remoteFile, isDirectory);
	}
}
