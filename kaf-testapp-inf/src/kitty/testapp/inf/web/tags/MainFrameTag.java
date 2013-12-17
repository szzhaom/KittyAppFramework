package kitty.testapp.inf.web.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import kitty.kaf.util.DateTime;
import kitty.kaf.webframe.tags.BasicTag;
import kitty.testapp.inf.ds.right.beans.User;
import kitty.testapp.inf.web.WebSession;

class MainMenuDef {
	long right;
	String desp;
	String url;

	public MainMenuDef(long right, String desp, String url) {
		super();
		this.right = right;
		this.desp = desp;
		this.url = url;
	}

}

public class MainFrameTag extends BasicTag {
	private static final long serialVersionUID = 1L;
	boolean notRenderMainFrame;
	private Object menuItems;

	public boolean isNotRenderMainFrame() {
		return notRenderMainFrame;
	}

	public void setNotRenderMainFrame(boolean notRenderMainFrame) {
		this.notRenderMainFrame = notRenderMainFrame;
	}

	protected void createWebMenu(JspWriter writer, String contextPath) throws IOException {
		writer.write("<li class='item'><a class='cta' href='" + contextPath + "/index.go'>管理平台</a></li>");
	}

	protected void outputHeader(JspWriter writer, String contextPath) throws IOException {
		writer.write("<div id='header'><ol id='headermenu'>");
		writer.write("<li class='item'><span class='cts'>KAF</span></li>");
		createWebMenu(writer, contextPath);
		writer.write("<div class='clear'></div>");
		writer.write("</ol>");
		outputUserInfo(writer, contextPath);
		writer.write("<div class='clear'></div></div>");
	}

	private void outputUserInfo(JspWriter writer, String contextPath) throws IOException {
		WebSession session = (WebSession) WebSession.getCurrentSession((HttpServletRequest) pageContext.getRequest());
		writer.write("<ol id='header_ui'>");
		if (session.isLogined()) {
			writer.write("<li class='item'><span class='user_info'>" + ((User) session.getUser()).getUserName()
					+ "</span></li>");
			writer.write("<li class='item' id='webmenu'><a class='cta' href='" + contextPath
					+ "/loginout.go'>退出登录</a></li>");
		} else
			writer.write("<li class='item' id='webmenu'><a class='cta' href='" + contextPath + "/login.go'>登录</a></li>");
		writer.write("</ol>");
	}

	protected void outputMainFrame(JspWriter writer, String contextPath) throws IOException {
		writer.write("<div id='mainframe'>");
	}

	protected void outputFooter(JspWriter writer, String contextPath) throws IOException {
		writer.write("<div id='footer'>");
		writer.write("<span>© ?-" + new DateTime().getYear() + "</span>");
		writer.write("</div>");
	}

	@Override
	protected void doStartTag(JspWriter writer) throws IOException {
		WebSession session = (WebSession) WebSession.getCurrentSession((HttpServletRequest) pageContext.getRequest());
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String path = request.getContextPath();
		if (path == null)
			path = "";
		if (session.isLogined()) {
			// User user = (User) session.getUser();
		}
		writeText(writer, "<div>");
		outputHeader(writer, path);
		outputMainFrame(writer, path);
	}

	@Override
	protected void doEndTag(JspWriter writer) throws IOException {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String path = request.getContextPath();
		if (path == null)
			path = "";
		writer.write("</div>");
		outputFooter(writer, path);
		if (!notRenderMainFrame) {
			writer.write("<script type='text/javascript'>");
			writer.write("var jsCssFileMgr=new JsCssFileManager();var mainPageControl = new UIPageControl({");
			writer.write("'parent' : 'mainframe',");
			writer.write("'isleftright' : true,");
			writer.write("'jsCssFileMgr' : jsCssFileMgr,");
			writer.write("'createParams' : {'class' : 'main_frame'},");
			writer.write("'buttonParams' : {'createParams':{'class' : 'pagebutton'}},");
			writer.write("'buttonPanelParams' : {'class' : 'leftpanel'},");
			writer.write("'contentPanelParams' : {'class' : 'maincontent'},");
			writer.write("'pageParams' : {'class' : 'mainpage'},");
			writer.write("'spaceButtonParams' : {'class' : 'pagebuttonspace'},");
			writer.write("'items' : " + menuItems);
			writer.write("});");
			writer.write("</script>");
		}
		writeText(writer, "</div>");
	}

	public Object getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(Object menuItems) {
		this.menuItems = menuItems;
	}
}
