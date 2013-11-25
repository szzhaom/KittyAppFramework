package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.TemplateDef;
import kitty.kaf.helper.StringHelper;

public class MainMenuJspGenerator extends JspGenerator {
	MenuJspConfig config;

	public MainMenuJspGenerator(CodeGenerator generator, MenuJspConfig config) {
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
		File file = new File(fileName);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		FileWriter writer = new FileWriter(fileName);
		writer.write(template);
		writer.close();
	}

}
