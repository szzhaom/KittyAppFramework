package kitty.kaf.webframe.tags;


/**
 * 输出简单的A标签
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class DivTag extends HtmlTag {
	private static final long serialVersionUID = 1L;

	@Override
	String getTagName() {
		return "div";
	}

}
