# ICD Example Compiler

This is an example compiler written in Java for the Introduction to Compiler Design workshop. It is able to compile
a subset of the C language to LLVM-IR, which can then be compiled into a platform-dependent executable using LLVM.

The development of this compiler generally follows the ICD workshop concepts. More information about ICD can be
found at [xarkenz.github.io/icd](https://xarkenz.github.io/icd/).

If you are coming here from ICD, you may want to switch to a branch other than `main` to see the incremental
progress of the compiler implementation (see below).

### Branches

- `main`: The current development state of the compiler. If you are looking for the implementation of a specific topic,
  it may be more helpful to switch to one of the branches listed below.
- `01_scanning`: First implementation of the token scanner, along with a basic command-line interface.
- `02_parsing-ast`: First (flawed) implementation of expression parsing and the AST.
  Includes a basic interpreter for testing purposes.
- `03_pratt-parsing`: Implementation of Pratt parsing for handling operator precedence and associativity.
- `04_llvm-generation`: First implementation of LLVM-IR code generation.
- `05_statements`: Adds a `print` statement and allows for multiple statements in one program.
- `06_local-variables`: Adds the ability to declare, assign to, and use local variables.
- `07_comparisons`: Adds new operators for comparisons, as well as a boolean type.
- `08_conditionals-loops`: Adds `if`/`else` statements and `while` loops, along with compound statements.
- `09_functions`: Adds function definitions, function calls, and `return` statements (plus the remainder
  operation, `%`, just for fun).
