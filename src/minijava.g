grammar minijava;
options {
	output=AST;
	ASTLabelType=CommonTree;
}
tokens {
	CLASS='class';
	L_BRACE='{';
	R_BRACE='}';
	PUBLIC='public';
	STATIC='static';
	VOID='void';
	MAIN='main';
	L_PAREN='(';
	R_PAREN=')';
	STRING='String';
	L_BRACKET='[';
	R_BRACKET=']';
	EQUALS='=';
	SEMICOLON=';';
	EXTENDS='extends';
	INT='int';
	BOOLEAN='boolean';
	RETURN='return';
	COMMA=',';
	IF='if';
	ELSE='else';
	WHILE='while';
	SOUT='System.out.println';
	DO='do';
	FOR='for';
	IN='in';
	PLUS='+';
	MINUS='-';
	MULTIPLY='*';
	BANG='!';
	NEW='new';
	THIS='this';
	TRUE='true';
	FALSE='false';
	PERIOD='.';
	LENGTH='length';
	BOOL_AND='&&';
	LESS_THAN='<';
	BLOCK;
	VARDECLS;
	METHODDECLS;
	INTARRAY;
	STATEMENT;
	CALLEXP;
	METHOD;
	RETURN_TYPE;
	NAME;
	STATEMENTS;
	METHOD_ARG_LIST;
	GOAL;
	MAIN_CLASS;
	ADDITIONAL_CLASSES;
	PUBLIC_STATIC_VOID_MAIN;
	VAR_DECL;
	STATEMENT;
	ASSIGNMENT_STATEMENT;
	LHS;
	RHS;
	IF_STATEMENT;
	CONDITION;
	WHILE_STATEMENT;
	DO_WHILE_STATEMENT;
	FOR_EACH_STATEMENT;
}

@parser::header{
import  org.antlr.stringtemplate.*;
import org.antlr.*;
}
@members {
    public static void main(String[] args) throws Exception {
        minijavaLexer lex = new minijavaLexer(new ANTLRFileStream(args[0]));
        CommonTokenStream tokens = new CommonTokenStream(lex);
 
        minijavaParser parser = new minijavaParser(tokens);
        try {
/*
	CommonTree tree = (CommonTree)parser.goal().getTree();
    DOTTreeGenerator gen = new DOTTreeGenerator();
    StringTemplate st = gen.toDOT(tree);
    System.out.println(st);
	minijavaParser.goal_return g = parser.goal();
	System.out.println(((Tree)g.tree).toStringTree());
*/
CommonTree tree = (CommonTree)parser.goal().getTree();
    DOTTreeGenerator gen = new DOTTreeGenerator();
   StringTemplate  st = gen.toDOT(tree);
    System.out.println(st);
        } catch (RecognitionException e)  {
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

goal	:	mainclass classdecls* EOF -> ^(GOAL mainclass ^(ADDITIONAL_CLASSES classdecls*));

//goal	:	expression EOF;

ID 	:	('a'..'z' | 'A'..'Z' | '_') ('a'..'z' |'A'..'Z' |'0'..'9' | '_')* ;

LitInt	:	 ('0'..'9')+;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+    { $channel = HIDDEN; } ;

SINGLE_COMMENT: WHITESPACE * '//' ~('\n')* '\n' { $channel = HIDDEN; };

mainclass
	:	CLASS ID L_BRACE PUBLIC STATIC VOID MAIN L_PAREN STRING L_BRACKET R_BRACKET ID R_PAREN L_BRACE statement R_BRACE R_BRACE -> ^(MAIN_CLASS ^(NAME ID) ^(PUBLIC_STATIC_VOID_MAIN statement));

classdecls
	:	CLASS n=ID (EXTENDS e=ID)? L_BRACE vardecl* methoddecl* R_BRACE -> ^(CLASS ^(NAME $n) ^(EXTENDS $e)? ^(VARDECLS vardecl*) ^(METHODDECLS methoddecl*));
	
vardecl
	:	type ID SEMICOLON -> ^(VAR_DECL type ID);
	
type
	:	INT L_BRACKET R_BRACKET -> INTARRAY
	|	INT | BOOLEAN | ID;
	
methoddecl
	:	PUBLIC type ID L_PAREN methodarglist? R_PAREN L_BRACE vardecl* statement* RETURN expression SEMICOLON R_BRACE ->
		^(METHOD ^(NAME ID) ^(RETURN_TYPE type)  ^(METHOD_ARG_LIST methodarglist)? ^(VARDECLS vardecl*) ^(STATEMENTS statement*) ^(RETURN expression));
	
methodarglist
	:	type ID -> ^(type ID)	
	|	type ID (COMMA type ID)+ -> ^(COMMA ^(type ID)+);
	
statement
	:	ID EQUALS expression SEMICOLON -> ^(ASSIGNMENT_STATEMENT ^(LHS ID) ^(RHS expression))
	|	L_BRACE statement+ R_BRACE -> ^(BLOCK statement+)
	|	IF L_PAREN e=expression R_PAREN s1=statement ELSE s2=statement -> ^(IF_STATEMENT ^(CONDITION $e) ^(IF $s1) ^(ELSE $s2))
	|	WHILE L_PAREN expression R_PAREN statement -> ^(WHILE_STATEMENT ^(CONDITION expression) ^(STATEMENT statement))
	|	SOUT L_PAREN expression R_PAREN SEMICOLON -> ^(SOUT expression)
	|	DO statement WHILE L_PAREN expression R_PAREN SEMICOLON -> ^(DO_WHILE_STATEMENT ^(STATEMENT statement) ^(CONDITION expression))
	|	FOR L_PAREN type ID IN ID R_PAREN statement
	|	ID L_BRACKET expression R_BRACKET EQUALS expression SEMICOLON;
	
expression
	:	expln;
	
expln	:	expadd ((LESS_THAN | BOOL_AND)^ expadd)?;

expadd	:	expmul ((PLUS|MINUS)^ expmul)*;

expmul	:	bangexp (MULTIPLY^ bangexp)*;

bangexp :	BANG? parenexp;

parenexp 
	:	L_PAREN! expression R_PAREN! ((PERIOD ID L_PAREN (expression(COMMA expression)*)? R_PAREN) | (PERIOD LENGTH))?
	|	callexp;

callexp	:	lengexp rhs=(PERIOD ID L_PAREN (expression(COMMA expression)*)? R_PAREN)?;

lengexp	:	arrayexp (PERIOD LENGTH)?;

arrayexp 
	:	newexp (L_BRACKET expression R_BRACKET)*;
	
newexp	:	NEW ID L_PAREN R_PAREN
	|	NEW INT L_BRACKET expression R_BRACKET
	|	primeexp;
	
primeexp:	ID | TRUE | FALSE | THIS | LitInt;
