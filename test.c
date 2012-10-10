#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/mman.h>

// gcc -Wall -std=c99 -D_GNU_SOURCE -o a1 a1.c

extern int yyparse(void);

int main(int argc, char ** args) {
	return yyparse();
}
