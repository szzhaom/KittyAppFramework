package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.TemplateDef;
import kitty.kaf.helper.StringHelper;

public class MenuJspGenerator extends JspGenerator {
	MenuJspConfig config;

	public MenuJspGenerator(CodeGenerator generator, MenuJspConfig config) {
		super(generator);
		this.config = config;
	}

	@Override
	public void generate() throws IOException {
		String fileName = generator.getWorkspaceDir() + generator.getWebProjectName() + "/root"
				+ config.path.replace("//", "/") + ".jsp";
		TemplateDef td = generator.getJspTemplateMap().get(config.template);
		if (td == null)
			return;
		// RightConfig rc = config.getTable().getRightConfig();
		String tempFileName = generator.getWorkspaceDir() + generator.getWebProjectName() + "/root" + td.getLocation();
		tempFileName = tempFileName.replace("//", "/");
		String template = StringHelper.loadFromFile(tempFileName).toString();
		template = template.replace("${template.menu_items}",
				"mysession.menuData." + StringHelper.toVarName(config.getName()) + "MenuJson");
		template = template.replace("${template.page.right}", "true");
		template = template.replace("${template.menu_desp}", config.getDesp());
		File file = new File(fileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(template);
		writer.close();
	}

}
