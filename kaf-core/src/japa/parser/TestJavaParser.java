package japa.parser;

import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestJavaParser {

	public void execute(Object... params) {
	}

	public void execute(List<?> params) {
	}

	public TestJavaParser(List<?> params) {
	}

	public static void main(String[] args) {
		try {
			CompilationUnit c = JavaParser.parse(new File(
					"/zhaom/product/kaf/git/KittyAppFramework/kaf-core/src/japa/parser/TestJavaParser.java"));
			System.out.println(c.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
