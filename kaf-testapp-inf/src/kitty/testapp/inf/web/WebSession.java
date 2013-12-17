package kitty.testapp.inf.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import kitty.kaf.io.DataRead;
import kitty.kaf.io.DataWrite;
import kitty.kaf.io.IdObject;
import kitty.kaf.session.AbstractRequestSession;
import kitty.testapp.inf.ds.right.UserHelper;
import kitty.testapp.inf.ds.right.beans.User;

public class WebSession extends AbstractRequestSession<User> {
	private static final long serialVersionUID = 1L;
	HashMap<String, String> parameters = new HashMap<String, String>();

	@Override
	protected User loadUser(long userId) {
		return UserHelper.userMap.get(userId);
	}

	@Override
	public void readFromStream(DataRead stream) throws IOException {
		super.readFromStream(stream);
		int c = stream.readShort(false);
		for (int i = 0; i < c; i++) {
			parameters.put(stream.readPacketByteLenString(), stream.readPacketShortLenString());
		}
	}

	@Override
	public void writeToStream(DataWrite stream) throws IOException {
		super.writeToStream(stream);
		stream.writeShort(parameters.size(), false);
		Iterator<String> it = parameters.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String v = parameters.get(key);
			stream.writePacketByteLenString(key);
			stream.writePacketShortLenString(v);
		}
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public static GlobalData data = new GlobalData();
	public MenuData menuData;

	public MenuData getMenuData() {
		if (menuData == null)
			menuData = new MenuData(this.getUser());
		return menuData;
	}

	public GlobalData getGlobalData() {
		return data;
	}

	@Override
	protected int compareId(String id1, Object id2) {
		return id1.compareTo(id2.toString());
	}

	@Override
	protected IdObject<String> newInstance() {
		return new WebSession();
	}

}
