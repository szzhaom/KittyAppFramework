package kitty.kaf.dao.tools.cg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitty.kaf.helper.StringHelper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RightDef {
	String tableName;
	String fields;
	List<String> rows = new ArrayList<String>();
	String className;
	String packageName;
	Map<String, Long> rightMap = new HashMap<String, Long>();

	public RightDef(Element el) {
		tableName = el.getAttribute("table_name");
		fields = el.getAttribute("fields");
		packageName = el.getAttribute("package");
		className = el.getAttribute("class");
		NodeList ls = el.getElementsByTagName("row");
		for (int i = 0; i < ls.getLength(); i++) {
			Element e = (Element) ls.item(i);
			String v = e.getAttribute("values");
			rows.add(v);
			String s[] = StringHelper.splitToStringArray(v, ",");
			String n = s[1].trim();
			n = n.substring(1, n.length() - 1);
			rightMap.put(n.trim(), Long.valueOf(s[0].trim()));
		}
	}

	public String getTableName() {
		return tableName;
	}

	public String getFields() {
		return fields;
	}

	public List<String> getRows() {
		return rows;
	}

	public String getClassName() {
		return className;
	}

	public String getPackageName() {
		return packageName;
	}

	public Map<String, Long> getRightMap() {
		return rightMap;
	}
}
