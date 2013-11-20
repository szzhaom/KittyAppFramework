package kitty.kaf.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import kitty.kaf.logging.KafLogger;
import kitty.kaf.util.DateTime;

/**
 * 执行数据库操作的Bean
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoBean {
	final static KafLogger logger = KafLogger.getLogger(DaoBean.class);
	private Dao dao;
	private HashMap<String, Dao> otherDaos = null;

	public DaoBean() {
		super();
	}

	/**
	 * 获取默认的Dao对象，即名称为default的Dao对象，该对象会自动释放
	 * 
	 * @return Dao对象
	 * @throws SQLException
	 */
	public Dao getDao() throws SQLException {
		if (dao == null) {
			dao = new Dao("default");
		}
		dao.setAutoCommit(false);
		return dao;
	}

	public Dao getDao(String name) throws SQLException {
		if (name == null || "default".equals(name))
			return getDao();
		else {
			if (otherDaos == null)
				otherDaos = new HashMap<String, Dao>();
			Dao dao = otherDaos.get(name);
			if (dao == null) {
				dao = new Dao(name);
				otherDaos.put(name, dao);
			}
			return dao;
		}
	}

	public void closeDaos(boolean commit, boolean rollback) {
		if (dao != null) {
			if (commit) {
				try {
					dao.commit();
				} catch (Throwable e) {
				}
			}
			if (rollback) {
				try {
					dao.rollback();
				} catch (Throwable e) {
				}
			}
			try {
				dao.close();
			} catch (Throwable e) {
			}
			dao = null;
		}
		if (otherDaos != null) {
			Collection<Dao> c = otherDaos.values();
			for (Dao d : c) {
				if (commit) {
					try {
						d.commit();
					} catch (Throwable e) {
					}
				}
				if (rollback) {
					try {
						d.rollback();
					} catch (Throwable e) {
					}
				}
				try {
					d.close();
				} catch (Throwable e) {
				}
			}
			otherDaos.clear();
			otherDaos = null;
		}
	}

	/**
	 * 拦截器，处理数据库的事务，并关闭数据库资源
	 * 
	 * @param ic
	 *            截取程序
	 * @return 处理结果
	 * @throws Exception
	 */
	@AroundInvoke
	public Object daoInterceptor(InvocationContext ic) throws Exception {
		long startTime = System.currentTimeMillis();
		try {
			closeDaos(false, false);
			Object ret = ic.proceed();
			closeDaos(true, false);
			return ret;
		} catch (Exception e) {
			closeDaos(false, true);
			throw e;
		} catch (Throwable e) {
			closeDaos(false, true);
			throw new Exception(e);
		} finally {
			if (logger.isDebugEnabled())
				logger.debug(ic.getMethod().getName()
						+ " called. [runinterval="
						+ DateTime.milliSecondsBetween(startTime,
								System.currentTimeMillis()) + "]");
		}
	}

}
