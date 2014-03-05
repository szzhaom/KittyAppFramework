package kitty.kaf.logging;

import org.apache.log4j.spi.LoggerFactory;

/**
 * 系统日志记录器
 * 
 * @author 赵明
 * 
 */
public class SystemLogger implements LoggerInf {
	final String name;

	public static SystemLogger getLogger(Class<?> clazz) {
		return new SystemLogger(clazz.getName());
	}

	public static SystemLogger getLogger(String name) {
		return new SystemLogger(name);
	}

	public static SystemLogger getLogger(String name, LoggerFactory factory) {
		return new SystemLogger(name);
	}

	public SystemLogger(String name) {
		super();
		this.name = name;
	}

	public void debug(Object message) {
		System.out.println("Debug - " + name + " - " + message);
	}

	public void debug(Object message, Throwable t) {
		System.out.print("Debug - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.out);
	}

	public void error(Object message) {
		System.err.println("Error - " + name + " - " + message);
	}

	public void error(Object message, Throwable t) {
		System.err.print("Error - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.err);
	}

	public void info(Object message) {
		System.err.println("Info - " + name + " - " + message);
	}

	public void info(Object message, Throwable t) {
		System.err.print("Info - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.out);
	}

	public void warn(Object message) {
		System.err.println("Warn - " + name + " - " + message);
	}

	public void warn(Object message, Throwable t) {
		System.err.print("Warn - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.out);
	}

	public void trace(Object message) {
		System.err.print("Trace - " + name + " - " + message);
	}

	public void trace(Object message, Throwable t) {
		System.err.print("Trace - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.out);
	}

	public void fatal(Object message) {
		System.err.print("Fatal - " + name + " - " + message);
	}

	public void fatal(Object message, Throwable t) {
		System.err.print("Fatal - " + name + " - " + message);
		if (t != null)
			t.printStackTrace(System.out);
	}

	public boolean isDebugEnabled() {
		return true;
	}

	public boolean isInfoEnabled() {
		return true;
	}

	public boolean isTraceEnabled() {
		return true;
	}

}
