package kitty.kaf.dao.tools;

import kitty.kaf.dao.source.DaoSource;

/**
 * 数据库表的基础配置定义
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class BaseConfigDef {
	boolean isNeedAdded = true;
	boolean isNeedDeleted = false;
	boolean isNeedModified = false;
	DaoSource daoSource;

	public BaseConfigDef() {
		super();
	}

	public BaseConfigDef(DaoSource daoSource) {
		super();
		this.daoSource = daoSource;
	}

	public boolean isNeedAdded() {
		return isNeedAdded;
	}

	public void setNeedAdded(boolean isNeedAdded) {
		this.isNeedAdded = isNeedAdded;
	}

	public boolean isNeedDeleted() {
		return isNeedDeleted;
	}

	public void setNeedDeleted(boolean isNeedDeleted) {
		this.isNeedDeleted = isNeedDeleted;
	}

	public boolean isNeedModified() {
		return isNeedModified;
	}

	public void setNeedModified(boolean isNeedModified) {
		this.isNeedModified = isNeedModified;
	}

	public DaoSource getDaoSource() {
		return daoSource;
	}

	public void setDaoSource(DaoSource daoSource) {
		this.daoSource = daoSource;
	}

}
