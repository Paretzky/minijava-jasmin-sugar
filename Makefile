CC := g++
CFLAGS :=
DEBUG_CFLAGS := -Wall -Wextra
DEST_DIR := ./build/

GRAMMAR_FILE := ./src/minijava.g
ANTLR_CP := /usr/share/java/stringtemplate.jar:/usr/share/java/antlr3.jar:/usr/share/java/antlr3-runtime.jar:/usr/share/java/antlr.jar

all:	setup grammar javac
.PHONY: all

setup:	
	mkdir $(DEST_DIR)

grammar:
	antlr3 -verbose -o $(DEST_DIR) $(GRAMMAR_FILE) 

javac:
	javac -cp $(ANTLR_CP):./build/com/benparetzky/minijava-jasmin-sugar -d $(DEST_DIR) $(DEST_DIR)src/minijavaLexer.java $(DEST_DIR)src/minijavaParser.java src/com/benparetzky/minijavajasminsugar/*java

clean:
	rm -rf build
