package kitty.kaf.dao.tools;

import java.io.Serializable;

/**
 * 分区项
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class PartitionItem extends BaseConfigDef implements Serializable,
		Comparable<PartitionItem> {
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
		if (table.partition.getType().equalsIgnoreCase("RANGE"))
			return Long.valueOf(this.value).compareTo(Long.valueOf(o.value));
		else
			return this.value.compareTo(o.value);
	}

}
