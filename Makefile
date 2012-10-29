CC := clang
CFLAGS := 
DEBUG_CFLAGS := -Wall -Wextra
DEST_DIR := ./build/

all:	setup parser lexer test
.PHONY: all

debug:	setup parser lexer debug_test

setup:	
	mkdir $(DEST_DIR)
	
parser: parser.y
	bison -d -v -o $(DEST_DIR)parser.c parser.y

lexer: lexer.lex
	flex -o $(DEST_DIR)lexer.c lexer.lex

test:	test.c
	$(CC) $(CFLAGS) -g -o $(DEST_DIR)parser -I. -I$(DEST_DIR) test.c $(DEST_DIR)parser.c $(DEST_DIR)lexer.c
	
debug_test: test.c
	$(CC) $(DEBUG_CFLAGS) -o $(DEST_DIR)parser -I. -I$(DEST_DIR) test.c $(DEST_DIR)parser.c $(DEST_DIR)lexer.c

clean:
	rm -rf build test
