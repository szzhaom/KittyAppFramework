package kitty.kaf.file;

/**
 * 文件主机配置接口。
 * 
 * @author 赵明
 * @version 1.0
 */
public interface IFileHost {
	/**
	 * 
	 * @return 配置名称
	 */
	public String getConfigName();

	/**
	 * @return 文件主机类型
	 */
	public String getType();

	/**
	 * 
	 * @return WEB根目录
	 */
	public String getWebRoot();
}
