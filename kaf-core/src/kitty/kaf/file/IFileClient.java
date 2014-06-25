package kitty.kaf.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 文件客户端接口，用于上传系统的文件。
 * 
 * @author 赵明
 * @version 1.0
 * 
 */
public interface IFileClient {
	/**
	 * 关闭客户端
	 */
	public void close();

	/**
	 * 上传文件，如果存在，则覆盖文件
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @param stream
	 *            数据流
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public void upload(String remoteFile, InputStream stream) throws InterruptedException, IOException;

	/**
	 * 往文件里添加内容，如果不存在，则创建文件
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @param stream
	 *            数据流
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public void append(String remoteFile, InputStream stream) throws InterruptedException, IOException;

	/**
	 * 下载文件，保存到流中
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @param stream
	 *            数据流
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public void download(String remoteFile, OutputStream stream) throws InterruptedException, IOException;

	/**
	 * 获取远程文件的属性
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @return 远程文件的属性
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public File getFileAttribute(String remoteFile) throws InterruptedException, IOException;

	/**
	 * 列出远程路径下的文件
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @param wildcards
	 *            通配符
	 * @return 找到的文件属性列表
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public List<File> listFiles(String remoteFile, String wildcards) throws InterruptedException, IOException;

	/**
	 * 删除远程文件
	 * 
	 * @param remoteFile
	 *            远程文件路径
	 * @param isDirectory
	 *            是否是目录
	 * @return 删除是否成功
	 * @throws InterruptedException
	 *             如果文件线程中断
	 * @throws IOException
	 *             如果通讯出现异常
	 */
	public boolean delete(String remoteFile, boolean isDirectory) throws InterruptedException, IOException;
}
