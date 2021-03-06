// extern is valid only above a MainClass in the form:
//	"extern" Type IDENTIFIER "(" MethodArgList ")" ";"
%{
#define YYERROR_VERBOSE 1
#define YYDEBUG 1
#include<mjcc_driver.hh>

extern "C"
  {
    int yyparse();
    int yylex(void);
    void yyerror(char *s){}
  }

extern "C" int yyparse (void);
%}
%define parser_class_name "mjcc_parser"
%code requires {
     # include <string>
     class mjcc_driver;
     }
%parse-param { mjcc_driver& driver }
%lex-param   { mjcc_driver& driver }
%locations
%initial-action
     {
       // Initialize the initial location.
 //      @$.begin.filename = @$.end.filename = &driver.file;
     };
%debug
%error-verbose

%skeleton "lalr1.cc"
%left PLUS MINUS
%left MULTIPLY
%left BOOL_AND
%left L_BRACKET L_PAREN
%right BANG PERIOD EQUALS
%nonassoc LESS_THAN 

%union {
	int		token;
	int32_t		litInt;
	std::string 	*string;
}
%token <string> IDENTIFIER TYPE
%token <litInt> LIT_INT
%token <token> BOOL_AND LESS_THAN PLUS MINUS MULTIPLY EQUALS L_BRACE R_BRACE L_PAREN R_PAREN L_BRACKET R_BRACKET PERIOD LENGTH
%token <token> TRUE FALSE NEW THIS CLASS PUBLIC STATIC VOID MAIN IF ELSE WHILE SOUT SEMICOLON INT BANG COMMA STRING BOOLEAN RETURN
%token <token> EXTERN EXTENDS END

%start Goal
%%

Goal:		Externs MainClass ClassDeclarations END;

Externs:	/* nothing or */
	|	EXTERN Type IDENTIFIER L_PAREN MethodArgList R_PAREN SEMICOLON Externs;

ClassDeclarations: /*match nothing or*/
	|	ClassDeclaration ClassDeclarations;

ClassDeclaration: CLASS IDENTIFIER ExtendsClass L_BRACE VarDeclarations MethodDeclarations R_BRACE;

ExtendsClass:	/* nothing or */
	|	EXTENDS IDENTIFIER;

MainClass:	CLASS IDENTIFIER L_BRACE PUBLIC STATIC VOID MAIN L_PAREN STRING L_BRACKET R_BRACKET IDENTIFIER R_PAREN L_BRACE Statement R_BRACE R_BRACE;

VarDeclarations: /*nothing or */
	|	VarDeclaration VarDeclarations;

MethodVarDeclarations: Statements
	|	VarDeclaration MethodVarDeclarations;

VarDeclaration: Type IDENTIFIER SEMICOLON;

MethodDeclarations: /* nothing or */
	|	MethodDeclaration MethodDeclarations;

MethodDeclaration: PUBLIC Type IDENTIFIER L_PAREN MethodArgList R_PAREN L_BRACE MethodVarDeclarations RETURN Expression SEMICOLON R_BRACE;

MethodArgList:	/* nothing or */
	| Type IDENTIFIER MethodArgListRest;

MethodArgListRest: /* nothing or */
	| COMMA Type IDENTIFIER MethodArgListRest;

Type:	INT L_BRACKET R_BRACKET
	|	INT
	|	BOOLEAN
	|	IDENTIFIER;

Statements: /* nothing or */
	|	Statement Statements;

Statement:	IDENTIFIER EQUALS Expression SEMICOLON
	|	L_BRACE  Statement NullableStatements R_BRACE
	|	IF L_PAREN Expression R_PAREN Statement ELSE Statement
	|	WHILE L_PAREN Expression R_PAREN Statement
	|	SOUT L_PAREN Expression R_PAREN SEMICOLON
	|	IDENTIFIER L_BRACKET Expression R_BRACKET EQUALS Expression SEMICOLON;

NullableStatements: /* match nothing or */
	|	Statement NullableStatements;

Expression:	Expression L_BRACKET Expression R_BRACKET
	|	Expression PERIOD LENGTH
	|	Expression PERIOD IDENTIFIER L_PAREN ArgList R_PAREN
	|	NEW INT L_BRACKET Expression R_BRACKET
	|	NEW IDENTIFIER L_PAREN R_PAREN
	|	BANG Expression
	|	L_PAREN Expression R_PAREN
	|	Expression BOOL_AND Expression
	|	Expression LESS_THAN Expression
	|	Expression PLUS Expression
	|	Expression MINUS Expression
	|	Expression MULTIPLY Expression
	|	TRUE
	|	FALSE
	|	IDENTIFIER
	|	THIS
	|	LitInt;

LitInt:	PLUS LIT_INT
	|	LIT_INT
	|	MINUS LIT_INT;

ArgList: /* match nothing or */
	| Expression ArgListRest;

ArgListRest: /* match nothing or */
	| COMMA Expression ArgListRest;

%%

void yy::mjcc_parser::error (const yy::mjcc_parser::location_type& l, const std::string& m) {
	//driver.error (l, m);
	
}
