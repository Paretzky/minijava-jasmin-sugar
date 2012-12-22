package com.benparetzky.minijavajasminsugar;

import org.antlr.stringtemplate.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.runtime.Token;
import org.antlr.*;
import antlr.*;
import java.util.*;

public class ASTBuilder {
/*
	static int verifyNewNodeStart(String firstToken, char [] input, int start) {
		char [] tok = firstToken.toCharArray();
		int leng = index.length-1,lastI = -1;
		if(input[start] != '(') {
			return -1;
		}
		for(int i = 1; i < tok.length && i < leng;i++) {
			if(tok[i]!=input[start+i])
				return -1;
			lastI = i;
		}
		return start+lastI;
	}
*/
	public GoalNode build(char [] input) {
		
	}
	static abstract class ASTNode { 
		abstract String toStringTree();
	}
	public static class GoalNode extends ASTNode {
		MainClassNode mainClass;
		List<AdditionalClassNode> additionalClasses;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(GOAL ");
			sb.append(mainClass.toStringTree());
			sb.append(" ");
			sb.append(additionalClasses.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class MainClassNode extends ASTNode {
		String name;
		StatementNode main;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(MAIN_CLASS (NAME ");
			sb.append(main);
			sb.append(") (PUBLIC_STATIC_VOID_MAIN ");
			sb.append(main.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class AdditionalClassNode extends ASTNode {
		String name, extendsIdent;
		List<VarDeclNode> varDecls;
		List<MethodDeclNode> methodDecls;
	}
	public static class VarDeclNode extends ASTNode {
		String type, ident;
	}
	public static class MethodDeclNode extends ASTNode {
		String name, returnType;
		List<VarDeclNode> varDecls;
		List<Statement> statements;
	}
	public static class StatementNode extends ASTNode { }
	public static class AssignmentStatementNode extends StatementNode {
		ReferenceAccessNode lhs;
		ExpressionNode rhs;
	}
	public static class ArrayAssignmentStatementNode extends AssignmentStatementNode {
		int index;
	}
	public static class BlockNode extends StatementNode {
		List<StatementNode> statements;
	}
	public static class IfNode extends StatementNode {
		Expression condition;
		Statement onTrue, onFalse;
	}
	public static class WhileNode extends StatementNode {
		Expression condition;
		Statement statement;
	}
	public static class SoutNode extends StatementNode {
		Expression sout;
	}
	public static class DoWhileNode extends WhileNode {}
	public static class ForEachNode extends StatementNode {
		VarDeclNode local;
		ReferenceAccessNode arrayIdent;
		Statement statement;
	}	
	public static class ExpressionNode extends ASTNode {}
	public static class NewExpNode extends ASTNode {
		String ident;
	}
	public static class NewIntArrExpNode extends NewExpNode {

	}
	public static class PrimeExpNode extends ASTNode {
		
	}
	public static class LiteralIntNode extends PrimeExpNode {
		int value;
		LiteralIntNode(int value) {
			super();
			this.value=value;
		}
	}
	public static class LiteralBoolNode extends PrimeExpNode {
		boolean value;
		LiteralIntNode(boolean value) {
			super();
			this.value=value;
		}
	}
	public static class ReferenceAccessNode extends PrimeExpNode {
		String ident;
		ReferenceAccessNode(String s) {
			this.ident=s;
		}
	}
}
