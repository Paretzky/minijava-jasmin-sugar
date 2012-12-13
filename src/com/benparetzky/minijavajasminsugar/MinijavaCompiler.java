package com.benparetzky.minijavajasminsugar;

import org.antlr.stringtemplate.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.runtime.Token;
import org.antlr.*;
import antlr.*;
import java.util.*;

public class MinijavaCompiler {
	static enum Type {INT, INTARRAY, BOOLEAN, REFERENCE};
	private static Map<BaseTree,Map<String,String>> scopes = new HashMap<>();
	public static void main(String[] args) throws Exception {
		minijavaLexer lex = new minijavaLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lex); 
		minijavaParser parser = new minijavaParser(tokens);
		try {
			minijavaParser.goal_return r = parser.goal();
			buildScopes(r.tree);
			System.out.println(scopes);
			checkScopes(r.tree);
		} catch (org.antlr.runtime.RecognitionException e)  {
			e.printStackTrace();
		}
		/*
		try {
			CommonTree tree = (CommonTree)parser.goal().getTree();
			DOTTreeGenerator gen = new DOTTreeGenerator();
			StringTemplate  st = gen.toDOT(tree);
			System.out.println(st);
		} catch (org.antlr.runtime.RecognitionException e)  {
			e.printStackTrace();
		}
		*/
	}

	static void buildScopes(BaseTree ret) {
		buildScopes(ret,null);
	}
	static void buildScopes(BaseTree ret, Map<String,String> paramScope) {
		Map<String,String> scope = new HashMap<String,String>();
		if(paramScope != null) {
			scope.putAll(paramScope);
		}
		String s = ret.toString();
		List l = ret.getChildren();
		System.out.println(s);	
		if(l == null) {
			return;
		}
		/*
		if(s.equals("GOAL")) {
			for(Object o : l) {
				BaseTree t = (BaseTree)o;
				//System.out.println("\t" + t);
				if(t.toString().equals("ADDITIONAL_CLASSES") && t.getChildren() != null) {
					for(Object o2 : t.getChildren()) {
						BaseTree t2 = (BaseTree)o2;
						if(t2.toString().equals("ADDITIONAL_CLASS") && t2.getChildren() != null) {
							for(Object o3 : t2.getChildren()) {
								BaseTree t3 = (BaseTree) o3;
								if(t3.toString().equals("NAME") && t3.getChildren() != null) {
									scope.put(t3.getChild(0).toString(),"CLASS");
								}
							}
						}
					}
					scopes.put(ret,scope);
				}
			}
		}
		*/
		if(s.equals("METHOD")) {
			for(Object o : l) {
				BaseTree t = (BaseTree)o;
				//System.out.println("\t" + t);
				if(t.toString().equals("VARDECLS") && t.getChildren() != null) {
					for(Object o2 : t.getChildren()) {
						BaseTree t2 = (BaseTree)o2;
						//System.out.println("\t\t" + t2);
						scope.put(t2.getChild(1).toString(),t2.getChild(0).toString());
					}
				}
			}
			scopes.put(ret,scope);
			return;
		}
		if(s.equals("ADDITIONAL_CLASS")) {
			for(Object o : l) {
				BaseTree t = (BaseTree)o;
				//System.out.println("\t" + t);
				if(t.toString().equals("VARDECLS") && t.getChildren() != null) {
					for(Object o2 : t.getChildren()) {
						BaseTree t2 = (BaseTree)o2;
						//System.out.println("\t\t" + t2);
						scope.put(t2.getChild(1).toString(),t2.getChild(0).toString());
					}
				}
			}
			scopes.put(ret,scope);
			for(Object o : l) {
				buildScopes((BaseTree)o,scope);
			}
			//System.out.println(scope);
			return;
		}
		for(Object o : l) {
			buildScopes((BaseTree)o,scope);
		}
	}

	static void checkScopes(BaseTree ret) { checkScopes(ret,scopes.get(ret)); }
	
	static void checkScopes(BaseTree ret,Map<String,String> scope) {
		String s = ret.toString();
		List l = ret.getChildren();
		System.out.println(s);	
		if(l == null) {
			return;
		}
		if(s.equals("METHOD") || s.equals("ADDITIONAL_CLASS")) {
			for(Object o : l) {
				checkScopes((BaseTree)o,scopes.get(ret));
			}
			return;
		}
		if(s.equals("REFERENCE")) {
			String r = scope.get(ret.getChild(0).toString());
			if(r == null) {
				System.out.println("Unable to find " + ret.getChild(0).toString() + " in " + scope + " at " + ret.getLine() + "," + ret.getCharPositionInLine());
			}
		}
		for(Object o : l) {
			checkScopes((BaseTree)o,scope);
		}
	}


	static final TreeAdaptor tadaptor = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	public static void printTree(CommonTree t, int indent) {
		if ( t != null ) {
			StringBuffer sb = new StringBuffer(indent);
			if (t.getParent() == null){
				System.out.println(sb.toString() + t.getText().toString()+ " " + t.getType());	
			}
			for ( int i = 0; i < indent; i++ )
				sb = sb.append("   ");
				for ( int i = 0; i < t.getChildCount(); i++ ) {
					System.out.println(sb.toString() + t.getChild(i).toString() + " " + t.getType());
					printTree((CommonTree)t.getChild(i), indent+1);
				}
			}
	}
}
