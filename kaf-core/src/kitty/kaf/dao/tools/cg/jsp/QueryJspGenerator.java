package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.RightConfig;
import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.PackageDef;
import kitty.kaf.dao.tools.cg.template.JspTemplate;
import kitty.kaf.dao.tools.cg.template.Template;
import kitty.kaf.helper.StringHelper;

public class QueryJspGenerator extends JspGenerator {
	TableJspConfig config;

	public QueryJspGenerator(CodeGenerator generator, TableJspConfig config) {
		super(generator);
		this.config = config;
	}

	@Override
	public void generate() throws IOException {
		PackageDef def = generator.getPackageDef(config.table.getPackageName());
		String fileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root"
				+ config.queryConfig.path.replace("//", "/") + ".jsp";
		JspTemplate jt = generator.getTemplateConfig().getJspFileTemplates().get(config.queryConfig.getTemplateName());
		RightConfig rc = config.getTable().getRightConfig();
		String tempFileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root" + jt.getLocation();
		tempFileName = tempFileName.replace("//", "/");
		String template = StringHelper.loadFromFile(tempFileName).toString();
		StringBuffer sb = new StringBuffer();
		for (JspOptionActionConfig o : config.queryConfig.actions) {
			Template tt = generator.getTemplateConfig().getQueryOptionActionTemplates().get(o.getActionName());
			String url = o.url;
			if (!url.endsWith(".go"))
				url += ".go";
			String t = tt.getContent().replace("${url}", url);
			if (o.getSaveUrl() != null)
				t = t.replace("${save_url}", o.getSaveUrl());
			t = t.replace("${title}", o.getTitle());
			t = t.replace("${desp}", o.getDesp());
			sb.append(t);
		}
		template = template.replace("${template.custom_actions}", sb.toString());
		sb.setLength(0);
		Column pk = config.table.getPkColumn();
		for (JspTableColumn o : config.queryConfig.tableColumns) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append("{");
			if (o.getWidth().equalsIgnoreCase("auto"))
				sb.append("width:'auto',");
			else
				sb.append("width:" + o.getWidth() + ",");

			sb.append("field:'" + (pk == o.getColumn() ? "id" : o.columnName) + "',");
			sb.append("row_class:'" + o.rowClass + "',");
			sb.append("head_class:'" + o.headClass + "',");

			if (o.isCheckboxes())
				sb.append("checkbox:true");
			else
				sb.append("caption:'" + o.getCaption() + "'");
			sb.append("}");
		}
		template = template.replace("${template.talble.columns}", sb);

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
		template = template.replace("${template.textfield}", "undefined");
		template = template.replace("${template.create_func_desp}", config.getQueryConfig().getCreateButtonDesp());
		template = template.replace("${template.delete_func_desp}", config.getQueryConfig().getDeleteButtonDesp());
		template = template.replace("${template.edit_func_desp}", config.getQueryConfig().getEditButtonDesp());
		template = template.replace("${template.prev_id}", config.getQueryConfig().getPrevIdName());
		File file = new File(fileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(template);
		writer.close();
	}
}
