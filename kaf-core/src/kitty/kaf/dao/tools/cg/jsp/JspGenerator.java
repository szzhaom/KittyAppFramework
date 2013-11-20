package kitty.kaf.dao.tools.cg.jsp;

import java.io.IOException;

import kitty.kaf.dao.tools.cg.CodeGenerator;

/**
 * JSP生成器
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class JspGenerator {
	CodeGenerator generator;

	public JspGenerator(CodeGenerator generator) {
		this.generator = generator;
	}

	abstract public void generator() throws IOException;
}
