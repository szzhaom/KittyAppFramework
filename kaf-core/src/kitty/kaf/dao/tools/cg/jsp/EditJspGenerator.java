package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.KafUtil;
import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.PackageDef;
import kitty.kaf.dao.tools.cg.template.JspTemplate;
import kitty.kaf.dao.tools.cg.template.Template;
import kitty.kaf.helper.StringHelper;

public class EditJspGenerator extends JspGenerator {
	TableJspConfig config;
	EditJspConfig editConfig;

	public EditJspGenerator(CodeGenerator generator, TableJspConfig config, EditJspConfig editConfig) {
		super(generator);
		this.config = config;
		if (editConfig == null)
			this.editConfig = config.editConfig;
		else
			this.editConfig = editConfig;
	}

	@Override
	public void generate() throws IOException {
		PackageDef def = generator.getPackageDef(config.table.getPackageName());
		String fileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root"
				+ KafUtil.clearFirstAttributeTag(editConfig.path).replace("//", "/") + ".jsp";
		JspTemplate jt = generator.getTemplateConfig().getJspFileTemplates().get(editConfig.getTemplateName());
		String tempFileName = generator.getWorkspaceDir() + def.getWebProjectName() + "/root" + jt.getLocation();
		tempFileName = tempFileName.replace("//", "/");

		String template = StringHelper.loadFromFile(tempFileName).toString();
		StringBuffer sb = new StringBuffer();
		for (JspEditField o : editConfig.editFields) {
			Template t = generator.getTemplateConfig().getEditFieldTemplates().get(o.getTemplateName());
			String tt = t.getContent().replace("${id}", o.getField());
			if (o.getColumn() != null) {
				if (o.getColumn().isAutoIncrement() || o.getColumn().getUserInputMode().equals("editonly"))
					tt = tt.replace("${rendered}", "${data.id!=null}");
				else if (o.getColumn().getUserInputMode().equals("createonly"))
					tt = tt.replace("${rendered}", "${data.id==null}");
				else
					tt = tt.replace("${rendered}", "true");
			} else
				tt = tt.replace("${rendered}", "true");
			tt = tt.replace("${desp}", o.getDesp());
			tt = tt.replace("${type}", o.getType());

			tt = tt.replace("${value}", o.getValue());
			tt = tt.replace("${normal_prompt}", o.getNormalPrompt());
			tt = tt.replace("${error_prompt}", o.getErrorPrompt());
			tt = tt.replace("${readonly}", o.getReadonly());
			tt = tt.replace("${params}", o.getParams());
			tt = tt.replace("${min_length}", o.getMinLength());
			tt = tt.replace("${max_length}", o.getMaxLength());
			tt = tt.replace("${min_value}", o.getMinValue());
			tt = tt.replace("${max_value}", o.getMaxValue());
			tt = tt.replace("${checkboxes}", o.getCheckboxes());
			tt = tt.replace("${multiselect}", o.getMultiselect());
			String url = KafUtil.procAttribute(o.getUrl()).replace("\\", "\\\\");
			tt = tt.replace("${url}", url);
			tt = tt.replace("${url_text_field}", o.getUrlTextField());
			tt = tt.replace("${depths}", o.getDepths());
			tt = tt.replace("${regexp}", o.getRegExp() != null ? o.getRegExp() : "");
			tt = tt.replace("${nullable}", o.getNullable());
			sb.append(tt);
		}
		template = template.replace("${template_fields}", sb.toString());
		template = template.replace("${template_init}", config.getTable().getFullHelperClassName()
				+ ".insertOrEditPageProcess(request, response);");
		sb.setLength(0);
		File file = new File(fileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(template);
		writer.close();
	}
}
