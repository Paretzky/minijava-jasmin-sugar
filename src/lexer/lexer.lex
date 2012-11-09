%option noyywrap nounput batch debug
%{ 
	# include <cstdlib>
	# include <cerrno>
		# include <climits>
	# include <string>
	# include "mjcc_driver.hh"
	# include "parser.h"

	# undef yywrap
	# define yywrap() 1
     
	#define yyterminate() return token::END

	# define YY_USER_ACTION  yylloc->columns (yyleng);

	extern  int yylex();
%}
%%
%{
	yylloc->step ();
	typedef yy::mjcc_parser::token token;
	
%}
"boolean"			return token::BOOLEAN;
"&&"				return token::BOOL_AND;
"<"				return token::LESS_THAN;
"+"				return token::PLUS;
"-"				return token::MINUS;
"*"				return token::MULTIPLY;
"="				return token::EQUALS;
"{"				return token::L_BRACE;
"}"				return token::R_BRACE;
"("				return token::L_PAREN;
")"				return token::R_PAREN;
"["				return token::L_BRACKET;
"]"				return token::R_BRACKET;
"System.out.println"		return token::SOUT;
"."				return token::PERIOD;
"length"			return token::LENGTH;
"true"				return token::TRUE;
"false"				return token::FALSE;
"new"				return token::NEW;
"this"				return token::THIS;
"class"				return token::CLASS;
"public"			return token::PUBLIC;
"static"			return token::STATIC;
"void"				return token::VOID;
"main"				return token::MAIN;
"String"			return token::STRING;
"if"				return token::IF;
"else"				return token::ELSE;
"while"				return token::WHILE;
";"				return token::SEMICOLON;
"int"				return token::INT;
"!"				return token::BANG;
","				return token::COMMA;
"return"			return token::RETURN;
"extern"			return token::EXTERN;
"extends"			return token::EXTENDS;
[a-zA-Z][a-zA-Z0-9_-]*		{
					yylval->string = new std::string(yytext); return token::IDENTIFIER;
				}
[0-9]+				{
					errno = 0;
					long int l = strtol(yytext,NULL,10);
					if (!(INT_MIN <= l && l <= INT_MAX && errno != ERANGE)) {
						//driver.error (*yylloc, "integer is out of range");
					}
					yylval->litInt = (int32_t) l;
					return token::LIT_INT;
				}
[ \t]+   			yylloc->step ();
[\n]+      			yylloc->lines (yyleng); yylloc->step ();

%%

void mjcc_driver::scan_begin () {
	yyin = stdin;
}
     
void mjcc_driver::scan_end () {
	fclose (yyin);
}
