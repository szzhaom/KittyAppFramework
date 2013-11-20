package kitty.kaf.webframe.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.jsp.JspWriter;

import kitty.kaf.helper.CollectionHelper;
import kitty.kaf.io.TreeNode;
import kitty.kaf.json.JSONException;
import kitty.kaf.json.JSONObject;
import kitty.kaf.json.NoQuoteString;

public class TreeTag extends DivTag {
	private static final long serialVersionUID = 1L;
	private boolean checkboxes;
	private Object items;
	protected JSONObject json;
	private String url;
	private int depths;
	private String onSelect;
	private String onCreateField;
	private String scriptScope;
	private String expandCollapseImagePrefix;

	@Override
	protected String getScriptScope() {
		if (scriptScope != null)
			return scriptScope + ".";
		return super.getScriptScope();
	}

	public void setScriptScope(String scriptScope) {
		this.scriptScope = scriptScope;
	}

	public String getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		try {
			json = new JSONObject();
			json.put("container", getId() + "_d");
			json.put("value", getValue());
			json.put("url", url);
			json.put("checkbox", checkboxes);
			if (expandCollapseImagePrefix != null)
				json.put("ecimageprefix", expandCollapseImagePrefix);
			JSONObject events = new JSONObject();
			json.put("events", events);
			if (onSelect != null)
				events.put("select", new NoQuoteString("function(s,o){" + onSelect + "}"));
			if (onCreateField != null)
				events.put("createField", new NoQuoteString("function(s,o){" + onCreateField + "}"));
			// JSONArray a = new JSONArray();
			// json.put("items", a);
			if (items instanceof TreeNode<?, ?>) {
				TreeNode<?, ?> node = (TreeNode<?, ?>) items;
				if (depths > 0) {
					node = (TreeNode<?, ?>) node.copy();
					List<Object> ls = new ArrayList<Object>();
					if (getValue() != null) {
						if (getValue() instanceof Object[]) {
							for (Object o : (Object[]) getValue())
								ls.add(o);
						} else if (getValue() instanceof Collection<?>)
							ls.addAll((Collection<?>) getValue());
						else
							ls.add(getValue());
					}
					node.enableDepths(ls, depths);
				}
				node.toJson(json, 1000000);
			}
			if (getId() != null) {
				writer.write("<input type='hidden' id='" + this.getId() + "' name='" + this.getId() + "' value='"
						+ CollectionHelper.collectionToString(getValue()) + "'/>");
				try {
					json.put("value_input", getId());
				} catch (JSONException e) {
				}
			}
		} catch (JSONException e) {
			throw new IOException(e);
		}
		super.doStartTag(writer);
	}

	@Override
	protected void writeId(JspWriter writer) throws IOException {
		writeAttribute(writer, "id", getId() + "_d");
	}

	protected void outputTreeScript(JspWriter writer) throws IOException {
		writer.write("<script>" + getScriptScope() + getId() + "=new TreeControl(" + json.toString() + ");</script>");
		json = null;
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		super.doEndTag(writer);
		outputTreeScript(writer);
	}

	@Override
	protected void writeValue(JspWriter writer) throws IOException {
	}

	public Object getItems() {
		return items;
	}

	public void setItems(Object items) {
		this.items = items;
	}

	public boolean isCheckboxes() {
		return checkboxes;
	}

	public void setCheckboxes(boolean checkboxes) {
		this.checkboxes = checkboxes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDepths() {
		return depths;
	}

	public void setDepths(int depths) {
		this.depths = depths;
	}

	public String getExpandCollapseImagePrefix() {
		return expandCollapseImagePrefix;
	}

	public void setExpandCollapseImagePrefix(String expandCollapseImagePrefix) {
		this.expandCollapseImagePrefix = expandCollapseImagePrefix;
	}

	public String getOnCreateField() {
		return onCreateField;
	}

	public void setOnCreateField(String onCreateField) {
		this.onCreateField = onCreateField;
	}

}
