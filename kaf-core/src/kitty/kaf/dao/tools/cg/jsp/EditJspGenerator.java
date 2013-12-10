package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.PackageDef;
import kitty.kaf.dao.tools.cg.TemplateDef;
import kitty.kaf.helper.StringHelper;

public class EditJspGenerator extends JspGenerator {
	TableJspConfig config;

	public EditJspGenerator(CodeGenerator generator, TableJspConfig config) {
		super(generator);
		this.config = config;
	}

	@Override
	public void generate() throws IOException {
		PackageDef def = generator.getPackageDef(config.table.getPackageName());
		String fileName = generator.getWorkspaceDir() + def.getWebProjectName()
				+ "/root" + config.editConfig.path.replace("//", "/") + ".jsp";
		TemplateDef td = generator.getJspTemplateMap().get(
				config.editConfig.getTemplateName());
		String tempFileName = generator.getWorkspaceDir()
				+ def.getWebProjectName() + "/root" + td.getLocation();
		tempFileName = tempFileName.replace("//", "/");

		String template = StringHelper.loadFromFile(tempFileName).toString();
		StringBuffer sb = new StringBuffer();
		for (JspEditField o : config.editConfig.editFields) {
			String tt = td.getEditField(o.getTemplateName()).replace("${id}",
					o.getField());
			if (o.getColumn() != null && o.getColumn().isAutoIncrement())
				tt = tt.replace("${rendered}", "${data.id!=null}");
			else
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
			tt = tt.replace("${url}", o.getUrl());
			tt = tt.replace("${url_text_field}", o.getUrlTextField());
			tt = tt.replace("${depths}", o.getDepths());
			sb.append(tt);
		}
		template = template.replace("${template_fields}", sb.toString());
		template = template.replace("${template_init}", config.getTable()
				.getFullHelperClassName()
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
