package kitty.kaf.logging;

/**
 * 日志接口
 * 
 * @author 赵明
 * @version 1.0
 */

public interface Logger {

	public void debug(Object message);

	public void debug(Object message, Throwable t);

	public void error(Object message);

	public void error(Object message, Throwable t);

	public void info(Object message);

	public void info(Object message, Throwable t);

	public void warn(Object message);

	public void warn(Object message, Throwable t);

	public void trace(Object message);

	public void trace(Object message, Throwable t);

	public void fatal(Object message);

	public void fatal(Object message, Throwable t);

	public boolean isDebugEnabled();

	public boolean isInfoEnabled();

	public boolean isTraceEnabled();

}
