package kitty.kaf.dao.tools;

import kitty.kaf.KafUtil;

public class GenTool {

	public static void main(String[] args) {
		String file = "db-config.xml";
		if (args.length > 1)
			file = args[1];
		new Database(KafUtil.getConfigPath() + file, "default");
	}

}
