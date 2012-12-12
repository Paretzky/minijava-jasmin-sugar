package com.benparetzky.minijavajasminsugar;

import org.antlr.stringtemplate.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.runtime.Token;
import org.antlr.*;
import antlr.*;

public class MinijavaCompiler {
	public static void main(String[] args) throws Exception {
		minijavaLexer lex = new minijavaLexer(new ANTLRFileStream(args[0]));
		CommonTokenStream tokens = new CommonTokenStream(lex); 
		minijavaParser parser = new minijavaParser(tokens);
		try {
			CommonTree tree = (CommonTree)parser.goal().getTree();
			DOTTreeGenerator gen = new DOTTreeGenerator();
			StringTemplate  st = gen.toDOT(tree);
			System.out.println(st);
		} catch (org.antlr.runtime.RecognitionException e)  {
			e.printStackTrace();
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
				System.out.println(sb.toString() + t.getText().toString());	
			}
			for ( int i = 0; i < indent; i++ )
				sb = sb.append("   ");
				for ( int i = 0; i < t.getChildCount(); i++ ) {
					System.out.println(sb.toString() + t.getChild(i).toString());
					printTree((CommonTree)t.getChild(i), indent+1);
				}
			}
	}
}
