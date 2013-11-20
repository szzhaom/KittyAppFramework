package kitty.kaf.watch;

/**
 * 监视任务
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public interface WatchTask {
	/**
	 * 监控
	 */
	public void watch() throws InterruptedException;
}
