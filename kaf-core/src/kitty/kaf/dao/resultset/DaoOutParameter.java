package kitty.kaf.dao.resultset;

/**
 * 数据库输出参数
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoOutParameter extends DaoParameter {
	private static final long serialVersionUID = 1L;
	Integer scale;
	String typeName;

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
