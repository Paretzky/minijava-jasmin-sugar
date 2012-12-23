package com.benparetzky.minijavajasminsugar;

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
    public static void main(String [] args) {
        Queue<Character> q = new LinkedList<Character>();
        StringBuilder sb = new StringBuilder();
        Scanner in = new Scanner(System.in);
        while(in.hasNext()) {
            sb.append(in.next());
        }
        for(char c : sb.toString().toCharArray())
            q.add(c);
        GoalNode n = new GoalNode(q);
        System.out.println(n.toStringTree());
    }

    static boolean validStart(Queue<Character> in) {
        if (in.peek() == '(')
            return in.poll() != null;
        return false;
    }

    static boolean validEnd(Queue<Character> in) {
        while ((!in.isEmpty()) && in.peek().charValue() == ' ') {
            in.poll();
        }
        if (in.size() == 0)
            return false;
        if (in.peek().charValue() == ')') {
            in.poll();
            return true;
        }
        return false;
    }

    static String getTok(Queue<Character> in) {
        StringBuilder sb = new StringBuilder();
        Character c;
        while ((c = in.peek()) != '(' && c != ' ' && c != ')') {
            sb.append(in.poll());
        }
        while ((c = in.peek()) == ' ') {
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

        //^(GOAL mainclass ^(ADDITIONAL_CLASSES classdecls*))
        GoalNode(Queue<Character> in) {
            if (!validStart(in)) {
                parseError();
            }
            if (!"GOAL".equals(getTok(in))) {
                parseError();
            }
            mainClass = new MainClassNode(in);
            AdditionalClassNode n = new AdditionalClassNode(in);
            if (!n.isNull) {
                additionalClasses = new LinkedList<AdditionalClassNode>();
                additionalClasses.add(n);
                while ((n = new AdditionalClassNode(in)).isNull) {
                    additionalClasses.add(n);
                }
            }
            if (!(validEnd(in) && in.size() == 0)) {
                parseError();
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(GoalNode ");
            sb.append(mainClass.toStringTree());
            sb.append(" ");
            if (additionalClasses != null && additionalClasses.size() > 0) {
                sb.append("(ADDITIONAL_CLASSES ");
                for (AdditionalClassNode n : additionalClasses) {
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
            if (!validStart(in)) {
                parseError();
            }
            if (!"MAIN_CLASS".equals(getTok(in))) {
                parseError();
            }
            if (!validStart(in)) {
                parseError();
            }
            if (!"NAME".equals(getTok(in))) {
                parseError();
            }
            name = getTok(in);
            if (!validEnd(in)) {
                parseError();
            }
            if (!validStart(in)) {
                parseError();
            }
            if (!"PUBLIC_STATIC_VOID_MAIN".equals(getTok(in))) {
                parseError();
            }
            main = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                parseError();
            }
            if (!validEnd(in)) {
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
        boolean isNull;
        String name, extendsIdent;
        List<VarDeclNode> varDecls;
        List<MethodDeclNode> methodDecls;

        //^(ADDITIONAL_CLASS ^(NAME $n) ^(EXTENDS $e)? ^(VARDECLS vardecl*) ^(METHODDECLS methoddecl*));
        AdditionalClassNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"ADDITIONAL_CLASS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"NAME".equals(getTok(in))) {
                isNull = true;
                return;
            }
            name = getTok(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                // Normally valid endings are not EOF here it's ok
                isNull = (!validEnd(in)) && in.isEmpty();
                return;
            }
            String tok = getTok(in);
            if ("EXTENDS".equals(tok)) {
                extendsIdent = getTok(in);
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if ("VARDECLS".equals(tok)) {
                varDecls = new LinkedList<VarDeclNode>();
                VarDeclNode vdn;
                while (!(vdn = new VarDeclNode(in)).isNull) {
                    varDecls.add(vdn);
                }
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if ("METHODDECLS".equals(tok)) {
                methodDecls = new LinkedList<MethodDeclNode>();
                MethodDeclNode mdn;
                while (!(mdn = new MethodDeclNode(in)).isNull) {
                    methodDecls.add(mdn);
                }
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    return;
                }
                tok = getTok(in);
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(AdditionalClassNode (NAME ");
            sb.append(name);
            sb.append(") ");
            if (extendsIdent != null) {
                sb.append("(EXTENDS ");
                sb.append(extendsIdent);
                sb.append(" )");
            }
            if (varDecls != null && varDecls.size() > 0) {
                sb.append("(VARDECLS ");
                for (VarDeclNode n : varDecls) {
                    sb.append(n.toStringTree());
                }
                sb.append(" )");
            }
            if (methodDecls != null && methodDecls.size() > 0) {
                sb.append("(METHODDECLS ");
                for (MethodDeclNode n : methodDecls) {
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

        //^(VAR_DECL type ID)
        VarDeclNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"VAR_DECL".equals(getTok(in))) {
                isNull = true;
                return;
            }
            type = getTok(in);
            ident = getTok(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
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
        String name, returnType;
        List<VarDeclNode> varDecls, argList;
        List<StatementNode> statements;
        ExpressionNode returnExp;
        boolean isNull;

        // ^(METHOD ^(NAME ID) ^(RETURN_TYPE type)  ^(METHOD_ARG_LIST methodarglist)? ^(VARDECLS vardecl*)
        //      ^(STATEMENTS statement*) ^(RETURN expression))
        MethodDeclNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"METHOD".equals(getTok(in))) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"NAME".equals(getTok(in))) {
                isNull = true;
                return;
            }
            name = getTok(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"RETURN_TYPE".equals(getTok(in))) {
                isNull = true;
                return;
            }
            returnType = getTok(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            String tok;
            if ("METHOD_ARG_LIST".equals(getTok(in))) {
                VarDeclNode arg;
                argList = new LinkedList<VarDeclNode>();
                while (!(arg = new VarDeclNode(in)).isNull) {
                    argList.add(arg);
                }
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    isNull = true;
                    return;
                }
                tok = getTok(in);
            }
            if ("VARDECLS".equals(getTok(in))) {
                VarDeclNode arg;
                varDecls = new LinkedList<VarDeclNode>();
                while (!(arg = new VarDeclNode(in)).isNull) {
                    varDecls.add(arg);
                }
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    isNull = true;
                    return;
                }
                tok = getTok(in);
            }
            if ("STATEMENTS".equals(getTok(in))) {
                StatementNode arg;
                statements = new LinkedList<StatementNode>();
                while ((arg = StatementNode.constructStatement(in)) != null && !arg.isNull) {
                    statements.add(arg);
                }
                if (!validEnd(in)) {
                    isNull = true;
                    return;
                }
                if (!validStart(in)) {
                    isNull = true;
                    return;
                }
                tok = getTok(in);
            }
            if (!"RETURN".equals(getTok(in))) {
                isNull = true;
                return;
            }
            returnExp = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(MethodDeclNode (NAME ");
            sb.append(name);
            sb.append(") (RETURN_TYPE ");
            sb.append(returnType);
            sb.append(") ");
            if (argList != null && argList.size() > 0) {
                sb.append("(METHOD_ARG_LIST ");
                for (VarDeclNode n : argList) {
                    sb.append(n.toStringTree());
                }
                sb.append(" )");
            }
            if (varDecls != null && varDecls.size() > 0) {
                sb.append("(VARDECLS ");
                for (VarDeclNode n : varDecls) {
                    sb.append(n.toStringTree());
                }
                sb.append(" )");
            }
            if (statements != null && statements.size() > 0) {
                sb.append("(STATEMENTS ");
                for (StatementNode n : statements) {
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
        //All instance's constructors will have an extra closing paren
        boolean isNull;
        private static final StatementNode nullNode = null;

        /*
        ^(ASSIGNMENT_STATEMENT ^(LHS ID) ^(RHS expression))
        ^(BLOCK statement+)
        ^(IF_STATEMENT ^(CONDITION $e) ^(IF $s1) ^(ELSE $s2)) ^(CONDITION expression) ^(STATEMENT statement))
        ^(WHILE_STATEMENT ^(CONDITION expression) ^(STATEMENT statement))
        ^(SOUT expression)
        ^(DO_WHILE_STATEMENT ^(STATEMENT statement) ^(CONDITION expression))
        ^(FOR_EACH_STATEMENT ^(IN ^(VAR_DECL type $a) $b) ^(STATEMENT statement))
        ^(ARRAY_ASSIGNMENT_STATEMENT ^(LHS ^(ARRAY $a) ^(INDEX $e1)) ^(RHS $e2))
         */
        static StatementNode constructStatement(Queue<Character> in) {
            String tok;
            if (!validStart(in)) {
                return nullNode;
            }
            tok = getTok(in);
            if (tok == null) {
                return nullNode;
            }
            if ("ASSIGNMENT_STATEMENT".equals(tok)) {
                return new AssignmentStatementNode(in);
            }
            if ("BLOCK".equals(tok)) {
                return new BlockNode(in);
            }
            if ("IF_STATEMENT".equals(tok)) {
                return new IfNode(in);
            }
            if ("WHILE_STATEMENT".equals(tok)) {
                return new WhileNode(in);
            }
            if ("SOUT".equals(tok)) {
                return new SoutNode(in);
            }
            if ("DO_WHILE_STATEMENT".equals(tok)) {
                return new DoWhileNode(in);
            }
            if ("FOR_EACH_STATEMENT".equals(tok)) {
                return new ForEachNode(in);
            }
            if ("ARRAY_ASSIGNMENT_STATEMENT".equals(tok)) {
                return new ArrayAssignmentStatementNode(in);
            }
            return nullNode;
        }
    }

    public static class AssignmentStatementNode extends StatementNode {
        ReferenceAccessNode lhs;
        ExpressionNode rhs;

        //^(ASSIGNMENT_STATEMENT ^(LHS ID) ^(RHS expression))
        AssignmentStatementNode(Queue<Character> in) {
            //Starting at LHS
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"LHS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            lhs = new ReferenceAccessNode(getTok(in));
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"RHS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            rhs = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

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

    public static class ArrayAssignmentStatementNode extends StatementNode {
        ArrayAccessNode lhs;
        ExpressionNode rhs;
        int index;


        // ^(ARRAY_ASSIGNMENT_STATEMENT ^(LHS ^(ARRAY $a) ^(INDEX $e1)) ^(RHS $e2))
        ArrayAssignmentStatementNode(Queue<Character> in) {
            //Starting at LHS
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"LHS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            lhs = new ArrayAccessNode(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"index".equals(getTok(in))) {
                isNull = true;
                return;
            }
            index = Integer.parseInt(getTok(in));
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"RHS".equals(getTok(in))) {
                isNull = true;
                return;
            }
            rhs = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

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

        //^(BLOCK statement+)
        BlockNode(Queue<Character> in) {
            StatementNode n;
            statements = new LinkedList<StatementNode>();
            while ((n = StatementNode.constructStatement(in)) != null && !n.isNull) {
                statements.add(n);
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(BlockNode ");
            if (statements != null && statements.size() > 0) {
                for (StatementNode n : statements) {
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
        IfNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"CONDITION".equals(getTok(in))) {
                isNull = true;
                return;
            }
            condition = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"IF".equals(getTok(in))) {
                isNull = true;
                return;
            }
            onTrue = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"ELSE".equals(getTok(in))) {
                isNull = true;
                return;
            }
            onFalse = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

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

        protected WhileNode() {

        }

        //^(WHILE_STATEMENT ^(CONDITION expression) ^(STATEMENT statement))
        WhileNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"CONDITION".equals(getTok(in))) {
                isNull = true;
                return;
            }
            condition = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"STATEMENT".equals(getTok(in))) {
                isNull = true;
                return;
            }
            statement = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

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

        //^(SOUT expression)
        SoutNode(Queue<Character> in) {
            sout = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(SoutNode ");
            sb.append(sout.toStringTree());
            sb.append(" )");
            return sb.toString();
        }
    }

    public static class DoWhileNode extends WhileNode {

        //^(DO_WHILE_STATEMENT ^(STATEMENT statement) ^(CONDITION expression))
        DoWhileNode(Queue<Character> in) {
            super();
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"STATEMENT".equals(getTok(in))) {
                isNull = true;
                return;
            }
            statement = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"CONDITION".equals(getTok(in))) {
                isNull = true;
                return;
            }
            condition = ExpressionNode.constructExpression(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }

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
        VarDeclNode local;
        String arrayIdent;
        StatementNode statement;

        //^(FOR_EACH_STATEMENT ^(IN ^(VAR_DECL type $a) $b) ^(STATEMENT statement))
        ForEachNode(Queue<Character> in) {
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"IN".equals(getTok(in))) {
                isNull = true;
                return;
            }
            local = new VarDeclNode(in);
            arrayIdent = getTok(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validStart(in)) {
                isNull = true;
                return;
            }
            if (!"STATEMENT".equals(getTok(in))) {
                isNull = true;
                return;
            }
            statement = StatementNode.constructStatement(in);
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
            if (!validEnd(in)) {
                isNull = true;
                return;
            }
        }


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

    public abstract static class ExpressionNode extends ASTNode {
        static ExpressionNode constructExpression(Queue<Character> in) {
            return null;
        }
    }

    public static class CallExpNode extends ExpressionNode {
        ExpressionNode lhs, rhs;

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
            if (params != null && params.size() > 0) {
                sb.append("(PARAM_LIST ");
                for (String s : params) {
                    sb.append(s);
                }
                sb.append(" )");
            }
            if (params != null && params.size() > 0) {
                sb.append("(RHS ");
                for (ExpressionNode n : rhss) {
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
            if (exps != null && exps.size() > 0) {
                for (ExpressionNode n : exps) {
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
            sb.append(plus ? "+ " : "- ");
            if (exps != null && exps.size() > 0) {
                for (ExpressionNode n : exps) {
                    sb.append(n.toStringTree());
                }
            }
            sb.append(" )");
            return sb.toString();
        }
    }

    public static class LessThanExpNode extends ExpressionNode {
        ExpressionNode lhs, rhs;

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
        ExpressionNode lhs, rhs;

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
            if (!validStart(in)) {
                parseError();
            }
            if (!"ARRAY_ACCESS".equals(getTok(in))) {
                parseError();
            }
            if (!validStart(in)) {
                parseError();
            }
            if (!"ARRAY".equals(getTok(in))) {
                parseError();
            }
            ident = getTok(in);
            if (!validEnd(in)) {
                parseError();
            }
        }

        String toStringTree() {
            StringBuilder sb = new StringBuilder();
            sb.append("(ArrayAccessNode (ARRAY");
            sb.append(ident);
            sb.append(" ) ");
            if (indices != null)
                for (Integer i : indices) {
                    sb.append("(INDEX ");
                    sb.append(i);
                    sb.append(") ");
                }
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
            this.value = value;
        }

        String toStringTree() {
            return Integer.toString(value);
        }
    }

    public static class LiteralBoolNode extends PrimeExpNode {
        boolean value;

        LiteralBoolNode(boolean value) {
            super();
            this.value = value;
        }

        String toStringTree() {
            return Boolean.toString(value);
        }
    }

    public static class ReferenceAccessNode extends PrimeExpNode {
        String ident;

        ReferenceAccessNode(String s) {
            this.ident = s;
        }

        String toStringTree() {
            return ident;
        }
    }
}
