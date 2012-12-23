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
			sb.append("(GoalNode ");
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
			sb.append("(MainClassNode (NAME ");
			sb.append(name);
			sb.append(") (PUBLIC_STATIC_VOID_MAIN ");
			sb.append(main.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class AdditionalClassNode extends ASTNode {
		//^(ADDITIONAL_CLASS ^(NAME $n) ^(EXTENDS $e)? ^(VARDECLS vardecl*) ^(METHODDECLS methoddecl*));
		String name, extendsIdent;
		List<VarDeclNode> varDecls;
		List<MethodDeclNode> methodDecls;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(AdditionalClassNode (NAME ");
			sb.append(name);
			sb.append(") ");
			if(extendsIdent != null) {
				sb.append("(EXTENDS ");
				sb.append(extendsIdent);
				sb.append(" )");
			}
			if(varDecls != null && varDecls.length > 0) {
				sb.append("(VARDECLS ");
				for(VarDeclNode n : varDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(methodDecls != null && methodDecls.length > 0) {
				sb.append("(METHODDECLS ");
				for(MethodDeclNode n : methodDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class VarDeclNode extends ASTNode {
		String type, ident;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(VarDeclNode (NAME ");
			sb.append(ident);
			sb.append(") (TYPE");
			sb.append(type);
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class MethodDeclNode extends ASTNode {
		// 		^(METHOD ^(NAME ID) ^(RETURN_TYPE type)  ^(METHOD_ARG_LIST methodarglist)? ^(VARDECLS vardecl*) ^(STATEMENTS statement*) ^(RETURN expression));
	
		String name, returnType;
		List<VarDeclNode> varDecls, argList;
		List<Statement> statements;
		Expression returnExp;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(MethodDeclNode (NAME ");
			sb.append(name);
			sb.append(") (RETURN_TYPE ");
			sb.append(returnType);
			sb.append(") ");
			if(argList != null && argList.length > 0) {
				sb.append("(METHOD_ARG_LIST ");
				for(VarDeclNode n : argList) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(varDecls != null && varDecls.length > 0) {
				sb.append("(VARDECLS ");
				for(VarDeclNode n : varDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(statements != null && statements.length > 0) {
				sb.append("(STATEMENTS ");
				for(MethodDeclNode n : methodDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			sb.append("(RETURN ");
			sb.append(returnExp.toStringTree());
			sb.append(") )");
			return sb.toString();
		}
	}
	public static class StatementNode extends ASTNode { }
	public static class AssignmentStatementNode extends StatementNode {
		ReferenceAccessNode lhs;
		ExpressionNode rhs;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(AssignmentStatementNode (LHS ");
			sb.append(lhs.toStringTree());
			sb.append(") (RHS");
			sb.append(rhs.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class ArrayAssignmentStatementNode extends AssignmentStatementNode {
		int index;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(AssignmentStatementNode (LHS ");
			sb.append(lhs.toStringTree());
			sb.append(") (INDEX ");
			sb.append(index);
			sb.append(") (RHS ");
			sb.append(rhs.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class BlockNode extends StatementNode {
		List<StatementNode> statements;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(BlockNode ");
			if(statements != null && statements.length > 0) {
				for(StatementNode n : statements) {
					sb.append(n.toStringTree());
				}
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class IfNode extends StatementNode {
		Expression condition;
		Statement onTrue, onFalse;
		// ^(IF_STATEMENT ^(CONDITION $e) ^(IF $s1) ^(ELSE $s2))
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(IfNode (CONDITION ");
			sb.append(condition.toStringTree());
			sb.append(" ) (IF ");
			sb.append(onTrue.toStringTree());
			sb.append(" ) (ELSE ");
			sb.append(onFalse.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
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
