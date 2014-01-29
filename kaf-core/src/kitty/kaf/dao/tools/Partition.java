package kitty.kaf.dao.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitty.kaf.dao.source.DaoSource;
import kitty.kaf.dao.tools.datatypes.DateColumnDataType;
import kitty.kaf.dao.tools.datatypes.StringColumnDataType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Partition extends BaseConfigDef {
	String type, columns;
	Table table;
	Map<String, PartitionItem> itemsMap = new HashMap<String, PartitionItem>();
	String func;

	/**
	 * 从XML Element中读取分区配置
	 * 
	 * @param el
	 *            分区配置element
	 */
	public Partition(Element el, Table table) {
		this.table = table;
		this.daoSource = table.daoSource;
		type = el.getAttribute("type");
		columns = el.getAttribute("columns");
		func = el.hasAttribute("func") ? null : el.getAttribute("func").trim();
		if (func != null && func.isEmpty())
			func = null;
		NodeList ls = el.getElementsByTagName("item");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			PartitionItem item = new PartitionItem();
			item.name = e.getAttribute("name");
			item.value = e.getAttribute("value");
			item.setTable(table);
			itemsMap.put(item.name, item);
		}
	}

	public Partition() {
		super();
	}

	public Partition(DaoSource daoSource) {
		super(daoSource);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Partition [type=" + type + ", columns=" + columns + "]";
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
		this.daoSource = table.daoSource;
		for (PartitionItem o : itemsMap.values())
			o.setTable(table);
	}

	public Map<String, PartitionItem> getItemsMap() {
		return itemsMap;
	}

	public void setItemsMap(Map<String, PartitionItem> map) {
		this.itemsMap = map;
	}

	public String getCreateSql() {
		List<PartitionItem> ls = new ArrayList<PartitionItem>();
		ls.addAll(itemsMap.values());
		Collections.sort(ls);
		StringBuffer sb = new StringBuffer();
		String c = columns;
		if (func != null)
			c = func + "(" + columns + ")";
		Column column = table.findColumnByName(columns);
		sb.append("partition by " + type + " columns (" + c + ")(\r\n");
		boolean isFirst = true;
		for (PartitionItem o : ls) {
			String value = o.getValue();
			if (column.getDataType() instanceof DateColumnDataType
					|| column.getDataType() instanceof StringColumnDataType)
				value = "'" + value + "'";
			if (isFirst)
				isFirst = false;
			else
				sb.append(",\r\n");
			sb.append("    partition " + o.getName() + " values less than (" + value + ")");
		}
		sb.append("\r\n)");
		return sb.toString();
	}

	public String getModifySql() {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		List<PartitionItem> ls = new ArrayList<PartitionItem>();
		ls.addAll(itemsMap.values());
		Collections.sort(ls);
		Column column = table.findColumnByName(columns);
		for (PartitionItem o : ls) {
			if (o.isNeedAdded()) {
				String value = o.getValue();
				if (column.getDataType() instanceof DateColumnDataType
						|| column.getDataType() instanceof StringColumnDataType)
					value = "'" + value + "'";
				if (isFirst) {
					isFirst = false;
					sb.append("alter table " + this.table.getName() + " add partition(\r\n");
				} else
					sb.append(",\r\n");
				sb.append("    partition " + o.getName() + " values less than (" + value + ")");
			}
		}
		if (!isFirst)
			sb.append("\r\n);\r\n");
		return sb.toString();
	}
}
