## minijava-clang-compiler
Compiler for CSC444 at SUNY Oswego.

###Features
Targets MiniJava <http://www.cambridge.org/us/features/052182060X/> and the Clang <http://clang.llvm.org/> Abstract Syntax Tree <http://clang.llvm.org/doxygen/dir_0650a8545092ec289ec4fa137f1d5911.html>.

###Dependencies (tested version(s))
* flex (2.5.35-13.fc15.x86_64)
* bison (2.5-1.fc16.x86_64)
* clang (2.9-10.fc16.x86_64)
* make (3.82-8.fc16.x86_64)

###Milestone One
Takes in one MiniJava source file from stdin.  Parses the file, no semantic checks are made, no symbol tables etc.

Returns 0 on success. On the first parse error the parse stops, an error is printed to stdout and the program exits a non-zero value.
