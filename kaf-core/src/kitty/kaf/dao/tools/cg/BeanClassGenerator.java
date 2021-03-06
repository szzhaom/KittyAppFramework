package kitty.kaf.dao.tools.cg;

import japa.parser.ASTHelper;
import japa.parser.ParseException;
import japa.parser.ast.Comment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.AssignExpr.Operator;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.PrimitiveType.Primitive;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import kitty.kaf.dao.tools.Column;
import kitty.kaf.dao.tools.EnumDef;
import kitty.kaf.dao.tools.ForeignKey;
import kitty.kaf.dao.tools.Table;
import kitty.kaf.dao.tools.datatypes.StringColumnDataType;
import kitty.kaf.helper.JPHelper;
import kitty.kaf.helper.StringHelper;

/**
 * Bean 类生成器
 * 
 * @author 赵明
 * @version 1.0
 * @since 1.0
 * 
 */
public class BeanClassGenerator extends ClassGenerator {
	Table table;
	String pkClass;
	List<Column> pkColumns = null;
	CodeGenerator generator;
	Column uniqueKeyColumn;

	public BeanClassGenerator(CodeGenerator generator, Table table) {
		super();
		this.generator = generator;
		this.table = table;
		uniqueKeyColumn = table.getUniqueKeyColumn();
	}

	@Override
	CompilationUnit createParser() throws ParseException, IOException {
		PackageDef def = generator.packageDefs.get(table.getPackageName());
		if (def == null)
			throw new IOException(table.getName() + " not package def");
		String path = generator.workspaceDir + def.getInfProjectName() + "/src/"
				+ def.getBeanPackageName().replace(".", "/").replace("//", "/");
		String fileName = path + "/" + table.getJavaClassName() + ".java";
		classFile = new File(fileName);
		if (!classFile.getParentFile().exists())
			classFile.getParentFile().mkdirs();
		if (classFile.exists())
			cu = parse(classFile);
		if (cu == null)
			cu = new CompilationUnit();
		if (cu.getImports() == null)
			cu.setImports(new LinkedList<ImportDeclaration>());
		if (cu.getTypes() == null)
			cu.setTypes(new LinkedList<TypeDeclaration>());
		if (cu.getComments() == null)
			cu.setComments(new LinkedList<Comment>());
		cu.setPackage(new PackageDeclaration(ASTHelper.createNameExpr(def.getBeanPackageName())));

		pkClass = null;
		if (table.getPk() != null) {
			try {
				pkColumns = table.getPk().getTableColumns();
				if (pkColumns.size() == 1) {
					pkClass = pkColumns.get(0).getDataType().getJavaClassName();
				}
			} catch (SQLException e) {
				throw new IOException(e);
			}
		}
		addImport("java.io.IOException");
		addImport("java.sql.SQLException");
		addImport("kitty.kaf.io.DataRead");
		addImport("kitty.kaf.io.DataWrite");
		addImport("kitty.kaf.dao.table.TableDef");
		addImport("kitty.kaf.dao.resultset.DaoResultSet");
		addImport("kitty.kaf.dao.table.TableColumnDef");
		addImport("kitty.kaf.json.JSONException");
		addImport("kitty.kaf.trade.pack.HttpRequest");
		addImport("kitty.kaf.json.JSONObject");
		if (pkClass != null)
			addImport("kitty.kaf.dao.table.IdTableObject");
		else
			addImport("kitty.kaf.dao.table.TableObject");
		if (uniqueKeyColumn != null)
			addImport("kitty.kaf.io.UnuqieKeyCachable");
		if (table.getLocalCache() != null) {
			addImport("kitty.kaf.cache.LocalCachable");
		}
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			String n = o.getDataType().getCustomJavaClassName();
			if (n != null) {
				EnumDef d = table.getDatabase().getEnumDefs().get(n);
				if (d != null) {
					PackageDef pd = generator.getPackageDef(d.getPackageName());
					addImport(pd.getEnumPackageName() + "." + d.getName());
				} else if (n.contains("."))
					addImport(o.getDataType().getCustomJavaClassName());
			} else if (o.getDataType().getDataType().equalsIgnoreCase("Date"))
				addImport("java.util.Date");
			if (o.getAutoConvertMethod() != null) {
				if (o.getAutoConvertMethod().equalsIgnoreCase("jp") || o.getAutoConvertMethod().equals("qp")) {
					addImport("kitty.kaf.helper.PinYinHelper");
				}
			}
		}
		return cu;
	}

	public void generateBody() throws IOException, ParseException {
		generateTableDef();
		generateFields();
		generateForeignVars();
		generateImplements();
	}

	private void generateForeignVars() {
		for (ForeignKey v : table.getForeignGenVars()) {
			Table t = generator.database.getTables().get(v.getTableRef());
			if (t != null) {
				addImport(t.getFullHelperClassName());
				Column column = t.findColumnByName(v.getVarBindColumn());
				addImport(t.getFullBeanClassName());
				String varName = StringHelper.toVarName(v.getObjVarName());
				String idVarName = StringHelper.toVarName(v.getColumn());
				FieldDeclaration item = new FieldDeclaration(ModifierSet.PRIVATE, new ReferenceType(
						new ClassOrInterfaceType(t.getJavaClassName())), new VariableDeclarator(
						new VariableDeclaratorId(varName)));
				JPHelper.addOrUpdateFieldsToClass(mainClass, item);
				String methodName = column.getDataType().getGetMethodName(varName);
				if (t.getLocalCache() != null) {
					addImport("java.util.Date");
					item = new FieldDeclaration(ModifierSet.PRIVATE,
							new ReferenceType(new ClassOrInterfaceType("Date")), new VariableDeclarator(
									new VariableDeclaratorId(varName + "LastModified")));
					JPHelper.addOrUpdateFieldsToClass(mainClass, item);
				}
				MethodDeclaration d = new MethodDeclaration(Modifier.PUBLIC, new ReferenceType(
						new ClassOrInterfaceType(t.getJavaClassName())), methodName);
				d = JPHelper.addOrUpdateMethod(mainClass, d, true);
				List<Statement> ls = new LinkedList<Statement>();
				if (t.getLocalCache() != null) {
					ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
							"Date")), new VariableDeclarator(new VariableDeclaratorId("lastModified"),
							new MethodCallExpr(new NameExpr(t.getJavaClassName() + "Helper.local"
									+ t.getJavaClassName() + "Map"), "getLastModifiedTime")))));
				}
				AssignExpr assign = new AssignExpr();
				assign.setTarget(new NameExpr(varName));
				if (t.getLocalCache() != null) {
					assign.setValue(new MethodCallExpr(new NameExpr(t.getJavaClassName() + "Helper.local"
							+ t.getJavaClassName() + "Map"), "get", new NameExpr(idVarName)));
				} else if (t.getCacheConfig() != null) {
					assign.setValue(new MethodCallExpr(new NameExpr(t.getJavaClassName() + "Helper."
							+ StringHelper.firstWordLower(t.getJavaClassName()) + "Map"), "get",
							new NameExpr(idVarName)));
				}
				assign.setOperator(Operator.assign);
				BlockStmt bs = new BlockStmt(new LinkedList<Statement>());
				bs.getStmts().add(new ExpressionStmt(assign));
				Expression left = new BinaryExpr(new NameExpr(varName), new NullLiteralExpr(),
						japa.parser.ast.expr.BinaryExpr.Operator.equals);
				if (t.getLocalCache() != null) {
					bs.getStmts().add(
							new ExpressionStmt(new AssignExpr(new NameExpr(varName + "LastModified"),
									new MethodCallExpr(new NameExpr(t.getJavaClassName() + "Helper.local"
											+ t.getJavaClassName() + "Map"), "getLastModifiedTime"), Operator.assign)));
					left = new EnclosedExpr(new BinaryExpr(new BinaryExpr(left, new BinaryExpr(new NameExpr(varName
							+ "LastModified"), new NullLiteralExpr(), japa.parser.ast.expr.BinaryExpr.Operator.equals),
							japa.parser.ast.expr.BinaryExpr.Operator.or), new UnaryExpr(new MethodCallExpr(
							new NameExpr(varName + "LastModified"), "equals", new NameExpr("lastModified")),
							japa.parser.ast.expr.UnaryExpr.Operator.not), japa.parser.ast.expr.BinaryExpr.Operator.or));
				}
				IfStmt ifStmt = new IfStmt(new BinaryExpr(left, new BinaryExpr(new NameExpr(idVarName),
						new NullLiteralExpr(), japa.parser.ast.expr.BinaryExpr.Operator.notEquals),
						japa.parser.ast.expr.BinaryExpr.Operator.and), bs, null);
				ls.add(ifStmt);
				NameExpr expr = new NameExpr(varName);
				ls.add(new ReturnStmt(expr));
				autoGenerateStatements(d.getBody(), "autogenerated:return(" + methodName + ")", ls);
			}
		}
		for (ForeignKey v : table.getForeignGenVarLists()) {
			Table table = v.getTable();
			String columnName = v.getVarColumn();
			if (!(v.getIdListVarName() == null || v.getIdListVarName().trim().isEmpty())) {
				this.addImport("java.util.List");
				String varName = StringHelper.toVarName(v.getIdListVarName());
				Column column = table.findColumnByName(columnName);
				ClassOrInterfaceType type = new ClassOrInterfaceType("List");
				type.setTypeArgs(new LinkedList<Type>());
				type.getTypeArgs().add(
						new ReferenceType(new ClassOrInterfaceType(column.getDataType().getJavaClassName())));
				FieldDeclaration item = new FieldDeclaration(ModifierSet.PRIVATE, type, new VariableDeclarator(
						new VariableDeclaratorId(StringHelper.toVarName(v.getIdListVarName()))));
				JPHelper.addOrUpdateFieldsToClass(mainClass, item);

				type = new ClassOrInterfaceType("List");
				type.setTypeArgs(new LinkedList<Type>());
				type.getTypeArgs().add(
						new ReferenceType(new ClassOrInterfaceType(column.getDataType().getJavaClassName())));
				String methodName = column.getDataType().getGetMethodName(StringHelper.toVarName(v.getIdListVarName()));
				MethodDeclaration d = new MethodDeclaration(Modifier.PUBLIC, type, methodName);
				d = JPHelper.addOrUpdateMethod(mainClass, d, true);
				List<Statement> ls = new LinkedList<Statement>();
				Expression expr = new NameExpr(varName);
				ls.add(new ReturnStmt(expr));
				autoGenerateStatements(d.getBody(), "autogenerated:return(" + methodName + ")", ls);

				type = new ClassOrInterfaceType("List");
				type.setTypeArgs(new LinkedList<Type>());
				type.getTypeArgs().add(
						new ReferenceType(new ClassOrInterfaceType(column.getDataType().getJavaClassName())));
				methodName = column.getDataType().getSetMethodName(StringHelper.toVarName(v.getIdListVarName()));
				List<Parameter> params = new LinkedList<Parameter>();
				params.add(new Parameter(type, new VariableDeclaratorId("v")));
				d = new MethodDeclaration(Modifier.PUBLIC, new VoidType(), methodName, params);
				d = JPHelper.addOrUpdateMethod(mainClass, d, true);
				ls = new LinkedList<Statement>();
				ls.add(new ExpressionStmt(new AssignExpr(new NameExpr(varName), new NameExpr("v"), Operator.assign)));
				autoGenerateStatements(d.getBody(), "autogenerated:body(" + methodName + ")", ls);
				Table ft = generator.database.getTables().get(v.getVarTable());
				ls = new LinkedList<Statement>();
				if (ft.getLocalCache() != null || ft.getCacheConfig() != null) {
					this.addImport(ft.getFullHelperClassName());
					String idVarName = varName;
					varName = StringHelper.toVarName(v.getObjListVarName());
					type = new ClassOrInterfaceType("List");
					type.setTypeArgs(new LinkedList<Type>());
					this.addImport(ft.getFullBeanClassName());
					type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(ft.getJavaClassName())));
					item = new FieldDeclaration(ModifierSet.PRIVATE, type, new VariableDeclarator(
							new VariableDeclaratorId(StringHelper.toVarName(v.getObjListVarName()))));
					JPHelper.addOrUpdateFieldsToClass(mainClass, item);
					if (ft.getLocalCache() != null) {
						this.addImport("java.util.Date");
						item = new FieldDeclaration(ModifierSet.PRIVATE, new ReferenceType(new ClassOrInterfaceType(
								"Date")), new VariableDeclarator(new VariableDeclaratorId(StringHelper.toVarName(v
								.getObjListVarName()) + "LastModified")));
						JPHelper.addOrUpdateFieldsToClass(mainClass, item);
					}
					type = new ClassOrInterfaceType("List");
					type.setTypeArgs(new LinkedList<Type>());
					type.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(ft.getJavaClassName())));
					methodName = column.getDataType().getGetMethodName(StringHelper.toVarName(v.getObjListVarName()));
					d = new MethodDeclaration(Modifier.PUBLIC, type, methodName);
					d = JPHelper.addOrUpdateMethod(mainClass, d, true);
					if (ft.getLocalCache() != null) {
						ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(
								new ClassOrInterfaceType("Date")), new VariableDeclarator(new VariableDeclaratorId(
								"lastModified"), new MethodCallExpr(new NameExpr(ft.getJavaClassName() + "Helper.local"
								+ ft.getJavaClassName() + "Map"), "getLastModifiedTime")))));
					}
					AssignExpr assign = new AssignExpr();
					assign.setTarget(new NameExpr(varName));
					List<Expression> ns = new LinkedList<Expression>();
					ns.add(new NameExpr(idVarName));
					if (ft.getLocalCache() != null) {
						assign.setValue(new MethodCallExpr(new NameExpr(ft.getJavaClassName() + "Helper.local"
								+ ft.getJavaClassName() + "Map"), "gets", ns));
					} else if (ft.getCacheConfig() != null) {
						assign.setValue(new MethodCallExpr(new NameExpr(ft.getJavaClassName() + "Helper."
								+ StringHelper.firstWordLower(ft.getJavaClassName()) + "Map"), "gets", ns));
					}
					assign.setOperator(Operator.assign);
					BlockStmt bs = new BlockStmt(new LinkedList<Statement>());
					bs.getStmts().add(new ExpressionStmt(assign));
					Expression left = new BinaryExpr(new NameExpr(varName), new NullLiteralExpr(),
							japa.parser.ast.expr.BinaryExpr.Operator.equals);
					if (ft.getLocalCache() != null) {
						bs.getStmts().add(
								new ExpressionStmt(new AssignExpr(new NameExpr(varName + "LastModified"),
										new MethodCallExpr(new NameExpr(ft.getJavaClassName() + "Helper.local"
												+ ft.getJavaClassName() + "Map"), "getLastModifiedTime"),
										Operator.assign)));
						left = new EnclosedExpr(new BinaryExpr(new BinaryExpr(left, new BinaryExpr(new NameExpr(varName
								+ "LastModified"), new NullLiteralExpr(),
								japa.parser.ast.expr.BinaryExpr.Operator.equals),
								japa.parser.ast.expr.BinaryExpr.Operator.or), new UnaryExpr(new MethodCallExpr(
								new NameExpr(varName + "LastModified"), "equals", new NameExpr("lastModified")),
								japa.parser.ast.expr.UnaryExpr.Operator.not),
								japa.parser.ast.expr.BinaryExpr.Operator.or));
					}
					IfStmt ifStmt = new IfStmt(new BinaryExpr(left, new BinaryExpr(new NameExpr(idVarName),
							new NullLiteralExpr(), japa.parser.ast.expr.BinaryExpr.Operator.notEquals),
							japa.parser.ast.expr.BinaryExpr.Operator.and), bs, null);
					ls.add(ifStmt);
					expr = new NameExpr(varName);
					ls.add(new ReturnStmt(expr));
				}
				autoGenerateStatements(d.getBody(), "autogenerated:return(" + methodName + ")", ls);
			}
		}
	}

	private void generateImplements() throws IOException {
		MethodDeclaration md = null;
		if (pkClass != null) {
			// getIdString
			md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")),
					"getIdString", null, new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			List<Statement> ls = new LinkedList<Statement>();
			try {
				ls.add(table.getPk().getTableColumns().get(0).getDataType().generateGetIdStringCode());
			} catch (SQLException e) {
				throw new IOException(e);
			}
			autoGenerateStatements(md.getBody(), "autogenerated:return(getIdString)", ls);
			// setIdString
			md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "setIdString", new LinkedList<Parameter>(),
					new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			Parameter p = new Parameter(new ReferenceType(new ClassOrInterfaceType("String")),
					new VariableDeclaratorId("v"));
			md.getParameters().add(p);
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			ls = new LinkedList<Statement>();
			try {
				ls.add(table.getPk().getTableColumns().get(0).getDataType().generateSetIdStringCode());
			} catch (SQLException e) {
				throw new IOException(e);
			}
			autoGenerateStatements(md.getBody(), "autogenerated:body(setIdString)", ls);
			// compareId
			md = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(Primitive.Int), "compareId",
					new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			p = new Parameter(new ReferenceType(new ClassOrInterfaceType(pkClass)), new VariableDeclaratorId("id1"));
			md.getParameters().add(p);
			p = new Parameter(new ReferenceType(new ClassOrInterfaceType(pkClass)), new VariableDeclaratorId("id2"));
			md.getParameters().add(p);
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			ls = new LinkedList<Statement>();
			try {
				ls.addAll(table.getPk().getTableColumns().get(0).getDataType().generateCompareIdCode());
			} catch (SQLException e) {
				throw new IOException(e);
			}
			autoGenerateStatements(md.getBody(), "autogenerated:return(compareId)", ls);
		}
		// newInstance
		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				table.getJavaClassName())), "newInstance", null, new LinkedList<AnnotationExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> ls = new LinkedList<Statement>();
		try {
			ls.add(new ReturnStmt(new ObjectCreationExpr(null, new ClassOrInterfaceType(table.getJavaClassName()))));
		} catch (Throwable e) {
			throw new IOException(e);
		}
		autoGenerateStatements(md.getBody(), "autogenerated:return(newInstance)", ls);
		// setNull
		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "setNull", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters().add(new Parameter(new PrimitiveType(Primitive.Boolean), new VariableDeclaratorId("v")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		ls = new LinkedList<Statement>();
		try {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new NameExpr("v"));
			ls.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("super"), "setNull", args)));
			BlockStmt thenStmt = new BlockStmt(new LinkedList<Statement>());
			args = new LinkedList<Expression>();
			args.add(new NameExpr(table.getNullId()));
			thenStmt.getStmts().add(new ExpressionStmt(new MethodCallExpr(null, "setId", args)));
			IfStmt ifs = new IfStmt(new NameExpr("v"), thenStmt, null);
			ls.add(ifs);
		} catch (Throwable e) {
			throw new IOException(e);
		}
		autoGenerateStatements(md.getBody(), "autogenerated:return(setNull)", ls);
		// toJson
		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "toJson", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("JSONObject")), new VariableDeclaratorId(
						"json")));
		md.getThrows().add(new NameExpr("JSONException"));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		ls = new LinkedList<Statement>();
		try {
			List<Expression> args = new LinkedList<Expression>();
			args.add(new NameExpr("json"));
			ls.add(new ExpressionStmt(new MethodCallExpr(new SuperExpr(), "toJson", args)));
			boolean hasTextField = false;
			Column textFieldColumn = null;
			for (Column o : table.getColumns()) {
				if (!pkColumns.contains(o)) {
					if (o.isToJson()) {
						if (o.getName().equals("text"))
							hasTextField = true;
						args = new LinkedList<Expression>();
						args.add(new StringLiteralExpr(o.getName()));
						if (o.getDataType().getCustomJavaClassName() != null) {
							args.add(new MethodCallExpr(new NameExpr(o.getVarName()), "getValue"));
						} else
							args.add(new NameExpr(o.getVarName()));
						ls.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("json"), "put", args)));
						if (o.isToStringField())
							textFieldColumn = o;
					}
				}
			}
			if (!hasTextField && textFieldColumn != null)
				ls.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("json"), "put",
						new StringLiteralExpr("text"), new NameExpr(textFieldColumn.getVarName()))));
		} catch (Throwable e) {
			throw new IOException(e);
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(toJson)", ls);
		generateStream();
		if (uniqueKeyColumn != null) {
			// getUniqueKey
			md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")),
					"getUniqueKey", null, new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			ls = new LinkedList<Statement>();
			try {
				ls.add(new ReturnStmt(new MethodCallExpr(null, uniqueKeyColumn.getDataType().getGetMethodName(
						uniqueKeyColumn.getVarName()))));
			} catch (Throwable e) {
				throw new IOException(e);
			}
			autoGenerateStatements(md.getBody(), "autogenerated:return(getUniqueKey)", ls);
			// setUniqueKey
			md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "setUniqueKey", new LinkedList<Parameter>(),
					new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			md.getParameters()
					.add(new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId(
							"v")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			ls = new LinkedList<Statement>();
			try {
				List<Expression> args = new LinkedList<Expression>();
				args.add(new NameExpr("v"));
				ls.add(new ExpressionStmt(new MethodCallExpr(null, uniqueKeyColumn.getDataType().getSetMethodName(
						uniqueKeyColumn.getVarName()), args)));
			} catch (Throwable e) {
				throw new IOException(e);
			}
			autoGenerateStatements(md.getBody(), "autogenerated:body(setUniqueKey)", ls);
		}
	}

	private void generateStream() {
		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "doReadFromStream",
				new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getThrows().add(new NameExpr("IOException"));
		Parameter p = new Parameter(new ReferenceType(new ClassOrInterfaceType("DataRead")), new VariableDeclaratorId(
				"stream"));
		md.getParameters().add(p);
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		List<Statement> stmts = new LinkedList<Statement>();
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("super"), "doReadFromStream", new NameExpr(
				"stream"))));
		for (Column o : table.getColumns()) {
			if (!o.isStreamable() || generator.isStandardColumn(o) || o.getAutoConvertColumn() != null)
				continue;
			if (pkColumns != null && pkColumns.contains(o)) {
				stmts.add(new ExpressionStmt(o.getDataType().generateReadFromStreamCode(
						new MethodCallExpr(null, "setId"))));
			} else if (o.getDataType().getCustomJavaClassName() != null) {
				List<Expression> args1 = new LinkedList<Expression>();
				args1.add(new NameExpr("stream"));
				List<Expression> args = new LinkedList<Expression>();
				args.add(new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()), "readFromStream",
						args1));
				MethodCallExpr mce = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()), args);
				stmts.add(new ExpressionStmt(mce));
			} else
				stmts.add(new ExpressionStmt(o.getDataType().generateReadFromStreamCode(
						new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName())))));
		}
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o) || o.getAutoConvertColumn() == null)
				continue;
			Column fc = table.findColumnByName(o.getAutoConvertColumn());
			if (o.getAutoConvertMethod().equalsIgnoreCase("jp")) {
				stmts.add(new ExpressionStmt(new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()),
						new MethodCallExpr(new NameExpr("PinYinHelper"), "getHanyuPinyinFirstChar", new MethodCallExpr(
								null, fc.getDataType().getGetMethodName(fc.getVarName())), new IntegerLiteralExpr(o
								.getLength() + "")))));
			} else if (o.getAutoConvertMethod().equalsIgnoreCase("qp")) {
				stmts.add(new ExpressionStmt(new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()),
						new MethodCallExpr(new NameExpr("PinYinHelper"), "getHanyuPinyin", new MethodCallExpr(null, fc
								.getDataType().getGetMethodName(fc.getVarName())), new IntegerLiteralExpr(o.getLength()
								+ "")))));
			}
		}
		for (ForeignKey v : table.getForeignGenVarLists()) {
			if (!(v.getIdListVarName() == null || v.getIdListVarName().trim().isEmpty())) {
				String varName = StringHelper.toVarName(v.getIdListVarName());
				Column column = v.getTable().findColumnByName(v.getVarColumn());
				if (column.getDataType().getCustomJavaClassName() == null)
					stmts.add(new ExpressionStmt(column.getDataType().generateForeignVarReadFromStreamCode(
							new MethodCallExpr(null, column.getDataType().getSetMethodName(varName)))));
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(doReadFromStream)", stmts);
		// doWriteToStream
		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "doWriteToStream", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getThrows().add(new NameExpr("IOException"));
		p = new Parameter(new ReferenceType(new ClassOrInterfaceType("DataWrite")), new VariableDeclaratorId("stream"));
		md.getParameters().add(p);
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts = new LinkedList<Statement>();
		stmts.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("super"), "doWriteToStream",
				new NameExpr("stream"))));
		for (Column o : table.getColumns()) {
			if (!o.isStreamable() || generator.isStandardColumn(o) || o.getAutoConvertColumn() != null)
				continue;
			if (pkColumns != null && pkColumns.contains(o)) {
				stmts.add(new ExpressionStmt(o.getDataType().generateWriteToStreamCode(
						new MethodCallExpr(null, "getId"))));
			} else if (o.getDataType().getCustomJavaClassName() != null) {
				List<Expression> args = new LinkedList<Expression>();
				args.add(new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName())));
				args.add(new NameExpr("stream"));
				MethodCallExpr mce = new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()),
						"writeToStream", args);
				stmts.add(new ExpressionStmt(mce));
			} else
				stmts.add(new ExpressionStmt(o.getDataType().generateWriteToStreamCode(
						new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName())))));
		}
		for (ForeignKey v : table.getForeignGenVarLists()) {
			if (v.getIdListVarName() != null && !v.getIdListVarName().trim().isEmpty()) {
				String varName = StringHelper.toVarName(v.getIdListVarName());
				Column column = v.getTable().findColumnByName(v.getVarColumn());
				if (column.getDataType().getCustomJavaClassName() == null)
					stmts.add(new ExpressionStmt(column.getDataType().generateForeignVarWriteToStreamCode(
							new MethodCallExpr(null, column.getDataType().getGetMethodName(varName)))));
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(doWriteToStream)", stmts);
		// readFromDb
		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "readFromDb", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getThrows().add(new NameExpr("SQLException"));
		p = new Parameter(new ReferenceType(new ClassOrInterfaceType("DaoResultSet")), new VariableDeclaratorId("rset"));
		md.getParameters().add(p);
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts = new LinkedList<Statement>();
		List<Expression> args = new LinkedList<Expression>();
		args.add(new NameExpr("rset"));
		MethodCallExpr mce = new MethodCallExpr(new SuperExpr(), "readFromDb", args);
		stmts.add(new ExpressionStmt(mce));
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			if (pkColumns != null && pkColumns.contains(o)) {
				stmts.add(new ExpressionStmt(o.getDataType().generateReadFromDbCode(new MethodCallExpr(null, "setId"),
						o.getName())));
			} else if (o.getDataType().getCustomJavaClassName() != null) {
				List<Expression> args2 = new LinkedList<Expression>();
				args2.add(new StringLiteralExpr(o.getName()));
				List<Expression> args1 = new LinkedList<Expression>();
				args1.add(new MethodCallExpr(new NameExpr("rset"), "getInt", args2));
				args = new LinkedList<Expression>();
				args.add(new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()), "valueOf", args1));
				mce = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()), args);
				stmts.add(new ExpressionStmt(mce));
			} else
				stmts.add(new ExpressionStmt(o.getDataType().generateReadFromDbCode(
						new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName())), o.getName())));
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(readFromDb)", stmts);
		// readFromRequest
		md = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "readFromRequest", new LinkedList<Parameter>(),
				new LinkedList<AnnotationExpr>(), new LinkedList<NameExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getThrows().add(new NameExpr("Exception"));
		p = new Parameter(new ReferenceType(new ClassOrInterfaceType("HttpRequest")), new VariableDeclaratorId(
				"request"));
		md.getParameters().add(p);
		p = new Parameter(new PrimitiveType(Primitive.Boolean), new VariableDeclaratorId("isCreate"));
		md.getParameters().add(p);
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		stmts = new LinkedList<Statement>();
		args = new LinkedList<Expression>();
		args.add(new NameExpr("request"));
		args.add(new NameExpr("isCreate"));
		mce = new MethodCallExpr(new SuperExpr(), "readFromRequest", args);
		stmts.add(new ExpressionStmt(mce));
		List<Column> createOnlyList = new ArrayList<Column>();
		List<Column> editOnlyList = new ArrayList<Column>();
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o) || o.getAutoConvertColumn() != null)
				continue;
			if (o.getUserInputMode().equals("createonly")) {
				createOnlyList.add(o);
				continue;
			} else if (o.getUserInputMode().equals("editonly")) {
				editOnlyList.add(o);
				continue;
			} else if (o.getUserInputMode().equals("none"))
				continue;
			if (pkColumns != null && pkColumns.contains(o)) {
				stmts.add(new ExpressionStmt(o.getDataType().generateReadFromRequestCode(
						new MethodCallExpr(null, "setId"), o.getName(), this)));
			} else if (o.getDataType().getCustomJavaClassName() != null) {
				List<Expression> args2 = new LinkedList<Expression>();
				args2.add(new StringLiteralExpr(o.getName()));
				List<Expression> args1 = new LinkedList<Expression>();
				args1.add(new MethodCallExpr(new NameExpr("request"), "getParameterInt", args2));
				args = new LinkedList<Expression>();
				args.add(new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()), "valueOf", args1));
				mce = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()), args);
				stmts.add(new ExpressionStmt(mce));
			} else {
				MethodCallExpr expr = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()));
				stmts.add(new ExpressionStmt(expr));
				o.getDataType().generateReadFromRequestCode(expr, o.getName(), this);
			}
		}
		if (createOnlyList.size() > 0 || editOnlyList.size() > 0) {
			BlockStmt thenStmt = new BlockStmt(new LinkedList<Statement>());
			BlockStmt elseStmt = editOnlyList.size() > 0 ? new BlockStmt(new LinkedList<Statement>()) : null;
			stmts.add(new IfStmt(new NameExpr("isCreate"), thenStmt, elseStmt));
			for (Column o : createOnlyList) {
				if (pkColumns != null && pkColumns.contains(o)) {
					thenStmt.getStmts().add(
							new ExpressionStmt(o.getDataType().generateReadFromRequestCode(
									new MethodCallExpr(null, "setId"), o.getName(), this)));
				} else if (o.getDataType().getCustomJavaClassName() != null) {
					List<Expression> args2 = new LinkedList<Expression>();
					args2.add(new StringLiteralExpr(o.getVarName()));
					List<Expression> args1 = new LinkedList<Expression>();
					args1.add(new MethodCallExpr(new NameExpr("request"), "getParameterInt", args2));
					args = new LinkedList<Expression>();
					args.add(new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()), "valueOf",
							args1));
					mce = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()), args);
					thenStmt.getStmts().add(new ExpressionStmt(mce));
				} else {
					MethodCallExpr expr = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()));
					thenStmt.getStmts().add(new ExpressionStmt(expr));
					o.getDataType().generateReadFromRequestCode(expr, o.getName(), this);
				}
			}
			for (Column o : editOnlyList) {
				if (pkColumns != null && pkColumns.contains(o)) {
					elseStmt.getStmts().add(
							new ExpressionStmt(o.getDataType().generateReadFromRequestCode(
									new MethodCallExpr(null, "setId"), o.getName(), this)));
				} else if (o.getDataType().getCustomJavaClassName() != null) {
					List<Expression> args2 = new LinkedList<Expression>();
					args2.add(new StringLiteralExpr(o.getVarName()));
					List<Expression> args1 = new LinkedList<Expression>();
					args1.add(new MethodCallExpr(new NameExpr("request"), "getParameterInt", args2));
					args = new LinkedList<Expression>();
					args.add(new MethodCallExpr(new NameExpr(o.getDataType().getCustomJavaClassName()), "valueOf",
							args1));
					mce = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()), args);
					elseStmt.getStmts().add(new ExpressionStmt(mce));
				} else {
					MethodCallExpr expr = new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()));
					elseStmt.getStmts().add(new ExpressionStmt(expr));
					o.getDataType().generateReadFromRequestCode(expr, o.getName(), this);
				}
			}
		}
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o) || o.getAutoConvertColumn() == null)
				continue;
			Column fc = table.findColumnByName(o.getAutoConvertColumn());
			if (o.getAutoConvertMethod().equalsIgnoreCase("jp")) {
				stmts.add(new ExpressionStmt(new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()),
						new MethodCallExpr(new NameExpr("PinYinHelper"), "getHanyuPinyinFirstChar", new MethodCallExpr(
								null, fc.getDataType().getGetMethodName(fc.getVarName())), new IntegerLiteralExpr(o
								.getLength() + "")))));
			} else if (o.getAutoConvertMethod().equalsIgnoreCase("qp")) {
				stmts.add(new ExpressionStmt(new MethodCallExpr(null, o.getDataType().getSetMethodName(o.getVarName()),
						new MethodCallExpr(new NameExpr("PinYinHelper"), "getHanyuPinyin", new MethodCallExpr(null, fc
								.getDataType().getGetMethodName(fc.getVarName())), new IntegerLiteralExpr(o.getLength()
								+ "")))));
			}
		}
		for (ForeignKey v : table.getForeignGenVarLists()) {
			if (!v.getVarTable().equals(v.getTable().getName())) {
				this.addImport("kitty.kaf.helper.StringHelper");
				String varName = StringHelper.toVarName(v.getIdListVarName());
				Column column = v.getTable().findColumnByName(v.getColumn());
				if (column.getDataType().getCustomJavaClassName() == null)
					stmts.add(new ExpressionStmt(column.getDataType().generateForeignVarReadFromRequestCode(
							new MethodCallExpr(null, column.getDataType().getSetMethodName(varName)),
							v.getIdListVarName().trim(), this)));
			}
		}
		for (Column o : table.getColumns()) {
			if (o.isFile()) {
				List<Statement> ts = new LinkedList<Statement>();
				stmts.add(new IfStmt(new BinaryExpr(new NameExpr(o.getVarName()), new NullLiteralExpr(),
						japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new BlockStmt(ts), null));
				ts.add(new ExpressionStmt(new AssignExpr(new NameExpr(o.getVarName()), new MethodCallExpr(new NameExpr(
						o.getVarName()), "trim"), Operator.assign)));
				ts.add(new IfStmt(new MethodCallExpr(new NameExpr(o.getVarName()), "isEmpty"), new ExpressionStmt(
						new AssignExpr(new NameExpr(o.getVarName()), new NullLiteralExpr(), Operator.assign)), null));
				ts = new LinkedList<Statement>();
				stmts.add(new IfStmt(new BinaryExpr(new NameExpr(o.getVarName()), new NullLiteralExpr(),
						japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new BlockStmt(ts), null));
				ts.add(new ExpressionStmt(new AssignExpr(new NameExpr(o.getVarName() + "HostId"), new MethodCallExpr(
						new MethodCallExpr(new NameExpr("FileCategoryHelper.localFileCategoryMap"), "getByName",
								new StringLiteralExpr(o.getName())), "getCurFileHostId"), Operator.assign)));
			}
		}
		autoGenerateStatements(md.getBody(), "autogenerated:body(readFromRequest)", stmts);
	}

	private void generateTableDef() {
		FieldDeclaration fd = JPHelper.findFieldDeclartion(mainClass, "serialVersionUID");
		if (fd == null) {
			fd = new FieldDeclaration(ModifierSet.PRIVATE | ModifierSet.STATIC | ModifierSet.FINAL, new PrimitiveType(
					Primitive.Long), new VariableDeclarator(new VariableDeclaratorId("serialVersionUID"),
					new LongLiteralExpr("1L")));
			((ClassOrInterfaceDeclaration) mainClass).getMembers().add(0, fd);
		}
		List<BodyDeclaration> ls = new LinkedList<BodyDeclaration>();
		List<Expression> args = new ArrayList<Expression>();
		args.add(new StringLiteralExpr(table.getName()));
		args.add(new StringLiteralExpr(table.getDesp()));
		if (table.getSecondTables().size() > 0) {
			List<Expression> values = new LinkedList<Expression>();
			ArrayCreationExpr expr = new ArrayCreationExpr(new ClassOrInterfaceType("String"), table.getSecondTables()
					.size(), new ArrayInitializerExpr(values));
			args.add(expr);
			for (Table t : table.getSecondTables())
				values.add(new StringLiteralExpr(t.getName()));
		} else
			args.add(new NullLiteralExpr());
		ObjectCreationExpr inite = new ObjectCreationExpr(null, new ClassOrInterfaceType("TableDef"), args);
		VariableDeclarator variable = new VariableDeclarator(new VariableDeclaratorId("tableDef"), inite);
		fd = new FieldDeclaration(ModifierSet.STATIC | ModifierSet.PUBLIC | ModifierSet.FINAL, new ReferenceType(
				new ClassOrInterfaceType("TableDef")), variable);
		ls.add(fd);
		InitializerDeclaration init = new InitializerDeclaration(true, new BlockStmt(new LinkedList<Statement>()));
		int i = 0;
		for (Column o : table.getColumns()) {
			MethodCallExpr mce = new MethodCallExpr(new NameExpr("tableDef"), "getColumns");
			args = new ArrayList<Expression>();
			args.add(new IntegerLiteralExpr(Integer.toString(i++)));
			args.add(new StringLiteralExpr(o.getDesp()));
			args.add(new StringLiteralExpr(o.getName()));
			args.add(new IntegerLiteralExpr(o.getDataType().getGafDataType() + ""));
			args.add(new IntegerLiteralExpr(Integer.toString(o.getMaxLength())));
			args.add(new IntegerLiteralExpr(Integer.toString(o.getDigits())));
			args.add(new BooleanLiteralExpr(o.isUniqueKeyField()));
			args.add(o.getSequence() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getSequence()));
			args.add(new BooleanLiteralExpr(o.isSecret()));
			if (o.getUpdateDbMode().equals("createonly"))
				args.add(new IntegerLiteralExpr("1"));
			else if (o.getUpdateDbMode().equals("editonly"))
				args.add(new IntegerLiteralExpr("2"));
			else
				args.add(new IntegerLiteralExpr("0"));
			args.add(new BooleanLiteralExpr(o.isToStringField()));
			args.add(new IntegerLiteralExpr(o.getMinLength() + ""));
			args.add(o.getMinValue() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getMinValue()));
			args.add(o.getMaxValue() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getMaxValue()));
			args.add(o.getErrorPrompt() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getErrorPrompt()));
			args.add(o.getRegExp() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getRegExp().replace("\\",
					"\\\\")));
			args.add(new BooleanLiteralExpr(o.isAutoIncrement()));
			args.add(new BooleanLiteralExpr(o.isNullable()));
			args.add(o.getSerialKey() == null ? new NullLiteralExpr() : new StringLiteralExpr(o.getSerialKey()));
			ObjectCreationExpr create = new ObjectCreationExpr(null, new ClassOrInterfaceType("TableColumnDef"), args);
			args = new ArrayList<Expression>();
			args.add(new StringLiteralExpr(o.getVarName()));
			args.add(create);
			MethodCallExpr put = new MethodCallExpr(mce, "put", args);
			init.getBlock().getStmts().add(new ExpressionStmt(put));
		}
		MethodCallExpr mce = new MethodCallExpr(new NameExpr("tableDef"), "setPkColumns", new LinkedList<Expression>());
		if (pkColumns != null)
			for (Column o : pkColumns) {
				mce.getArgs().add(new StringLiteralExpr(o.getVarName()));
			}
		init.getBlock().getStmts().add(new ExpressionStmt(mce));
		ls.add(init);
		autoGenerateMembers(mainClass, "autogenerated:static(tableDef)", ls);

		MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				"TableDef")), "getTableDef", null, new LinkedList<AnnotationExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		LinkedList<Statement> stmts = new LinkedList<Statement>();
		stmts.add(new ReturnStmt(new NameExpr("tableDef")));
		autoGenerateStatements(md.getBody(), "autogenerated:return(getTableDef)", stmts);

		md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(new ClassOrInterfaceType("Object")),
				"getByColumn", new LinkedList<Parameter>(), new LinkedList<AnnotationExpr>());
		md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
		md.getParameters().add(
				new Parameter(new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclaratorId(
						"columnName")));
		md = JPHelper.addOrUpdateMethod(mainClass, md, true);
		IfStmt ifStmt = null, root = null;
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			MethodCallExpr condition = new MethodCallExpr(new NameExpr("columnName"), "equalsIgnoreCase",
					new LinkedList<Expression>());
			condition.getArgs().add(new StringLiteralExpr(o.getName()));
			MethodCallExpr then = null;
			if (pkColumns != null && pkColumns.contains(o)) {
				then = new MethodCallExpr(null, "getId");
			} else {
				then = new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName()));
			}
			if (ifStmt == null) {
				root = ifStmt = new IfStmt(condition, new ReturnStmt(then), null);
			} else {
				ifStmt.setElseStmt(new IfStmt(condition, new ReturnStmt(then), null));
				ifStmt = (IfStmt) ifStmt.getElseStmt();
			}
		}
		stmts = new LinkedList<Statement>();
		if (root != null)
			stmts.add(root);
		args = new LinkedList<Expression>();
		args.add(new NameExpr("columnName"));
		stmts.add(new ReturnStmt(new MethodCallExpr(new SuperExpr(), "getByColumn", args)));
		autoGenerateStatements(md.getBody(), "autogenerated:return(getByColumn)", stmts);
	}

	private void generateFields() {
		Column toStringField = null;
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			if (o.isToStringField())
				toStringField = o;
			if (pkColumns == null || !pkColumns.contains(o)) {
				Expression init = o.getDataType().getDefaultInit(null, this);
				FieldDeclaration d = new FieldDeclaration(Modifier.PRIVATE, new ReferenceType(new ClassOrInterfaceType(
						o.getDataType().getJavaClassName())), new VariableDeclarator(new VariableDeclaratorId(
						o.getVarName()), init));
				d.setJavaDoc(new JavadocComment(o.getDesp()));
				JPHelper.addOrUpdateFieldsToClass(mainClass, d);
			}
		}
		// get methods
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			boolean isPk = !(pkColumns == null || !pkColumns.contains(o));
			String methodName = o.getDataType().getGetMethodName(o.getVarName());
			MethodDeclaration d = new MethodDeclaration(Modifier.PUBLIC, new ReferenceType(new ClassOrInterfaceType(o
					.getDataType().getJavaClassName())), methodName);
			d.setJavaDoc(new JavadocComment("获得" + o.getDesp()));
			d = JPHelper.addOrUpdateMethod(mainClass, d, true);
			List<Statement> ls = new LinkedList<Statement>();
			Expression expr;
			if (isPk) {
				expr = new MethodCallExpr(null, "getId");
			} else
				expr = new NameExpr(o.getVarName());
			ls.add(new ReturnStmt(expr));
			autoGenerateStatements(d.getBody(), "autogenerated:return(" + methodName + ")", ls);
			if (o.isFile()) {
				generateFileMethods(o);
			}
		}
		// set methods
		for (Column o : table.getColumns()) {
			if (generator.isStandardColumn(o))
				continue;
			boolean isPk = !(pkColumns == null || !pkColumns.contains(o));
			String methodName = o.getDataType().getSetMethodName(o.getVarName());
			MethodDeclaration d = new MethodDeclaration(Modifier.PUBLIC, new VoidType(), methodName,
					new LinkedList<Parameter>());
			d.setJavaDoc(new JavadocComment("设置" + o.getDesp()));
			Parameter p = new Parameter(
					new ReferenceType(new ClassOrInterfaceType(o.getDataType().getJavaClassName())),
					new VariableDeclaratorId("v"));
			d.getParameters().add(p);
			d = JPHelper.addOrUpdateMethod(mainClass, d, true);
			List<Statement> ls = new LinkedList<Statement>();
			Statement st;
			if (isPk) {
				List<Expression> exps = new LinkedList<Expression>();
				exps.add(new NameExpr("v"));
				st = new ExpressionStmt(new MethodCallExpr(null, "setId", exps));
			} else {
				st = new ExpressionStmt(
						new AssignExpr(new NameExpr(o.getVarName()), new NameExpr("v"), Operator.assign));
				if (o.isSecret()
						|| (o.getDataType() instanceof StringColumnDataType && o.getUpdateDbMode().equals("notnull"))) {
					st = new IfStmt(new BinaryExpr(new BinaryExpr(new NameExpr("v"), new NullLiteralExpr(),
							japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new BinaryExpr(new MethodCallExpr(
							new NameExpr("v"), "length"), new IntegerLiteralExpr("0"),
							japa.parser.ast.expr.BinaryExpr.Operator.greater),
							japa.parser.ast.expr.BinaryExpr.Operator.and), st, null);
				} else if (o.getUpdateDbMode().equals("notnull")) {
					st = new IfStmt(new BinaryExpr(new NameExpr("v"), new NullLiteralExpr(),
							japa.parser.ast.expr.BinaryExpr.Operator.notEquals), st, null);
				}
			}
			ls.add(st);
			autoGenerateStatements(d.getBody(), "autogenerated:body(" + methodName + ")", ls);
		}
		if (toStringField != null) {
			Expression expr = null;
			for (ForeignKey k : table.getForeignGenVars()) {
				if (k.getColumn().equals(toStringField.getName())) {
					addImport("kitty.kaf.helper.StringHelper");
					expr = new MethodCallExpr(new NameExpr("StringHelper"), "toString", new MethodCallExpr(null,
							toStringField.getDataType().getGetMethodName(StringHelper.toVarName(k.getObjVarName()))));
				}
			}
			if (expr == null) {
				if (toStringField.getDataType() instanceof StringColumnDataType) {
					expr = new NameExpr(toStringField.getVarName());
				} else {
					addImport("kitty.kaf.helper.StringHelper");
					expr = new MethodCallExpr(new NameExpr("StringHelper"), "toString", new NameExpr(
							toStringField.getVarName()));
				}
			}
			MethodDeclaration md = new MethodDeclaration(ModifierSet.PUBLIC, new ReferenceType(
					new ClassOrInterfaceType("String")), "toString", null, new LinkedList<AnnotationExpr>());
			md.getAnnotations().add(new MarkerAnnotationExpr(new NameExpr("Override")));
			md = JPHelper.addOrUpdateMethod(mainClass, md, true);
			List<Statement> stmts = new LinkedList<Statement>();
			stmts.add(new ReturnStmt(expr));
			autoGenerateStatements(md.getBody(), "autogenerated:return(toString)", stmts);
		}
		FieldDeclaration d = new FieldDeclaration(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL,
				new ReferenceType(new ClassOrInterfaceType("String")), new VariableDeclarator(new VariableDeclaratorId(
						"CACHE_KEY_PREFIX"), new StringLiteralExpr("$cache." + table.getJavaClassName().toLowerCase())));
		List<BodyDeclaration> members = new LinkedList<BodyDeclaration>();
		members.add(d);
		autoGenerateMembers(mainClass, "autogenerated:static(CACHE_KEY_PREFIX)", members);
	}

	private void generateFileMethods(Column o) {
		PackageDef def = this.generator.packageDefs.get("file");
		addImport(def.infPackageName + ".beans.FileHost");
		addImport(def.infPackageName + ".beans.FileCategory");
		addImport(def.infPackageName + ".FileCategoryHelper");
		addImport(def.infPackageName + ".FileHostHelper");
		addImport("java.io.IOException");
		addImport("kitty.kaf.pools.ftp.FtpReplyError");
		// get host
		String varName = StringHelper.toVarName(o.getName() + "_host");
		String hostMethodName = o.getDataType().getGetMethodName(varName);
		MethodDeclaration d = new MethodDeclaration(Modifier.PUBLIC, new ReferenceType(new ClassOrInterfaceType(
				"FileHost")), hostMethodName);
		d.setJavaDoc(new JavadocComment("获得" + o.getDesp() + "主机"));
		d = JPHelper.addOrUpdateMethod(mainClass, d, true);
		List<Statement> ls = new LinkedList<Statement>();
		ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("FileHost")),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()))));
		autoGenerateStatements(d.getBody(), "autogenerated:begin(" + hostMethodName + ")", ls);
		ls = new LinkedList<Statement>();
		ls.add(new IfStmt(new BinaryExpr(new NameExpr(varName + "Id"), new IntegerLiteralExpr("-1"),
				japa.parser.ast.expr.BinaryExpr.Operator.greater), new ExpressionStmt(new AssignExpr(
				new NameExpr("ret"), new MethodCallExpr(new NameExpr("FileHostHelper.localFileHostMap"), "get",
						new NameExpr(varName + "Id")), Operator.assign)), null));
		autoGenerateStatements(d.getBody(), "autogenerated:body(" + hostMethodName + ")", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(d.getBody(), "autogenerated:return(" + hostMethodName + ")", ls);
		// get url
		varName = o.getVarName() + "Url";
		String urlMethodName = o.getDataType().getGetMethodName(varName);
		d = new MethodDeclaration(Modifier.PUBLIC, new ReferenceType(new ClassOrInterfaceType("String")), urlMethodName);
		d.setJavaDoc(new JavadocComment("获得" + o.getDesp() + "访问地址"));
		d = JPHelper.addOrUpdateMethod(mainClass, d, true);
		ls = new LinkedList<Statement>();
		ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("String")),
				new VariableDeclarator(new VariableDeclaratorId("ret"), new NullLiteralExpr()))));
		ls.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType("FileHost")),
				new VariableDeclarator(new VariableDeclaratorId("host"), new MethodCallExpr(null, hostMethodName)))));
		autoGenerateStatements(d.getBody(), "autogenerated:begin(" + urlMethodName + ")", ls);
		ls = new LinkedList<Statement>();
		ls.add(new IfStmt(new BinaryExpr(new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName())),
				new NullLiteralExpr(), japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new ExpressionStmt(
				new AssignExpr(new NameExpr("ret"), new MethodCallExpr(new NameExpr("host"), "getWebUrl",
						new MethodCallExpr(new MethodCallExpr(new NameExpr("FileCategoryHelper.localFileCategoryMap"),
								"getByName", new StringLiteralExpr(o.getName())), "getFileCategoryId"),
						new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName()))), Operator.assign)),
				null));
		autoGenerateStatements(d.getBody(), "autogenerated:body(" + urlMethodName + ")", ls);
		ls = new LinkedList<Statement>();
		ls.add(new ReturnStmt(new NameExpr("ret")));
		autoGenerateStatements(d.getBody(), "autogenerated:return(" + urlMethodName + ")", ls);
		// delete
		varName = StringHelper.toVarName("delete_" + o.getName() + "_file");
		String deleteMethodName = varName;
		d = new MethodDeclaration(Modifier.PUBLIC, new VoidType(), deleteMethodName);
		d.setThrows(new LinkedList<NameExpr>());
		d.getThrows().add(new NameExpr("IOException"));
		d.getThrows().add(new NameExpr("FtpReplyError"));
		d.getThrows().add(new NameExpr("InterruptedException"));
		d.setJavaDoc(new JavadocComment("删除" + o.getDesp() + "文件"));
		d = JPHelper.addOrUpdateMethod(mainClass, d, true);
		ls = new LinkedList<Statement>();
		List<Statement> stmts = new LinkedList<Statement>();
		List<Statement> stmts1 = new LinkedList<Statement>();
		ls.add(new IfStmt(new BinaryExpr(new MethodCallExpr(null, o.getDataType().getGetMethodName(o.getVarName())),
				new NullLiteralExpr(), japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new BlockStmt(stmts), null));
		stmts.add(new ExpressionStmt(new VariableDeclarationExpr(
				new ReferenceType(new ClassOrInterfaceType("FileHost")), new VariableDeclarator(
						new VariableDeclaratorId("host"), new MethodCallExpr(null, hostMethodName)))));
		stmts.add(new IfStmt(new BinaryExpr(new NameExpr("host"), new NullLiteralExpr(),
				japa.parser.ast.expr.BinaryExpr.Operator.notEquals), new BlockStmt(stmts1), null));
		stmts1.add(new ExpressionStmt(new VariableDeclarationExpr(new ReferenceType(new ClassOrInterfaceType(
				"FileCategory")), new VariableDeclarator(new VariableDeclaratorId("fc"), new MethodCallExpr(
				new NameExpr("FileCategoryHelper.localFileCategoryMap"), "getByName",
				new StringLiteralExpr(o.getName()))))));
		stmts1.add(new ExpressionStmt(new MethodCallExpr(new NameExpr("host"), "deleteFile", new MethodCallExpr(
				new NameExpr("fc"), "getFileCategoryId"), new MethodCallExpr(null, o.getDataType().getGetMethodName(
				o.getVarName())))));
		autoGenerateStatements(d.getBody(), "autogenerated:body(" + deleteMethodName + ")", ls);
	}

	protected TypeDeclaration generateMainClass() {
		ClassOrInterfaceDeclaration type = JPHelper.AddClassDeclartion(cu, table.getJavaClassName(), false,
				ModifierSet.PUBLIC);
		type.setJavaDoc(new JavadocComment("\r\n * " + table.getDesp() + "\r\n "));
		if (pkClass != null) {
			ReferenceType rt = new ReferenceType(new ClassOrInterfaceType(pkClass));
			JPHelper.updateExtendsToClass(type, "IdTableObject", rt);
		} else
			JPHelper.updateExtendsToClass(type, "TableObject");
		if (uniqueKeyColumn != null) {
			if (type.getImplements() == null)
				type.setImplements(new LinkedList<ClassOrInterfaceType>());
			ClassOrInterfaceType t = new ClassOrInterfaceType("UnuqieKeyCachable");
			t.setTypeArgs(new LinkedList<Type>());
			t.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
			if (!type.getImplements().contains(t))
				type.getImplements().add(t);
		}
		if (table.getLocalCache() != null) {
			if (type.getImplements() == null)
				type.setImplements(new LinkedList<ClassOrInterfaceType>());
			ClassOrInterfaceType t = new ClassOrInterfaceType("LocalCachable");
			t.setTypeArgs(new LinkedList<Type>());
			t.getTypeArgs().add(new ReferenceType(new ClassOrInterfaceType(pkClass)));
			if (!type.getImplements().contains(t))
				type.getImplements().add(t);
		}
		if (table.getImplementsStr() != null) {
			if (type.getImplements() == null)
				type.setImplements(new LinkedList<ClassOrInterfaceType>());
			String[] s = StringHelper.splitToStringArray(table.getImplementsStr(), ",");
			for (String str : s) {
				String[] sa = StringHelper.splitToStringArray(str, ".");
				ClassOrInterfaceType root = null, t = null;
				for (int i = sa.length - 1; i >= 0; i--) {
					if (root == null) {
						root = new ClassOrInterfaceType(sa[i]);
						t = root;
					} else {
						t.setScope(new ClassOrInterfaceType(sa[i]));
						t = t.getScope();
					}
				}
				if (!type.getImplements().contains(root))
					type.getImplements().add(root);
			}
		}
		return type;
	}

}
