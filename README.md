## minijava-jasmin-sugar
Compiler for CSC444 at SUNY Oswego.

Targets MiniJava <http://www.cambridge.org/us/features/052182060X/> and Java 7 Virtual Machines <http://docs.oracle.com/javase/specs/jvms/se7/html/index.html>.

Jasmin <http://jasmin.sourceforge.net/> is a Java bytecode assembler which will be used later for code generation.

The sugar, refers to <http://en.wikipedia.org/wiki/Syntactic_sugar> in my case I'll be add the following to minijava:
*  do-while: do { someStuff(); } while(condition);
* for-in: for(int i in someIntArray) { someStuffWith(i); }

## Milestone-One-Antlr
Tag representing the second submission for parser.  Due to time constraints I moved from bison/flex/clang to antlr/jasmin.  This tag will take one file name as an argument, and output any syntactic errors.  If the input is valid, no output is generated.

## Dependencies 
The following is the version of packages used for development.  Effort will be made later to ensure some level of portability.
* java-1.7.0-openjdk-1.7.0.9-2.3.3.fc17.1.x86_64
* antlr3-tool-3.4-12.fc17.noarch (/usr/share/java/antlr3.jar)
* antlr3-java-3.4-12.fc17.noarch (/usr/share/java/antlr3-runtime.jar)
* stringtemplate-3.2.1-3.fc17.noarch (/usr/share/java/stringtemplate.jar)
* make-3.82-13.fc17.x86_64

## prev/
This project started with flex and bison targeting Clang's AST.  The prev/ contains the latest unstable before that work was abandoned.  See the tags for stable snapshots
