package kitty.kaf.dao.tools;

import japa.parser.ast.expr.MethodCallExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kitty.kaf.dao.tools.datatypes.ColumnDataType;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 枚举定义
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class EnumDef {
	List<EnumItemDef> enumItems = new ArrayList<EnumItemDef>();
	String packageName;
	String name;
	String desp;
	ColumnDataType dataType;

	public EnumDef(Element el) throws SQLException {
		NodeList ls = el.getElementsByTagName("enum");
		for (int i = 0; i < ls.getLength(); i++) {
			enumItems.add(new EnumItemDef((Element) ls.item(i)));
		}
		name = el.getAttribute("name");
		packageName = el.getAttribute("package");
		desp = el.getAttribute("desp");
		dataType = ColumnDataType.getColumnDataType(null, el
				.getAttribute("type"),
				el.hasAttribute("classname") ? el.getAttribute("classname")
						: null);
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<EnumItemDef> getEnumItems() {
		return enumItems;
	}

	public void setEnumItems(List<EnumItemDef> enumItems) {
		this.enumItems = enumItems;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void generateReadFromStreamCode(MethodCallExpr mce) {
		dataType.generateReadFromStreamCode(mce);
	}

	public MethodCallExpr generateWriteToStreamCode(MethodCallExpr mce) {
		return dataType.generateWriteToStreamCode(mce);
	}

}
