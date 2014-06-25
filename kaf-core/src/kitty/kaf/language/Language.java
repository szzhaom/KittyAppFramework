package kitty.kaf.language;

import java.util.concurrent.ConcurrentHashMap;

import kitty.kaf.io.IdObject;
import kitty.kaf.io.ListItem;

/**
 * 语言配置类
 * 
 * @author 赵明
 * @version 1.0
 */
public class Language extends ListItem<String> {
	private static final long serialVersionUID = 1L;
	public String name;
	public String desp;
	public String fielName;
	ConcurrentHashMap<String, String> stringsMap = new ConcurrentHashMap<String, String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getFielName() {
		return fielName;
	}

	public void setFielName(String fielName) {
		this.fielName = fielName;
	}

	public ConcurrentHashMap<String, String> getStringsMap() {
		return stringsMap;
	}

	/**
	 * 获取配置的键值
	 * 
	 * @param key
	 *            键值
	 * @return 与键相关的值
	 */
	public String getKey(String key) {
		return stringsMap.get(key);
	}

	@Override
	public String getText() {
		return desp;
	}

	@Override
	public void setText(String text) {
		desp = text;
	}

	@Override
	public String getIdString() {
		return name;
	}

	@Override
	public void setIdString(String v) {
		name = v;
	}

	@Override
	protected int compareId(String id1, Object id2) {
		return id1.compareTo((String) id2);
	}

	@Override
	protected IdObject<String> newInstance() {
		return new Language();
	}
}
