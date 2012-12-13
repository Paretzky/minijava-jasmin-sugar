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
	EXPRESSION;
	ARRAY;
	INDEX;
	ARRAY_ASSIGNMENT_STATEMENT;
	ADDITIONAL_CLASS;
	CALL;
	PARAMS_LIST;
	ARRAY_ACCESS;
	BOOLEAN_INVERT;
	REFERENCE;
}
@lexer::header{
package com.benparetzky.minijavajasminsugar;
}
@parser::header{
package com.benparetzky.minijavajasminsugar;
import  org.antlr.stringtemplate.*;
import org.antlr.*;
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
	:	CLASS n=ID (EXTENDS e=ID)? L_BRACE vardecl* methoddecl* R_BRACE -> ^(ADDITIONAL_CLASS ^(NAME $n) ^(EXTENDS $e)? ^(VARDECLS vardecl*) ^(METHODDECLS methoddecl*));
	
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
	|	FOR L_PAREN type a=ID IN b=ID R_PAREN statement -> ^(FOR_EACH_STATEMENT ^(IN ^(VAR_DECL type $a) $b) ^(STATEMENT statement))
	|	a=ID L_BRACKET e1=expression R_BRACKET EQUALS e2=expression SEMICOLON -> ^(ARRAY_ASSIGNMENT_STATEMENT ^(LHS ^(ARRAY $a) ^(INDEX $e1)) ^(RHS $e2));
	
expression
	:	expln;
	
expln	:
	expadd ((LESS_THAN | BOOL_AND)^ expadd)?;

expadd	:	expmul ((PLUS|MINUS)^ expmul)*;

expmul	:	bangexp (MULTIPLY^ bangexp)*;

bangexp :	b=BANG? parenexp -> {b != null}? ^(BOOLEAN_INVERT parenexp) -> parenexp;

parenexp 
	:	L_PAREN! expression R_PAREN! ((PERIOD ID L_PAREN (expression(COMMA expression)*)? R_PAREN) | (PERIOD LENGTH))?
	|	callexp;

//callexp	:	lengexp  (PERIOD ID L_PAREN (expression (COMMA expression)* )? R_PAREN)?;

callexp	:	lhs=lengexp  rhs=periodexp? -> {$rhs.text != null && !$rhs.text.equals("")}? ^(CALLEXP ^(LHS $lhs) ^(RHS $rhs)*) -> $lhs;

periodexp:	 (PERIOD ID L_PAREN (e1=expression (COMMA e2+=expression)* )? R_PAREN) r=((periodexp|(L_BRACKET expression R_BRACKET))*) -> ^(CALL ^(NAME ID) ^(PARAMS_LIST $e1 $e2*)? (^(RHS $r))*);

lengexp	:	arrayexp (PERIOD LENGTH)?;

arrayexp 
	:	newexp (L_BRACKET rhs+=expression R_BRACKET)* ->  {$rhs != null}? ^(ARRAY_ACCESS ^(ARRAY newexp) ^(INDEX $rhs)*) -> newexp;
	
newexp	:	NEW ID L_PAREN R_PAREN -> ^(NEW ID)
	|	NEW INT L_BRACKET expression R_BRACKET -> ^(NEW INTARRAY ^(LENGTH expression))
	|	primeexp;
	
primeexp:	ID -> ^(REFERENCE ID) | TRUE | FALSE | THIS | LitInt;
