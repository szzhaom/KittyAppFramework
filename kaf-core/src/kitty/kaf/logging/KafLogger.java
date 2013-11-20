package kitty.kaf.logging;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * 日志记录器，基于log4j
 * 
 * @author 赵明
 * 
 */
public class KafLogger {
	Logger logger;
	static {
		String path = System.getenv("KAF_HOME");
		if (path != null && !path.isEmpty()) {
			DOMConfigurator.configure(path + "/config/log4j.xml");
		} else {
			System.err.println("【KAF_HOME】未配置");
		}
	}

	public static KafLogger getLogger(Class<?> clazz) {
		return new KafLogger(Logger.getLogger(clazz));
	}

	public static KafLogger getLogger(String name) {
		return new KafLogger(Logger.getLogger(name));
	}

	public static KafLogger getLogger(String name, LoggerFactory factory) {
		return new KafLogger(Logger.getLogger(name, factory));
	}

	public KafLogger(Logger logger) {
		super();
		this.logger = logger;
	}

	public void debug(Object message) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isDebugEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.debug(o);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.debug(o);
				} else
					logger.debug(msg);
			}
		} else
			this.logger.debug(message);
	}

	public void debug(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isDebugEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.debug(o);
					logger.debug("", t);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.debug(o);
					logger.debug("", t);
				} else
					logger.debug(msg, t);
			}
		} else
			this.logger.debug(message, t);
	}

	public void error(Object message) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.error(o);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.error(o);
			} else
				logger.error(msg);
		} else
			this.logger.error(message);
	}

	public void error(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.error(o);
				logger.error("", t);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.error(o);
				logger.error("", t);
			} else
				logger.error(msg, t);
		} else
			this.logger.error(message, t);
	}

	public void info(Object message) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isInfoEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.info(o);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.info(o);
				} else
					logger.info(msg);
			}
		} else
			this.logger.info(message);
	}

	public void info(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isInfoEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.info(o);
					logger.info("", t);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.info(o);
					logger.info("", t);
				} else
					logger.info(msg, t);
			}
		} else
			this.logger.info(message, t);
	}

	public void warn(Object message) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.warn(o);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.warn(o);
			} else
				logger.warn(msg);
		} else
			this.logger.warn(message);
	}

	public void warn(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.warn(o);
				logger.warn("", t);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.warn(o);
				logger.warn("", t);
			} else
				logger.warn(msg, t);
		} else
			this.logger.warn(message, t);
	}

	public void trace(Object message) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isTraceEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.trace(o);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.trace(o);
				} else
					logger.trace(msg);
			}
		} else
			this.logger.trace(message);
	}

	public void trace(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			if (logger.isTraceEnabled()) {
				Object msg = ((KafLoggerDataSource) message).getLogData();
				if (msg instanceof Object[]) {
					for (Object o : (Object[]) msg)
						logger.trace(o);
					logger.trace("", t);
				} else if (msg instanceof Collection<?>) {
					for (Object o : (Collection<?>) msg)
						logger.trace(o);
					logger.trace("", t);
				} else
					logger.trace(msg, t);
			}
		} else
			this.logger.trace(message, t);
	}

	public void fatal(Object message) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.fatal(o);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.fatal(o);
			} else
				logger.fatal(msg);
		} else
			this.logger.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		if (message instanceof KafLoggerDataSource) {
			Object msg = ((KafLoggerDataSource) message).getLogData();
			if (msg instanceof Object[]) {
				for (Object o : (Object[]) msg)
					logger.fatal(o);
				logger.fatal("", t);
			} else if (msg instanceof Collection<?>) {
				for (Object o : (Collection<?>) msg)
					logger.fatal(o);
				logger.fatal("", t);
			} else
				logger.fatal(msg, t);
		} else
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
