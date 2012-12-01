## minijava-jasmin-sugar
Compiler for CSC444 at SUNY Oswego.

Targets MiniJava <http://www.cambridge.org/us/features/052182060X/> and Java 7 Virtual Machines <http://docs.oracle.com/javase/specs/jvms/se7/html/index.html>.

Jasmin <http://jasmin.sourceforge.net/> is a Java bytecode assembler which will be used later for code generation.

The sugar, refers to <http://en.wikipedia.org/wiki/Syntactic_sugar> in my case I'll be add the following to minijava:
*  do-while: do { someStuff(); } while(condition);
* for-in: for(int i in someIntArray) { someStuffWith(i); }

## prev/
This project started with flex and bison targeting Clang's AST.  The prev/ contains the latest unstable before that work was abandoned.  See the tags for stable snapshots
