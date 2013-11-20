package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.RightConfig;
import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.PackageDef;
import kitty.kaf.dao.tools.cg.TemplateDef;
import kitty.kaf.helper.StringHelper;

public class QueryJspGenerator extends JspGenerator {
	TableJspConfig config;

	public QueryJspGenerator(CodeGenerator generator, TableJspConfig config) {
		super(generator);
		this.config = config;
	}

	@Override
	public void generator() throws IOException {
		PackageDef def = generator.getPackageDef(config.table.getPackageName());
		String fileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root"
				+ config.queryConfig.path.replace("//", "/") + ".jsp";
		TemplateDef td = generator.getJspTemplateMap().get(config.queryConfig.getTemplateName());
		RightConfig rc = config.getTable().getRightConfig();
		String tempFileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root" + td.getLocation();
		tempFileName = tempFileName.replace("//", "/");
		String template = StringHelper.loadFromFile(tempFileName).toString();
		template = template.replace("${template.func_desp}", config.getTable().getDesp());
		template = template.replace("${template.page.right}",
				"mysession.user.right." + StringHelper.toVarName(rc.getQuery()) + "Enabled");
		template = template.replace("${template.trade.group}", config.getTable().getTradeConfig().getGroup());
		template = template.replace("${template.trade.executor}", config.getTable().getTradeConfig().getExecutorName());
		template = template.replace("${template.trade.queryCmd}", "query" + config.getTable().getJavaClassName());
		template = template.replace("${template.trade.deleteCmd}", "remove" + config.getTable().getJavaClassName());
		template = template.replace("${template.trade.insertCmd}", "insert" + config.getTable().getJavaClassName());
		template = template.replace("${template.trade.editCmd}", "edit" + config.getTable().getJavaClassName());
		template = template.replace("${template.page.create}", config.getEditConfig().getPath() + ".go");
		template = template.replace("${template.pk}", "id");
		template = template.replace("${template.desp_column}", config.getTable().getDespColumn().getName());
		template = template.replace("${template.query_right}",
				"${mysession.user.right." + StringHelper.toVarName(rc.getQuery()) + "Enabled}");
		template = template.replace("${template.create_right}",
				"${mysession.user.right." + StringHelper.toVarName(rc.getInsert()) + "Enabled}");
		template = template.replace("${template.delete_right}",
				"${mysession.user.right." + StringHelper.toVarName(rc.getDelete()) + "Enabled}");
		template = template.replace("${template.edit_right}",
				"${mysession.user.right." + StringHelper.toVarName(rc.getEdit()) + "Enabled}");
		template = template.replace("${template.place_holder}", config.getPlaceHolder());
		StringBuffer tableColumns = new StringBuffer();
		Column pk = config.table.getPkColumn();
		for (JspTableColumn o : config.queryConfig.tableColumns) {
			if (tableColumns.length() > 0)
				tableColumns.append(",");
			tableColumns.append("{");
			if (o.getWidth().equalsIgnoreCase("auto"))
				tableColumns.append("width:'auto',");
			else
				tableColumns.append("width:" + o.getWidth() + ",");

			tableColumns.append("field:'" + (pk == o.getColumn() ? "id" : o.columnName) + "',");
			tableColumns.append("row_class:'" + o.rowClass + "',");
			tableColumns.append("head_class:'" + o.headClass + "',");

			if (o.isCheckboxes())
				tableColumns.append("checkbox:true");
			else
				tableColumns.append("caption:'" + o.getCaption() + "'");
			tableColumns.append("}");
		}
		template = template.replace("${template.talble.columns}", tableColumns);
		File file = new File(fileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(template);
		writer.close();
	}
}
