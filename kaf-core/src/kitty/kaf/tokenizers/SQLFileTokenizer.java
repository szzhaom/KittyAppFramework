package kitty.kaf.tokenizers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;

/**
 * SQL文件解析器，以;号为一条SQL的结束符，解析出全部的SQL语句
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
public class SQLFileTokenizer implements Enumeration<String> {
	Reader reader;
	String currentSql;

	public SQLFileTokenizer(Reader reader) {
		super();
		this.reader = reader.markSupported() ? reader : new BufferedReader(
				reader);
	}

	public SQLFileTokenizer(String sqlString) {
		this(new StringReader(sqlString));
	}

	@Override
	public boolean hasMoreElements() {
		StringBuffer sb = new StringBuffer();
		boolean ret = false;
		try {
			int c;
			boolean hasQuote = false;
			while ((c = reader.read()) >= 0) {
				sb.append((char) c);
				ret = true;
				if (c == '\\') { // 处理转义字符\'
					c = reader.read();
					sb.append((char) c);
					if (c == '\'')
						continue;
				} else if (c == '\'') {
					hasQuote = !hasQuote;
				}
				if (!hasQuote) {
					if (c == ';')
						break;
				}
			}
			if (ret)
				currentSql = sb.toString();
		} catch (IOException e) {
			if (ret)
				currentSql = sb.toString();
			return ret;
		}
		return ret;
	}

	@Override
	public String nextElement() {
		return currentSql;
	}

}
