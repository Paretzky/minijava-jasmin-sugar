/*
This code started it's life at: http://www.gnu.org/software/bison/manual/html_node/A-Complete-C_002b_002b-Example.html
*/

#ifndef MJCC_HH
# define MJCC_HH
# include<string>
# include<map>
# include<parser.h>
# define YY_DECL yy::mjcc_parser::token_type \
       yylex (yy::mjcc_parser::semantic_type* yylval,yy::mjcc_parser::location_type* yylloc, mjcc_driver& driver)
YY_DECL;

 extern "C" int yyparse (void);

class mjcc_driver {
public:
	int result;
	std::map<std::string,int> variables;

	mjcc_driver();
	virtual ~mjcc_driver();

	void scan_begin();
	void scan_end();
	bool trace;
};

#endif // ! MJCC_HH
