package kitty.kaf.dao.tools.cg.jsp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import kitty.kaf.dao.tools.Table;
import kitty.kaf.dao.tools.cg.CodeGenerator;
import kitty.kaf.dao.tools.cg.TemplateDef;
import kitty.kaf.helper.StringHelper;
import kitty.kaf.json.JSONArray;
import kitty.kaf.json.JSONObject;

public class MenuJspGenerator extends JspGenerator {
	MenuJspConfig config;

	public MenuJspGenerator(CodeGenerator generator, MenuJspConfig config) {
		super(generator);
		this.config = config;
	}

	@Override
	public void generator() throws IOException {
		String fileName = generator.getWorkspaceDir() + generator.getWebProjectName() + "/root"
				+ config.path.replace("//", "/") + ".jsp";
		TemplateDef td = generator.getJspTemplateMap().get(config.template);
		if (td == null)
			return;
		// RightConfig rc = config.getTable().getRightConfig();
		String tempFileName = generator.getWorkspaceDir() + generator.getWebProjectName() + "/root" + td.getLocation();
		tempFileName = tempFileName.replace("//", "/");
		String template = StringHelper.loadFromFile(tempFileName).toString();
		JSONArray json = new JSONArray();
		int i = 0;
		try {
			Collections.sort(config.getTables(), new Comparator<Table>() {
				@Override
				public int compare(Table o1, Table o2) {
					return o1.getOrderIndex() - o2.getOrderIndex();
				}
			});
			for (Table o : config.getTables()) {
				JSONObject j = new JSONObject();
				if (i == 0)
					j.put("selected", true);
				json.put(j);
				JSONObject j1 = new JSONObject();
				j.put("button", j1);
				JSONObject j2 = new JSONObject();
				j1.put("labelParams", j2);
				j2.put("html", o.getDesp());
				j.put("url", o.getJspConfig().getQueryConfig().path + ".go");
				i++;
			}
		} catch (Throwable e) {
			throw new IOException(e);
		}
		template = template.replace("${template.menu_items}", json.toString());
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
