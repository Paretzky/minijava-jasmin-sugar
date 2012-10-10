%option nodefault
%option bison-bridge bison-locations
%{
#include <ast.h>
#include <parser.h>
#define SAVE_TOKEN /*yylval->string = strdup(yytext);*/charNum += yyleng
#define TOKEN(t) (yylval->token = t)
#define RET_TOKEN(t) {int i = yylval->token = t;charNum += yyleng;return i;}
uint32_t lineNum = 1, charNum = 1;
extern int yywrap() { return 42; }
%}
%%
"//".*"\n"			lineNum++;charNum=1;
"boolean"			RET_TOKEN(BOOLEAN);
"&&"				RET_TOKEN(BOOL_AND);
"<"				RET_TOKEN(LESS_THAN);
"+"				RET_TOKEN(PLUS);
"-"				RET_TOKEN(MINUS);
"*"				RET_TOKEN(MULTIPLY);
"="				RET_TOKEN(EQUALS);
"{"				RET_TOKEN(L_BRACE);
"}"				RET_TOKEN(R_BRACE);
"("				RET_TOKEN(L_PAREN);
")"				RET_TOKEN(R_PAREN);
"["				RET_TOKEN(L_BRACKET);
"]"				RET_TOKEN(R_BRACKET);
"System.out.println"		RET_TOKEN(SOUT);
"."				RET_TOKEN(PERIOD);
"length"			RET_TOKEN(LENGTH);
"true"				RET_TOKEN(TRUE);
"false"				RET_TOKEN(FALSE);
"new"				RET_TOKEN(NEW);
"this"				RET_TOKEN(THIS);
"class"				RET_TOKEN(CLASS);
"public"			RET_TOKEN(PUBLIC);
"static"			RET_TOKEN(STATIC);
"void"				RET_TOKEN(VOID);
"main"				RET_TOKEN(MAIN);
"String"			RET_TOKEN(STRING);
"if"				RET_TOKEN(IF);
"else"				RET_TOKEN(ELSE);
"while"				RET_TOKEN(WHILE);
";"				RET_TOKEN(SEMICOLON);
"int"				RET_TOKEN(INT);
"!"				RET_TOKEN(BANG);
","				RET_TOKEN(COMMA);
"return"			RET_TOKEN(RETURN);
"extern"			RET_TOKEN(EXTERN);
"extends"			RET_TOKEN(EXTENDS);
" "				charNum++;
\t				charNum+=8;
\n				lineNum++;charNum=1;
[a-zA-Z][a-zA-Z0-9_-]*		SAVE_TOKEN; return IDENTIFIER;
[0-9]+				SAVE_TOKEN; return LIT_INT;
.				;
