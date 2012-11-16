grammar minijava;

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
	STRING='string';
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

goal	:	mainclass classdecls EOF;

id	:	('a'..'z' | 'A'..'Z') ('a'..'z' | 'A'..'Z' | '0'..'9')*;

LitInt	:	(PLUS | MINUS)? ('0'..'9')+;

mainclass
	:	CLASS id L_BRACE PUBLIC STATIC VOID MAIN L_PAREN STRING L_BRACKET R_BRACKET id R_PAREN L_BRACE statement R_BRACE R_BRACE;

classdecls
	:	(CLASS id (EXTENDS id)? L_BRACE vardecl* methoddecls R_BRACE)*;
	
vardecl
	:	type id SEMICOLON;
	
type
	:	INT L_BRACKET R_BRACKET
	|	id;
	
methoddecls
	:	(PUBLIC type id L_PAREN methodarglist R_PAREN L_BRACE vardecl* statement* RETURN expression SEMICOLON R_BRACE)*;
	
methodarglist
	:	type id (COMMA type id)*;
	
statement
	:	id EQUALS expression SEMICOLON
	|	L_BRACE statement+ R_BRACE
	|	IF L_PAREN expression R_PAREN statement ELSE statement
	|	WHILE L_PAREN expression R_PAREN statement
	|	SOUT L_PAREN expression R_PAREN SEMICOLON
	|	DO statement WHILE L_PAREN expression R_PAREN
	|	FOR L_PAREN type id IN id R_PAREN statement
	|	id L_BRACKET expression R_BRACKET EQUALS expression SEMICOLON;
	
expression
	:	postfixexpression
	|	NEW id L_PAREN R_PAREN;
	
postfixexpression
	:	mainexpression 
	(
		(PERIOD (LENGTH | (id L_PAREN (expression (COMMA expression)*)? R_PAREN))) 
		|
		(L_BRACKET expression R_BRACKET)
	)* 
	|	plusminusexp expression;

plusminusexp
	:	multiplyexp (PLUS | MINUS) expression;
	
multiplyexp
	:	andexp MULTIPLY expression;
	
andexp	
	:	cmpexp BOOL_AND expression;
	
cmpexp	
	:	mainexpression LESS_THAN expression;

mainexpression
	:	THIS
	|	id
	|	FALSE
	|	TRUE
	|	LitInt;