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
	:	arithmeticexp (PERIOD postfixexp)*;
	
arrayaccessexp
	:	L_BRACKET expression R_BRACKET;
	
postfixexp
	:	LENGTH
	|	ID L_PAREN (expression (COMMA expression)*)? R_PAREN;
	
arithmeticexp
	:	addexp (LESS_THAN addexp)?
	|	NEW ID L_PAREN R_PAREN
	|	NEW INT L_BRACKET expression? R_BRACKET;
	
addexp	:	
	multiplyexp ((PLUS multiplyexp) | (MINUS multiplyexp))*;
	
multiplyexp
	:	andexp (MULTIPLY andexp)*;
	
andexp	:	atomexp (BOOL_AND atomexp)*;

atomexp	:	 ((L_PAREN expression  R_PAREN 
	| BANG expression 
	| ID) arrayaccessexp*)
	| LitInt | THIS | TRUE | FALSE;
/*
expr:   multExpr (('+'^|'-'^) multExpr)*
    ; 

multExpr
    :   atom ('*'^ atom)*
    ; 

atom:   LitInt 
    |   ID
    |   '('! expr ')'!
    ;

/*
expression
	:	prefixexpression (options{greedy=true;}: PERIOD periodexpression)*;

periodexpression
	:	LENGTH
	|	ID L_PAREN (expression (COMMA expression)*)? R_PAREN;

prefixexpression
	:	NEW ID L_PAREN R_PAREN
	|	NEW INT L_BRACKET expression R_BRACKET
	|	addexpression (options{greedy=true;}: LESS_THAN addexpression)?;


addexpression
	:	 multiplyexpression ((PLUS^ | MINUS^) (options{greedy=true;}: multiplyexpression))*;

multiplyexpression
	:	andexpression (options{greedy=true;}: MULTIPLY^ andexpression)*;

andexpression
	:	baseexpression (options{greedy=true;}: BOOL_AND^ baseexpression)*;

baseexpression
	:	THIS | TRUE | FALSE | LitInt | ID | L_PAREN (options{greedy=true;}: expression) R_PAREN | BANG expression;
*/