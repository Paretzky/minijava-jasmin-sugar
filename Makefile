CC = clang
CFLAGS = -Wall
DEST_DIR := ./build/

all:	setup parser lexer test
	
test: test.c
	$(CC) $(CFLAGS) -o $(DEST_DIR)parser -I. -I$(DEST_DIR) test.c $(DEST_DIR)parser.c $(DEST_DIR)lexer.c

setup:	.IGNORE

.IGNORE:
	mkdir $(DEST_DIR)
	

parser:	setup parser.y
	bison -d -v -o $(DEST_DIR)parser.c parser.y

lexer:	setup lexer.lex
	flex -o $(DEST_DIR)lexer.c lexer.lex

clean:
	rm -rf build test
