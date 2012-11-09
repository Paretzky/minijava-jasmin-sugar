#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include <unistd.h>

#define IDENT_MAX_LEGNTH 256

struct varDeclaration {
	char *type, *ident;
};
