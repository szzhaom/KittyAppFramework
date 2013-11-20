package kitty.kaf.dao.tools.cg;

import japa.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.Database;
import kitty.kaf.dao.tools.EnumDef;
import kitty.kaf.dao.tools.Table;
import kitty.kaf.dao.tools.cg.jsp.EditJspGenerator;
import kitty.kaf.dao.tools.cg.jsp.QueryJspGenerator;
import kitty.kaf.helper.StringHelper;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * 代码生成器
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class CodeGenerator {
	/**
	 * 代码生成的工作空间
	 */
	String workspaceDir, enumPackageName, infProjectName;
	Map<String, PackageDef> packageDefs = new HashMap<String, PackageDef>();
	Collection<Table> tables;
	Collection<EnumDef> enums;
	Map<String, TradeExecutorConfig> tradeExecutorConfigMap = new HashMap<String, TradeExecutorConfig>();
	Map<String, Long> rightMap = new HashMap<String, Long>();
	Map<String, TemplateDef> jspTemplateMap = new HashMap<String, TemplateDef>();
	protected Database database;
	Table rightTable;

	public CodeGenerator(Database database, Element root) {
		this.database = database;
		NodeList ls = root.getElementsByTagName("code_generator");
		if (ls.getLength() > 0) {
			Element el = (Element) ls.item(0);
			workspaceDir = el.getAttribute("work-space-dir");
			enumPackageName = el.getAttribute("enum-package-name");
			infProjectName = el.getAttribute("inf-project-name");
			ls = el.getElementsByTagName("package");
			for (int i = 0; i < ls.getLength(); i++) {
				el = (Element) ls.item(i);
				String name = el.getAttribute("name");
				packageDefs.put(name, new PackageDef(el));
			}
		}
		ls = root.getElementsByTagName("trade-executors");
		for (int i = 0; i < ls.getLength(); i++) {
			Element el = (Element) ls.item(i);
			NodeList ls1 = el.getElementsByTagName("trade-executor");
			for (int j = 0; j < ls1.getLength(); j++) {
				el = (Element) ls1.item(j);
				tradeExecutorConfigMap.put(el.getAttribute("name"), new TradeExecutorConfig(el));
			}
		}
		ls = root.getElementsByTagName("jsp-templates");
		for (int i = 0; i < ls.getLength(); i++) {
			Element el = (Element) ls.item(i);
			NodeList ls1 = el.getElementsByTagName("template");
			for (int j = 0; j < ls1.getLength(); j++) {
				el = (Element) ls1.item(j);
				jspTemplateMap.put(el.getAttribute("name"), new TemplateDef(el));
			}
		}
		this.tables = database.getTables().values();
		this.enums = database.getEnumDefs().values();
	}

	public String getWorkspaceDir() {
		return workspaceDir;
	}

	public void setWorkspaceDir(String workspaceDir) {
		this.workspaceDir = workspaceDir;
	}

	public PackageDef getPackageDef(String name) {
		return packageDefs.get(name);
	}

	public Map<String, TemplateDef> getJspTemplateMap() {
		return jspTemplateMap;
	}

	/**
	 * 生成代码
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void generator() throws IOException, ParseException {
		for (Table table : tables) {
			if (table.getTableData() != null && table.getTableData().getRightClass() != null) {
				parseRightMap(table.getTableData().getRows());
				break;
			}
		}
		for (Table table : tables) {
			if (table.getPackageName() == null || table.getPackageName().length() == 0)
				continue;
			ClassGenerator generator = new BeanClassGenerator(this, table);
			generator.generate();
			generator = new DaoHelperClassGenerator(this, table);
			generator.generate();
			generator = new BeanInterfaceGenerator(this, table);
			generator.generate();
			generator = new BeanHelperClassGenerator(this, table);
			generator.generate();
			generator = new EjbBeanClassGenerator(this, table);
			generator.generate();
			if (table.getTradeConfig() != null) {
				TradeExecutorConfig c = tradeExecutorConfigMap.get(table.getTradeConfig().getExecutorName());
				if (c != null) {
					List<Table> ls = c.getTables().get(table.getTradeConfig().getGroup());
					if (ls == null) {
						ls = new ArrayList<Table>();
						c.getTables().put(table.getTradeConfig().getGroup(), ls);
					}
					ls.add(table);
				}
			}
			if (table.getTableData() != null && table.getTableData().getRightClass() != null) {
				rightTable = table;
				generator = new RightClassGenerator(this, table);
				generator.generate();
			}
		}
		for (Table table : tables) {
			if (table.getPackageName() == null || table.getPackageName().length() == 0)
				continue;
			if (table.getJspConfig() != null) {
				if (table.getJspConfig().getQueryConfig() != null) {
					new QueryJspGenerator(this, table.getJspConfig()).generator();
				}
				if (table.getJspConfig().getEditConfig() != null) {
					new EditJspGenerator(this, table.getJspConfig()).generator();
				}
			}
		}
		for (EnumDef e : enums) {
			ClassGenerator generator = new EnumClassGenerator(this, e);
			generator.generate();
		}
		for (TradeExecutorConfig c : tradeExecutorConfigMap.values()) {
			if (c.getTables().size() > 0) {
				ClassGenerator generator = new TradeExecutorClassGenerator(this, c);
				generator.generate();
			}
		}
		ClassGenerator generator = new EnumValuesClassGenerator(this);
		generator.generate();
	}

	public Table getRightTable() {
		return rightTable;
	}

	private void parseRightMap(List<String> rows) {
		for (String o : rows) {
			String s[] = StringHelper.splitToStringArray(o, ",");
			String n = s[1].trim();
			n = n.substring(1, n.length() - 1);
			rightMap.put(n.trim(), Long.valueOf(s[0].trim()));
		}
	}

	public Map<String, Long> getRightMap() {
		return rightMap;
	}

	public void setRightMap(Map<String, Long> rightMap) {
		this.rightMap = rightMap;
	}

	public Map<String, PackageDef> getPackageDefs() {
		return packageDefs;
	}

	public Collection<Table> getTables() {
		return tables;
	}

	public Database getDatabase() {
		return database;
	}

	/**
	 * 检查列o是否是标准的列
	 * 
	 * @param o
	 *            要检查的列
	 * @return 检查结果
	 */
	public boolean isStandardColumn(Column o) {
		return database.getStandardColumns().contains(o);
	}
}
