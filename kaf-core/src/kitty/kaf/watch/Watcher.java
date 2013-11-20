package kitty.kaf.watch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import kitty.kaf.logging.KafLogger;

/**
 * 守护程序
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 */
public class Watcher implements Runnable {
	CopyOnWriteArrayList<WatchTask> tasks = new CopyOnWriteArrayList<WatchTask>();
	private volatile int interval;
	static KafLogger logger = KafLogger.getLogger(Watcher.class);
	String name;

	/**
	 * 创建一个守护者
	 * 
	 * @param interval
	 *            守护时间间隔
	 */
	public Watcher(String name, int interval) {
		super();
		this.interval = interval;
		this.name = name;
	}

	@Override
	public void run() {
		List<WatchTask> ls = new ArrayList<WatchTask>();
		try {
			while (!Thread.interrupted()) {
				ls.clear();
				ls.addAll(tasks);
				for (WatchTask o : ls) {
					try {
						o.watch();
					} catch (InterruptedException e) {
						throw e;
					} catch (Throwable e) {
					}
				}
				Thread.sleep(interval);
			}
		} catch (Throwable e) {
			logger.error("watcher[name=" + name + "] exited:", e);
		}
		ls.clear();
		ls = null;
	}

	/**
	 * 获取守护间隔时间
	 */
	public int getInterval() {
		return interval;
	}

	/**
	 * 添加一个守护任务
	 * 
	 * @param task
	 *            守护任务
	 */
	public void addTask(WatchTask task) {
		tasks.add(task);
	}

	/**
	 * 移除一个守护任务
	 * 
	 * @param task
	 *            守护任务
	 */
	public void removeTask(WatchTask task) {
		tasks.remove(task);
	}
}
