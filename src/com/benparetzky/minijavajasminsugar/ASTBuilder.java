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
    static boolean validStart(Queue<Character> in) {
        if(in.peek() == '(')
            return in.poll() != null;
        return false;
    }
    static boolean validEnd(Queue<Character> in) {
        while((!in.isEmpty()) && in.peek().charValue() == ' ') {
            in.poll();
        }
        if(in.size() == 0)
            return false;
        if(in.peek().charValue() == ')') {
            in.poll();
            return true;
        }
        return false;
    }
    static String getTok(Queue<Character> in) {
        StringBuilder sb = new StringBuilder();
        Character c;
        while((c = in.peek()) != '(' && c != ' ' && c != ')') {
            sb.append(in.poll());
        }
        while((c = in.peek()) == ' ') {
            in.poll();
        }
        return sb.toString();
    }
    static void parseError() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            System.err.println(ste);
        }
        throw new IllegalArgumentException("");
    }
	public GoalNode build(Queue<Character> input) {
		      return null;
	}
	static abstract class ASTNode { 
		abstract String toStringTree();
	}
	public static class GoalNode extends ASTNode {
		MainClassNode mainClass;
		List<AdditionalClassNode> additionalClasses;
        GoalNode(Queue<Character> in) {
            if(!validStart(in)) {
                parseError();
            }
            if(!"GOAL".equals(getTok(in))) {
                parseError();
            }
            mainClass = new MainClassNode(in);
            AdditionalClassNode n = new AdditionalClassNode(in);
            if(!n.isNull) {
                additionalClasses = new LinkedList<AdditionalClassNode>();
                additionalClasses.add(n);
                while((n =  new AdditionalClassNode(in)).isNull) {
                    additionalClasses.add(n);
                }
            }
            if(!(validEnd(in) && in.size() == 0)) {
                parseError();
            }
        }
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(GoalNode ");
			sb.append(mainClass.toStringTree());
			sb.append(" ");
            if(additionalClasses != null && additionalClasses.size() > 0) {
                sb.append("(ADDITIONAL_CLASSES ");
                for(AdditionalClassNode n : additionalClasses) {
                    sb.append(n.toStringTree());
                }
                sb.append(" )");
            }
			//sb.append(additionalClasses.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class MainClassNode extends ASTNode {
		String name;
		StatementNode main;
        //^(MAIN_CLASS ^(NAME ID) ^(PUBLIC_STATIC_VOID_MAIN statement))
        MainClassNode(Queue<Character> in) {
            if(!validStart(in)) {
                parseError();
            }
            if(! "MAIN_CLASS".equals(getTok(in))) {
                parseError();
            }
            if(!validStart(in)) {
                parseError();
            }
            if(! "NAME".equals(getTok(in))) {
                parseError();
            }
            name = getTok(in);
            if(!validEnd(in)) {
                parseError();
            }
            if(!validStart(in)) {
                parseError();
            }
            if(! "PUBLIC_STATIC_VOID_MAIN".equals(getTok(in))) {
                parseError();
            }
            main = StatementNode.constructStatement(in);
            if(!validEnd(in)) {
                parseError();
            }
            if(!validEnd(in)) {
                parseError();
            }
        }
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
        boolean isNull;
		String name, extendsIdent;
		List<VarDeclNode> varDecls;
		List<MethodDeclNode> methodDecls;
        AdditionalClassNode(Queue<Character> in) {
            if(!validStart(in)) {
                isNull = true;
                return;
            }
            if(!"ADDITIONAL_CLASS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            if(!validStart(in)) {
                isNull = true;
                return;
            }
            if(!"NAME".equals(getTok(in))) {
                isNull = true;
                return;
            }
            name = getTok(in);
            if(!validEnd(in)) {
                isNull = true;
                return;
            }
            if(!validStart(in)) {
                // Normally valid endings are not EOF here it's ok
                isNull = (!validEnd(in)) && in.isEmpty();
                return;
            }
            String tok = getTok(in);
            if("EXTENDS".equals(tok)) {
                extendsIdent = getTok(in);
                if(!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if(!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if("VARDECLS".equals(tok)) {
                varDecls = new LinkedList<VarDeclNode>();
                VarDeclNode vdn;
                while(!(vdn = new VarDeclNode(in)).isNull) {
                    varDecls.add(vdn);
                }
                if(!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if(!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if("METHODDECLS".equals(tok)) {
                methodDecls = new LinkedList<MethodDeclNode>();
                MethodDeclNode mdn;
                while(!(mdn = new MethodDeclNode(in)).isNull) {
                    methodDecls.add(mdn);
                }
                if(!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if(!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if(!validEnd(in)) {
                isNull = true;
                return;
            }
        }
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
			if(varDecls != null && varDecls.size() > 0) {
				sb.append("(VARDECLS ");
				for(VarDeclNode n : varDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(methodDecls != null && methodDecls.size() > 0) {
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
        boolean isNull;
        VarDeclNode(Queue<Character> in) {

        }
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
		List<StatementNode> statements;
		ExpressionNode returnExp;
        MethodDeclNode(Queue<Character> in) {

        }
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(MethodDeclNode (NAME ");
			sb.append(name);
			sb.append(") (RETURN_TYPE ");
			sb.append(returnType);
			sb.append(") ");
			if(argList != null && argList.size() > 0) {
				sb.append("(METHOD_ARG_LIST ");
				for(VarDeclNode n : argList) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(varDecls != null && varDecls.size() > 0) {
				sb.append("(VARDECLS ");
				for(VarDeclNode n : varDecls) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			if(statements != null && statements.size() > 0) {
				sb.append("(STATEMENTS ");
				for(StatementNode n : statements) {
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
	public abstract static class StatementNode extends ASTNode {
        static StatementNode constructStatement(Queue<Character> in) {
            return null;
        }
    }
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
			if(statements != null && statements.size() > 0) {
				for(StatementNode n : statements) {
					sb.append(n.toStringTree());
				}
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class IfNode extends StatementNode {
		ExpressionNode condition;
		StatementNode onTrue, onFalse;
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
		ExpressionNode condition;
		StatementNode statement;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(WhileNode (CONDITION ");
			sb.append(condition.toStringTree());
			sb.append(" ) (STATEMENT ");
			sb.append(statement.toStringTree());
			sb.append(" ) )");
			return sb.toString();
		}
	}
	public static class SoutNode extends StatementNode {
		ExpressionNode sout;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(SoutNode ");
			sb.append(sout.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class DoWhileNode extends WhileNode {
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(WhileNode (STATEMENT  ");
			sb.append(statement.toStringTree());
			sb.append(" ) (CONDITION ");
			sb.append(condition.toStringTree());
			sb.append(" ) )");
			return sb.toString();
		}	
	}
	public static class ForEachNode extends StatementNode {
		//^(FOR_EACH_STATEMENT ^(IN ^(VAR_DECL type $a) $b) ^(STATEMENT statement))
		VarDeclNode local;
		String arrayIdent;
		StatementNode statement;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(ForEachNode (IN  ");
			sb.append(local.toStringTree());
			sb.append(" ");
			sb.append(arrayIdent);
			sb.append(") (STATEMENT ");
			sb.append(statement.toStringTree());
			sb.append(" ) )");
			return sb.toString();
		}
	}	
	public abstract static class ExpressionNode extends ASTNode {}
	public static class CallExpNode extends ExpressionNode {
		ExpressionNode lhs,rhs;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(CallExpNode (LHS");
			sb.append(lhs.toStringTree());
			sb.append(" ) (RHS ");
			sb.append(rhs.toStringTree());
			sb.append(" ) )");
			return sb.toString();
		}
	}
	public static class PeriodExpNode extends ASTNode {
		String name;
		List<String> params;
		List<ExpressionNode> rhss;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(PeriodExpNode (NAME");
			sb.append(name);
			sb.append(") ");
			if(params != null && params.size() > 0) {
				sb.append("(PARAM_LIST ");
				for(String s : params) {
					sb.append(s);
				}
				sb.append(" )");
			}
			if(params != null && params.size() > 0) {
				sb.append("(RHS ");
				for(ExpressionNode n : rhss) {
					sb.append(n.toStringTree());
				}
				sb.append(" )");
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class BangExpNode extends ExpressionNode {
		ExpressionNode exp;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(BangExpNode ");
			sb.append(exp.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class MultiplyExpNode extends ExpressionNode {
		List<ExpressionNode> exps;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(MultiplyExpNode ");
			if(exps != null && exps.size() > 0) {
				for(ExpressionNode n : exps) {
					sb.append(n.toStringTree());
				}
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class PlusMinusExpNode extends ExpressionNode {
		List<ExpressionNode> exps;
		boolean plus;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(PlusMinusExpNode ");
			sb.append(plus?"+ ":"- ");
			if(exps != null && exps.size() > 0) {
				for(ExpressionNode n : exps) {
					sb.append(n.toStringTree());
				}
			}
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class LessThanExpNode extends ExpressionNode {
		ExpressionNode lhs,rhs;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(LessThanExpNode (LHS ");
			sb.append(lhs.toStringTree());
			sb.append(" ) (RHS ");
			sb.append(rhs.toStringTree());
			sb.append(" ) )");
			return sb.toString();
		}
	}
	public static class BoolAndExpNode extends ExpressionNode {
		ExpressionNode lhs,rhs;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(BoolAndExpNode ");
			sb.append(lhs.toStringTree());
			sb.append(" ");
			sb.append(rhs.toStringTree());
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class ArrayAccessNode extends ExpressionNode {
		String ident;
		List<Integer> indices;
        // ^(ARRAY_ACCESS ^(ARRAY newexp) ^(INDEX $rhs)*)
        ArrayAccessNode(Queue<Character> in) {
            if(!validStart(in)) {
                parseError();
            }
            if(!"ARRAY_ACCESS".equals(getTok(in))) {
                parseError();
            }
            if(!validStart(in)) {
                parseError();
            }
            if(!"ARRAY".equals(getTok(in))) {
                parseError();
            }
            ident = getTok(in);
            if(!validEnd(in)) {
                parseError();
            }
        }
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(ArrayAccessNode (ARRAY");
			sb.append(ident);
			sb.append(" ) ");
			if(indices != null)
                for(Integer i : indices)
                    {sb.append("(INDEX ");sb.append(i);sb.append(") ");}
			sb.append(")");
			return sb.toString();
		}
	}
	public static class NewExpNode extends ASTNode {
		String ident;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(NewExpNode ");
			sb.append(ident);
			sb.append(" )");
			return sb.toString();
		}
	}
	public static class NewIntArrExpNode extends ASTNode {
		int length;
		String toStringTree() {
			StringBuilder sb = new StringBuilder();
			sb.append("(NewIntArrExpNode (LENGTH ");
			sb.append(length);
			sb.append(" ) )");
			return sb.toString();
		}
	}
	public abstract static class PrimeExpNode extends ASTNode {
		
	}
	public static class LiteralIntNode extends PrimeExpNode {
		int value;
		LiteralIntNode(int value) {
			super();
			this.value=value;
		}
		String toStringTree() {
			return Integer.toString(value);
		}
	}
	public static class LiteralBoolNode extends PrimeExpNode {
		boolean value;
        LiteralBoolNode(boolean value) {
			super();
			this.value=value;
		}
		String toStringTree() {
			return Boolean.toString(value);
		}
	}
	public static class ReferenceAccessNode extends PrimeExpNode {
		String ident;
		ReferenceAccessNode(String s) {
			this.ident=s;
		}
		String toStringTree() {
			return ident;
		}
	}
}