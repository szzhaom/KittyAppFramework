package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CommentList;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.io.KeyValue;

/**
 * 类生成器
 * 
 * @author 赵明
 * @since 1.0
 * @version 1.0
 * 
 */
abstract public class ClassGenerator {
	protected CompilationUnit cu;
	TypeDeclaration mainClass;
	File classFile;

	abstract TypeDeclaration generateMainClass();

	abstract CompilationUnit createParser() throws ParseException, IOException;

	abstract void generateBody() throws ParseException, IOException;

	public ClassGenerator() {
		super();
	}

	protected CompilationUnit parse(File file) throws ParseException, IOException {
		CompilationUnit unit = JavaParser.parse(classFile);
		// List<Node> nodes = new ArrayList<Node>();
		// nodes.add(unit.getPackage());
		// for (ImportDeclaration o : unit.getImports()) {
		// nodes.add(o);
		// }
		// for (TypeDeclaration o : unit.getTypes()) {
		//
		// }
		return unit;
	}

	/**
	 * 生成代码
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	public void generate() throws ParseException, IOException {
		cu = createParser();
		mainClass = generateMainClass();
		if (mainClass instanceof EnumDeclaration) {
			if (((EnumDeclaration) mainClass).getMembers() == null)
				((EnumDeclaration) mainClass).setMembers(new LinkedList<BodyDeclaration>());
		} else if (((ClassOrInterfaceDeclaration) mainClass).getMembers() == null)
			((ClassOrInterfaceDeclaration) mainClass).setMembers(new LinkedList<BodyDeclaration>());
		generateBody();
		FileWriter writer = new FileWriter(classFile);
		try {
			writer.write(cu.toString());
		} finally {
			writer.close();
		}
	}

	/**
	 * 加入import项
	 * 
	 * @param importClassName
	 *            import完整类名
	 */
	public void addImport(String importClassName) {
		ImportDeclaration p = new ImportDeclaration(ASTHelper.createNameExpr(importClassName), false, false);
		if (!cu.getImports().contains(p))
			cu.getImports().add(p);

	}

	/**
	 * 移除import项
	 * 
	 * @param importClassName
	 *            import项的类名
	 */
	public void removeImport(String importClassName) {
		ImportDeclaration p = new ImportDeclaration(ASTHelper.createNameExpr(importClassName), false, false);
		cu.getImports().remove(p);
	}

	/**
	 * 找出包含keyword处的代码列表
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的代码列表
	 */
	public List<Statement> findStatementsAtKeywordComment(List<Statement> stmts, String keyword) {
		KeyValue<Comment, CommentList> c = findKeywordComment(keyword);
		int index = -1;
		if (c.getKey() != null) {
			String text = c.getKey().getContent().trim();
			index = text.indexOf("statements=");
			text = text.substring(index + 11);
			text = text.substring(0, text.length() - 1);
			int count = Integer.valueOf(text);
			index = -1;
			for (int i = 0; i < stmts.size(); i++) {
				Statement st = stmts.get(i);
				if (st.getBeginLine() == c.getKey().getBeginLine() + 1) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				List<Statement> s = new LinkedList<Statement>();
				for (int i = index; i < index + count; i++) {
					s.add(stmts.get(i));
				}
				return s;
			}
		}
		return null;
	}

	public KeyValue<Integer, Comment> findStatementsAtKeyword(List<Statement> stmts, String keyword) {
		KeyValue<Comment, CommentList> c = findKeywordComment(keyword);
		int index = -1;
		if (c.getKey() != null) {
			String text = c.getKey().getContent().trim();
			index = text.indexOf("statements=");
			text = text.substring(index + 11);
			text = text.substring(0, text.length() - 1);
			index = -1;
			for (int i = 0; i < stmts.size(); i++) {
				Statement st = stmts.get(i);
				if (st.getBeginLine() == c.getKey().getBeginLine() + 1) {
					index = i;
					break;
				}
			}
		}
		return new KeyValue<Integer, Comment>(index, c.getValue());
	}

	/**
	 * 找出包含keyword处的代码，并删除自动生成的代码，返回删除时的语句索引
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的索引，-1表示未找到
	 */
	public KeyValue<Integer, Comment> findAndClearStatementsAtKeyword(List<Statement> stmts, String keyword) {
		KeyValue<Comment, CommentList> c = findKeywordComment(keyword);
		int index = -1;
		if (c.getKey() != null) {
			String text = c.getKey().getContent().trim();
			index = text.indexOf("statements=");
			text = text.substring(index + 11);
			text = text.substring(0, text.length() - 1);
			int count = Integer.valueOf(text);
			index = -1;
			for (int i = 0; i < stmts.size(); i++) {
				Statement st = stmts.get(i);
				if (st.getBeginLine() == c.getKey().getBeginLine() + 1) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				for (int i = 0; i < count; i++) {
					if (stmts.size() > index)
						stmts.remove(index);
					else
						break;
				}
			}
		}
		return new KeyValue<Integer, Comment>(index, c.getValue());
	}

	/**
	 * 找到相应的自动生成代码段部分
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的注释
	 */
	public void autoGenerateStatements(BlockStmt stmt, String keyword, List<Statement> statements,
			String beforeKeyword, boolean addFirst) {
		KeyValue<Integer, Comment> r = findAndClearStatementsAtKeyword(stmt.getStmts(), keyword);
		if (statements != null && statements.size() > 0) {
			Comment c = new LineComment("[" + keyword + " statements=" + statements.size() + "]");
			if (r.getValue() != null) {
				CommentList cl = (CommentList) r.getValue();
				for (int i = 0; i < cl.getCommentList().size(); i++) {
					if (cl.getCommentList().get(i).getContent().contains("[autogenerated:")) {
						cl.getCommentList().remove(i);
						i--;
					}
				}
				cl.getCommentList().add(c);
				c = cl;
			}
			statements.get(0).setComment(c);
			if (stmt.getStmts() == null)
				stmt.setStmts(new LinkedList<Statement>());
			if (r.getKey() < 0) {
				boolean added = false;
				if (beforeKeyword != null) {
					r = findStatementsAtKeyword(stmt.getStmts(), beforeKeyword);
					if (r.getKey() >= 0) {
						stmt.getStmts().addAll(r.getKey(), statements);
						added = true;
					}
				}
				if (!added) {
					if (addFirst)
						stmt.getStmts().addAll(0, statements);
					else
						stmt.getStmts().addAll(statements);
				}
			} else
				stmt.getStmts().addAll(r.getKey(), statements);
		}
	}

	public void autoGenerateStatements(BlockStmt stmt, String keyword, List<Statement> statements) {
		autoGenerateStatements(stmt, keyword, statements, null, false);
	}

	/**
	 * 找出包含keyword处的代码列表
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的代码列表
	 */
	public List<BodyDeclaration> findAutoGeneratorAtKeywordMembers(List<BodyDeclaration> members, String keyword) {
		KeyValue<Comment, CommentList> c = findKeywordComment(keyword);
		int index = -1;
		if (c.getKey() != null) {
			String text = c.getKey().getContent().trim();
			index = text.indexOf("statements=");
			text = text.substring(index + 11);
			text = text.substring(0, text.length() - 1);
			int count = Integer.valueOf(text);
			index = -1;
			for (int i = 0; i < members.size(); i++) {
				BodyDeclaration st = members.get(i);
				if (st.getBeginLine() == c.getKey().getBeginLine() + 1) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				List<BodyDeclaration> s = new LinkedList<BodyDeclaration>();
				for (int i = index; i < index + count; i++) {
					s.add(members.get(i));
				}
				return s;
			}
		}
		return null;
	}

	/**
	 * 找到包含keyword的注释
	 * 
	 * @param keyword
	 *            注释关键字
	 * @return 找到的注释，找不到返回null
	 */
	public KeyValue<Comment, CommentList> findKeywordComment(String keyword) {
		LineComment comment = null;
		CommentList commentList = null;
		String bk = "[" + keyword + " ";
		for (Comment o : cu.getComments()) {
			if (o instanceof LineComment) {
				LineComment c = (LineComment) o;
				if (comment == null) {
					if (c.getContent().trim().contains(bk)) {
						comment = c;
						commentList = null;
						break;
					}
				}
			} else if (o instanceof CommentList) {
				CommentList list = (CommentList) o;
				for (Comment cc : list.getCommentList()) {
					if (cc instanceof LineComment) {
						LineComment c = (LineComment) cc;
						if (comment == null) {
							if (c.getContent().trim().contains(bk)) {
								comment = c;
								commentList = list;
								break;
							}
						}
					}
				}
			}
		}
		return new KeyValue<Comment, CommentList>(comment, commentList);
	}

	/**
	 * 找出包含keyword处的代码，并删除自动生成的代码，返回删除时的语句索引
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的索引，-1表示未找到
	 */
	public KeyValue<Integer, Comment> findAndClearMembersAtKeyword(List<BodyDeclaration> members, String keyword) {
		KeyValue<Comment, CommentList> c = findKeywordComment(keyword);
		int index = 0;
		if (c.getKey() != null) {
			String text = c.getKey().getContent().trim();
			index = text.indexOf("statements=");
			text = text.substring(index + 11);
			text = text.substring(0, text.length() - 1);
			int count = Integer.valueOf(text);
			index = -1;
			for (int i = 0; i < members.size(); i++) {
				BodyDeclaration st = members.get(i);
				if (st.getBeginLine() == c.getKey().getBeginLine() + 1) {
					index = i;
					break;
				}
			}
			if (index > -1) {
				for (int i = 0; i < count; i++) {
					if (members.size() > index)
						members.remove(index);
					else
						break;
				}
			}
		}
		return new KeyValue<Integer, Comment>(index, c.getValue());
	}

	/**
	 * 找到相应的自动生成代码段部分
	 * 
	 * @param keyword
	 *            关键注释部分
	 * @return 找到的注释
	 */
	public void autoGenerateMembers(TypeDeclaration type, String keyword, List<BodyDeclaration> members) {
		KeyValue<Integer, Comment> r = findAndClearMembersAtKeyword(type.getMembers(), keyword);
		if (members != null && members.size() > 0) {
			Comment c = new LineComment("[" + keyword + " statements=" + members.size() + "]");
			if (r.getValue() != null) {
				CommentList cl = (CommentList) r.getValue();
				for (int i = 0; i < cl.getCommentList().size(); i++) {
					if (cl.getCommentList().get(i).getContent().contains("[autogenerated:")) {
						cl.getCommentList().remove(i);
						i--;
					}
				}
				cl.getCommentList().add(c);
				c = cl;
			}
			members.get(0).setComment(c);
			if (type.getMembers() == null)
				type.setMembers(new LinkedList<BodyDeclaration>());
			if (r.getKey() < 0)
				type.getMembers().addAll(members);
			else
				type.getMembers().addAll(r.getKey(), members);
		}
	}

}
