package kitty.kaf.logging;

import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 日志记录器，基于log4j
 * 
 * @author 赵明
 * 
 */
public class Log4jLogger implements LoggerInf {
	org.apache.log4j.Logger logger;
	static final boolean configed;
	static {
		String path = System.getenv("APP_RUN_HOME");
		if (path != null && !path.isEmpty()) {
			DOMConfigurator.configure(path + "/config/log4j.xml");
			configed = true;
		} else {
			System.err.println("【APP_RUN_HOME】未配置");
			configed = false;
		}
	}

	public static Log4jLogger getLogger(Class<?> clazz) {
		return new Log4jLogger(org.apache.log4j.Logger.getLogger(clazz));
	}

	public static Log4jLogger getLogger(String name) {
		return new Log4jLogger(org.apache.log4j.Logger.getLogger(name));
	}

	public static Log4jLogger getLogger(String name, LoggerFactory factory) {
		return new Log4jLogger(org.apache.log4j.Logger.getLogger(name, factory));
	}

	public Log4jLogger(org.apache.log4j.Logger logger) {
		super();
		this.logger = logger;
	}

	public void debug(Object message) {
		this.logger.debug(message);
	}

	public void debug(Object message, Throwable t) {
		this.logger.debug(message, t);
	}

	public void error(Object message) {
		this.logger.error(message);
	}

	public void error(Object message, Throwable t) {
		this.logger.error(message, t);
	}

	public void info(Object message) {
		this.logger.info(message);
	}

	public void info(Object message, Throwable t) {
		this.logger.info(message, t);
	}

	public void warn(Object message) {
		this.logger.warn(message);
	}

	public void warn(Object message, Throwable t) {
		this.logger.warn(message, t);
	}

	public void trace(Object message) {
		this.logger.trace(message);
	}

	public void trace(Object message, Throwable t) {
		this.logger.trace(message, t);
	}

	public void fatal(Object message) {
		this.logger.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		this.logger.fatal(message, t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

}
