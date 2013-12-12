package kitty.kaf.dao.tools.cg;

import japa.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kitty.kaf.KafUtil;
import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.Database;
import kitty.kaf.dao.tools.EnumDef;
import kitty.kaf.dao.tools.Table;
import kitty.kaf.dao.tools.cg.jsp.EditJspGenerator;
import kitty.kaf.dao.tools.cg.jsp.MainMenuJspGenerator;
import kitty.kaf.dao.tools.cg.jsp.MenuJspConfig;
import kitty.kaf.dao.tools.cg.jsp.MenuJspGenerator;
import kitty.kaf.dao.tools.cg.jsp.QueryJspGenerator;
import kitty.kaf.dao.tools.cg.template.TemplateConfig;
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
	String workspaceDir, webPackageName, infProjectName, webProjectName, ejbProjectName;
	Map<String, PackageDef> packageDefs = new HashMap<String, PackageDef>();
	Collection<Table> tables;
	Collection<EnumDef> enums;
	Map<String, TradeExecutorConfig> tradeExecutorConfigMap = new HashMap<String, TradeExecutorConfig>();
	Map<String, Long> rightMap = new HashMap<String, Long>();
	protected Database database;
	Table rightTable;
	List<MenuJspConfig> menuJspList = new ArrayList<MenuJspConfig>();
	MenuJspConfig mainMenuJspConfig;
	TemplateConfig templateConfig;

	public CodeGenerator(Database database, Element root) {
		templateConfig = new TemplateConfig(this, KafUtil.getConfigPath() + "template-config.xml");
		this.database = database;
		NodeList ls = root.getElementsByTagName("code_generator");
		if (ls.getLength() > 0) {
			Element el = (Element) ls.item(0);
			workspaceDir = el.getAttribute("work-space-dir");
			webPackageName = el.getAttribute("web-package-name");
			infProjectName = el.getAttribute("inf-project-name");
			webProjectName = el.getAttribute("web-project-name");
			ejbProjectName = el.getAttribute("ejb-project-name");
			ls = el.getElementsByTagName("package");
			for (int i = 0; i < ls.getLength(); i++) {
				el = (Element) ls.item(i);
				String name = el.getAttribute("name");
				packageDefs.put(name, new PackageDef(this, el));
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
		ls = root.getElementsByTagName("menu-jsp-config");
		for (int i = 0; i < ls.getLength(); i++) {
			Element el = (Element) ls.item(i);
			NodeList ls1 = el.getElementsByTagName("menu");
			for (int j = 0; j < ls1.getLength(); j++) {
				el = (Element) ls1.item(j);
				MenuJspConfig config = new MenuJspConfig(el);
				if (config.getName().equals("index"))
					mainMenuJspConfig = config;
				else
					menuJspList.add(config);
			}
		}
		this.tables = database.getTables().values();
		this.enums = database.getEnumDefs().values();
	}

	public TemplateConfig getTemplateConfig() {
		return templateConfig;
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
				MenuJspConfig config = this.getMenuJspTemplateConfig(table.getJspConfig().getMenuName());
				if (config != null && !table.getJspConfig().isDontCreateMenu())
					config.getTables().add(table);
				if (table.getJspConfig().getQueryConfig() != null) {
					new QueryJspGenerator(this, table.getJspConfig()).generate();
				}
				if (table.getJspConfig().getEditConfig() != null) {
					new EditJspGenerator(this, table.getJspConfig()).generate();
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
		for (MenuJspConfig o : menuJspList) {
			new MenuJspGenerator(this, o).generate();
		}
		if (mainMenuJspConfig != null) {
			new MainMenuJspGenerator(this, mainMenuJspConfig).generate();
			new MenuDataClassGenerator(this, mainMenuJspConfig).generate();
		}
		ClassGenerator generator = new EnumValuesClassGenerator(this);
		generator.generate();
		new LocalCacheDataClassGenerator(this).generate();
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

	public List<MenuJspConfig> getMenuJspList() {
		return menuJspList;
	}

	public MenuJspConfig getMenuJspTemplateConfig(String name) {
		for (MenuJspConfig o : menuJspList)
			if (o.getName().equals(name))
				return o;
		return null;
	}

	public String getWebPackageName() {
		return webPackageName;
	}

	public void setWebPackageName(String webPackageName) {
		this.webPackageName = webPackageName;
	}

	public String getWebProjectName() {
		return webProjectName;
	}

	public void setWebProjectName(String webProjectName) {
		this.webProjectName = webProjectName;
	}

	public String getEjbProjectName() {
		return ejbProjectName;
	}

	public void setEjbProjectName(String ejbProjectName) {
		this.ejbProjectName = ejbProjectName;
	}

	public String getInfProjectName() {
		return infProjectName;
	}

	public void setInfProjectName(String infProjectName) {
		this.infProjectName = infProjectName;
	}
}
