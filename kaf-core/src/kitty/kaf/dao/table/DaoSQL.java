package kitty.kaf.dao.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 与DAO有关的SQL
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class DaoSQL implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sql;
	private List<String> valueColumns = new CopyOnWriteArrayList<String>();

	public DaoSQL(String sql, List<String> valueColumns) {
		super();
		this.sql = sql;
		this.valueColumns = valueColumns;
	}

	/**
	 * 获取SQL的执行参数
	 * 
	 * @param o
	 *            与此参数相关联的TableObject对象
	 * @return 参数列表
	 */
	public <E extends TableObject> List<Object> getParams(E o) {
		List<Object> params = new ArrayList<Object>();
		for (String d : valueColumns) {
			params.add(o.getByColumn(d));
		}
		return params;
	}

	/**
	 * 获取的SQL
	 */
	public String getSql() {
		return sql;
	}

	public List<String> getValueColumns() {
		return valueColumns;
	}
}
