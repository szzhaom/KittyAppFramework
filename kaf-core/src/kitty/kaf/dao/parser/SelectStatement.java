package kitty.kaf.dao.parser;

import java.io.IOException;
import java.io.Reader;

public class SelectStatement {
	String selectExpr;
	String into;
	String tableReferences;
	String whereDefinition;
	String groupBy;
	String having;
	String orderBy;

	public SelectStatement(Reader reader) {
		StringBuffer sb = new StringBuffer();
		try {
			int c;
			boolean hasQuote = false;
			while ((c = reader.read()) >= 0) {
				sb.append((char) c);
				if (c == '\\') { // 处理转义字符\'
					c = reader.read();
					sb.append((char) c);
					if (c == '\'')
						continue;
				} else if (c == '\'') {
					hasQuote = !hasQuote;
				}
				if (!hasQuote) {
					if (c <= ' ') {

					}
				}
			}
		} catch (IOException e) {
		}
	}
}
