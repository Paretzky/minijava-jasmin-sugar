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
	static abstract class ASTNode { }
	public static class GoalNode extends ASTNode {
		MainClassNode mainClass;
		List<AdditionalClassNode> additionalClasses;
		GoalNode() {

		}
	}
	public static class MainClassNode extends ASTNode {
		String name;
		StatementNode main;
		MainClassNode() {
			
		}
	}
	public static class StatementNode extends ASTNode { }
	public static class AssignmentStatementNode extends StatementNode {
		ReferenceAccessNode lhs;
		ExpressionNode rhs;
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
