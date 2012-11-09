CC := g++
CFLAGS :=
DEBUG_CFLAGS := -Wall -Wextra
DEST_DIR := ./build/

INCLUDE_DIR = ./src/include
LEXER_FILE = ./src/lexer/lexer.lex
BISON_FILE = ./src/parser/parser.yy

all:	setup parser lexer test
.PHONY: all

debug:	setup parser lexer debug_test

setup:	
	mkdir $(DEST_DIR)
	
parser: setup lexer $(BISON_FILE)
	bison -d -v -o $(DEST_DIR)parser.c $(BISON_FILE)

lexer: setup $(LEXER_FILE)
	flex -o $(DEST_DIR)lexer.c $(LEXER_FILE)

test:	./src/test.cc
	$(CC) $(CFLAGS) -g -o $(DEST_DIR)parser -I$(INCLUDE_DIR) -I$(DEST_DIR) ./src/test.cc -lfl $(DEST_DIR)parser.c $(DEST_DIR)lexer.c
	
debug_test: test.c
	$(CC) $(DEBUG_CFLAGS) -o $(DEST_DIR)parser -I$(INCLUDE_DIR) -I$(DEST_DIR) test.cc -lfl $(DEST_DIR)parser.c $(DEST_DIR)lexer.c

clean:
	rm -rf build test
