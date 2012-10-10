#include <iostream>
#include "ast.h"
extern struct varDeclaration* testDec;
extern int yyparse();

int main(int argc, char **argv)
{
    yyparse();
    std::cout << testDec << std::endl;
    return 0;
}
