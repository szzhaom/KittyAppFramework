package kitty.testapp.inf.web;

import java.util.List;

import kitty.testapp.inf.ds.file.FileCategoryHelper;
import kitty.testapp.inf.ds.file.FileHostHelper;
import kitty.testapp.inf.enumdef.EnumValues;

public class GlobalData {
	private EnumValues enumValues = new EnumValues();

	public EnumValues getEnumValues() {
		return enumValues;
	}

	public List<?> getFileHostList() {
		return FileHostHelper.localFileHostMap.getItems();
	}

	public List<?> getFileCategoryList() {
		return FileCategoryHelper.localFileCategoryMap.getItems();
	}
}
