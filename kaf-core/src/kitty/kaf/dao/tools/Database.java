package kitty.kaf.dao.tools;

import japa.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import kitty.kaf.KafUtil;
import kitty.kaf.dao.source.DaoSource;
import kitty.kaf.dao.source.DaoSourceFactory;
import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.helper.SQLHelper;
import kitty.kaf.logging.KafLogger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Database {
	HashMap<String, Tablespace> tablespaces = new HashMap<String, Tablespace>();
	HashMap<String, Table> tables = new HashMap<String, Table>();
	HashMap<String, EnumDef> enumDefs = new HashMap<String, EnumDef>();
	List<Table> localCacheTables = new ArrayList<Table>();
	List<Column> standardColumns = new ArrayList<Column>();
	List<Object> rightList = new ArrayList<Object>();
	KafLogger logger = KafLogger.getLogger(Database.class);
	DaoSource daoSource;
	CodeGenerator generator;

	public static void main(String[] args) throws ParseException, IOException {
		// byte b=(byte)1;
		// CompilationUnit unit = JavaParser.parse(new
		// File("/zhaom/product/slots/workspace/server/hg-core/src/rongshi/core/dao/tools/Database.java"));
		new Database(KafUtil.getConfigPath() + "db-config.xml", "default");
	}

	public Database(String configFile, String daoSourceName) {
		try {
			this.daoSource = DaoSourceFactory.getDaoSource(this, daoSourceName);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStreamReader in = new InputStreamReader(new FileInputStream(configFile), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			InputSource input = new InputSource(reader);
			Document doc = builder.parse(input);
			NodeList list = doc.getElementsByTagName("config");
			Element root = (Element) list.item(0);
			NodeList ls = root.getElementsByTagName("tablespaces");
			for (int i = 0; i < ls.getLength(); i++) {
				Element e = (Element) ls.item(i);
				NodeList ls1 = e.getElementsByTagName("tablespace");
				for (int j = 0; j < ls1.getLength(); j++) {
					e = (Element) ls1.item(j);
					Tablespace s = new Tablespace(e, daoSource);
					if (s.sql != null)
						tablespaces.put(e.getAttribute("name"), s);
				}
			}
			// standard-columns
			list = doc.getElementsByTagName("standard-columns");
			for (int i = 0; i < list.getLength(); i++) {
				Element e = (Element) list.item(i);
				NodeList ls1 = e.getElementsByTagName("column");
				for (int j = 0; j < ls1.getLength(); j++) {
					Element node = (Element) ls1.item(j);
					standardColumns.add(new Column(node, null, daoSource));
				}
			}
			// enums
			list = doc.getElementsByTagName("enumdef");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				enumDefs.put(node.getAttribute("name"), new EnumDef(node));
			}
			// tables
			list = doc.getElementsByTagName("table");
			for (int i = 0; i < list.getLength(); i++) {
				Element node = (Element) list.item(i);
				Table table = new Table(node, this);
				table.orderIndex = i;
				tables.put(node.getAttribute("name"), table);
			}
			for (Table o : tables.values()) {
				if (o.isNeedAdded) {
					SQLHelper.executeSql(daoSource.getMaster(), daoSource.getType(), o.getCreateSql(), 5);
					SQLHelper.executeSql(daoSource.getMaster(), daoSource.getType(), o.getDataSql(true), 100);
				} else {
					SQLHelper.executeSql(daoSource.getMaster(), daoSource.getType(), o.getModifySql(), 5);
					SQLHelper.executeSql(daoSource.getMaster(), daoSource.getType(), o.getDataSql(false), 100);
				}
				for (int i = 0; i < o.getColumns().size(); i++) {
					if (o.getColumns().get(i).isNeedDeleted()) {
						o.getColumns().remove(i);
						i--;
					}
				}
				for (ForeignKey k : o.foreignKeys) {
					if (k.getGenCodeTableName() != null) {
						Table t = tables.get(k.getGenCodeTableName());
						if (t != null) {
							t.getForeignGenVarLists().add(k);
						}
					}
					if (k.getObjVarName() != null) {
						k.getTable().getForeignGenVars().add(k);
					}
				}
			}
			generator = new CodeGenerator(this, root);
			generator.generator();
		} catch (Throwable e) {
			logger.error("init dao failure:", e);
		}
		this.daoSource.close();
	}

	public HashMap<String, Tablespace> getTablespaces() {
		return tablespaces;
	}

	public HashMap<String, Table> getTables() {
		return tables;
	}

	public HashMap<String, EnumDef> getEnumDefs() {
		return enumDefs;
	}

	public void setEnumDefs(HashMap<String, EnumDef> enumDefs) {
		this.enumDefs = enumDefs;
	}

	public KafLogger getLogger() {
		return logger;
	}

	public DaoSource getDaoSource() {
		return daoSource;
	}

	public List<Column> getStandardColumns() {
		return standardColumns;
	}

	public List<Table> getLocalCacheTables() {
		return localCacheTables;
	}

	public void setLocalCacheTables(List<Table> localCacheTables) {
		this.localCacheTables = localCacheTables;
	}

	public List<ForeignKey> findForeignKeys(Table table) {
		// Column pk = table.getPkColumn();
		List<ForeignKey> r = new ArrayList<ForeignKey>();
		for (Table t : tables.values()) {
			if (t != table) {
				for (ForeignKey k : t.foreignKeys) {
					if (k.getTableRef().equalsIgnoreCase(table.getName())) {
						r.add(k);
					}
				}
			}
		}
		return r;
	}

	public List<Object> getRightList() {
		return rightList;
	}

	public void setRightList(List<Object> rightList) {
		this.rightList = rightList;
	}

}
