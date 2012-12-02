grammar minijava;
options {
	output=AST;
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
}

@members {
    public static void main(String[] args) throws Exception {
        minijavaLexer lex = new minijavaLexer(new ANTLRFileStream(args[0]));
        CommonTokenStream tokens = new CommonTokenStream(lex);
 
        minijavaParser parser = new minijavaParser(tokens);
 
        try {
            parser.goal();
        } catch (RecognitionException e)  {
            e.printStackTrace();
        }
        
    }
}

goal	:	mainclass classdecls* EOF;

//goal	:	expression EOF;

ID 	:	('a'..'z' | 'A'..'Z' | '_') ('a'..'z' |'A'..'Z' |'0'..'9' | '_')* ;

LitInt	:	 ('0'..'9')+;

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+    { $channel = HIDDEN; } ;

SINGLE_COMMENT: WHITESPACE * '//' ~('\n')* '\n' { $channel = HIDDEN; };

mainclass
	:	CLASS ID L_BRACE PUBLIC STATIC VOID MAIN L_PAREN STRING L_BRACKET R_BRACKET ID R_PAREN L_BRACE statement R_BRACE R_BRACE;

classdecls
	:	CLASS ID (EXTENDS ID)? L_BRACE vardecl* methoddecls* R_BRACE;
	
vardecl
	:	type ID SEMICOLON;
	
type
	:	INT (L_BRACKET R_BRACKET)?
	|	BOOLEAN | ID;
	
methoddecls
	:	PUBLIC type ID L_PAREN methodarglist? R_PAREN L_BRACE vardecl* statement* RETURN expression SEMICOLON R_BRACE;
	
methodarglist
	:	type ID (COMMA type ID)*;
	
statement
	:	ID EQUALS expression SEMICOLON
	|	L_BRACE statement+ R_BRACE
	|	IF L_PAREN expression R_PAREN statement ELSE statement
	|	WHILE L_PAREN expression R_PAREN statement
	|	SOUT L_PAREN expression R_PAREN SEMICOLON
	|	DO statement WHILE L_PAREN expression R_PAREN SEMICOLON
	|	FOR L_PAREN type ID IN ID R_PAREN statement
	|	ID L_BRACKET expression R_BRACKET EQUALS expression SEMICOLON;
	
expression
	:	expln;
	
expln	:	expadd ((LESS_THAN | BOOL_AND) expadd)?;

expadd	:	expmul ((PLUS|MINUS) expmul)*;

expmul	:	bangexp (MULTIPLY bangexp)*;

bangexp :	BANG? parenexp;

parenexp 
	:	L_PAREN expression R_PAREN ((PERIOD ID L_PAREN (expression(COMMA expression)*)? R_PAREN) | (PERIOD LENGTH))?
	|	callexp;

callexp	:	lengexp (PERIOD ID L_PAREN (expression(COMMA expression)*)? R_PAREN)?;

lengexp	:	arrayexp (PERIOD LENGTH)?;

arrayexp 
	:	newexp (L_BRACKET expression R_BRACKET)*;
	
newexp	:	NEW ID L_PAREN R_PAREN
	|	NEW INT L_BRACKET expression R_BRACKET
	|	primeexp;
	
primeexp:	ID | TRUE | FALSE | THIS | LitInt;