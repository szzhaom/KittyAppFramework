package kitty.kaf.dao.tools;

import java.io.Serializable;

import kitty.kaf.dao.tools.datatypes.DateColumnDataType;
import kitty.kaf.dao.tools.datatypes.StringColumnDataType;
import kitty.kaf.util.DateTime;

/**
 * 分区项
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class PartitionItem extends BaseConfigDef implements Serializable, Comparable<PartitionItem> {
	private static final long serialVersionUID = 1L;
	String name, value;
	Table table;

	public PartitionItem() {
		super();
	}

	public PartitionItem(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
		this.daoSource = table.daoSource;
	}

	@Override
	public String toString() {
		return "PartitionItem [name=" + name + ", value=" + value + "]";
	}

	@Override
	public int compareTo(PartitionItem o) {
		if (table.partition.getType().equalsIgnoreCase("RANGE")) {
			Column column = table.findColumnByName(table.partition.columns);
			if (column.getDataType() instanceof DateColumnDataType)
				return DateTime.parseDate(this.value, "yyyy-MM-dd")
						.compareTo(DateTime.parseDate(o.value, "yyyy-MM-dd"));
			else if (!(column.getDataType() instanceof StringColumnDataType))
				return Long.valueOf(this.value).compareTo(Long.valueOf(o.value));
		}
		return this.value.compareTo(o.value);
	}

}
