package codegen;

import codegen.symbol.Symbol;
import codegen.symbol.SymbolTable;
import codegen.value.ImmediateInt32;
import codegen.value.Register;
import codegen.value.Value;
import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import syntax.Operation;
import syntax.Parser;
import syntax.ast.ASTNode;
import syntax.ast.OperatorNode;
import syntax.ast.PrintNode;
import syntax.ast.VariableDeclarationNode;
import token.Identifier;
import token.IntegerLiteral;

import java.io.Writer;
import java.util.Objects;

/**
 * Class which takes an AST and handles semantic analysis and code generation.
 * The use of this class as an instance is not allowed outside of this class;
 * instead, external code must call the static method {@link #generate(Writer, Parser, String, boolean)}.
 * @see Emitter
 */
public class Generator {
    /**
     * Used to generate the syntax for LLVM instructions and write them to the output.
     */
    private final @NotNull Emitter emitter;
    /**
     * The symbol table used to store all symbols during generation
     */
    private final @NotNull SymbolTable symbolTable;
    /**
     * Numeric identifier for the virtual register {@link #createRegister(int)} returns the next time it is called.
     * Initial value is 1.
     */
    private int nextRegisterNumber;

    private Generator(@NotNull Emitter emitter) {
        this.emitter = emitter;
        this.symbolTable = new SymbolTable();
        this.nextRegisterNumber = 1;
    }

    /**
     * Create a new virtual register with a numeric identifier. These identifiers are treated specially
     * by LLVM in that they are required to count up from 1 in the order that the registers are defined.
     * @param bitCount The number of bits in the integer this register will store.
     * @return A new virtual register identified with the next unused register number.
     */
    private @NotNull Register createRegister(int bitCount) {
        String identifier = Integer.toString(this.nextRegisterNumber++);
        return new Register(identifier, bitCount);
    }

    /**
     * Get a symbol from the symbol table by name, throwing an exception if it does not exist.
     * @param name The name to search for in the symbol table.
     * @return The relevant symbol in the symbol table with the given name.
     * @throws CompilerError Thrown if the given name does not correspond to a symbol in the symbol table.
     */
    private @NotNull Symbol getSymbol(@NotNull String name) throws CompilerError {
        Symbol symbol = this.symbolTable.find(name);

        if (symbol == null) {
            throw new CompilerError("undefined symbol '" + name + "'");
        }
        else {
            return symbol;
        }
    }

    /**
     * Attempt to convert a value to a given type. If the value already has the given type, no conversion is done.
     * @param value The value to convert.
     * @param targetBitCount The desired integer width for the value to have after the conversion.
     * @return A new value with the desired width if a conversion was performed, or the input value otherwise.
     * @throws CompilerError Thrown if a conversion could not be performed between the two types.
     */
    private @NotNull Value convertValueType(@NotNull Value value, int targetBitCount) throws CompilerError {
        if (value.getBitCount() == targetBitCount) {
            // We already have a value of the desired type, so leave it unchanged
            return value;
        }

        // Regardless of what we do, we'll need an output register
        Register result = this.createRegister(targetBitCount);

        if (value.getBitCount() == 1) {
            // Since we are converting from a boolean to something else, perform a zero extension
            // instead of a sign extension so the boolean isn't treated as a sign bit
            this.emitter.emitZeroExtension(result, value);
        }
        else if (targetBitCount == 1) {
            // Since we are converting to a boolean from something else, compare whether the value != 0
            // instead of truncating so the boolean value isn't derived from the least significant bit only
            this.emitter.emitComparison(result, "ne", value, new ImmediateInt32(0));
        }
        else {
            throw new CompilerError("unsupported conversion from i" + value.getBitCount() + " to i" + targetBitCount);
        }

        return result;
    }

    /**
     * Recursively generate the LLVM code for an AST using a postorder traversal.
     * @param node The subtree of the AST to generate.
     * @return The resulting value of the subtree, or null if there is none.
     * @throws CompilerError Thrown if any unrecognized AST nodes are encountered (which should not happen).
     */
    private @Nullable Value generateNode(@NotNull ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            // Simply turn the integer literal into an immediate value
            return new ImmediateInt32(literal.getValue());
        }
        else if (node instanceof Identifier identifier) {
            // Get the stack pointer corresponding to the given identifier
            Symbol symbol = this.getSymbol(identifier.getName());
            Register pointer = symbol.getRegister();

            // Emit a load instruction to obtain the local variable's value
            Register result = this.createRegister(32);
            this.emitter.emitLoad(result, pointer);

            return result;
        }
        else if (node instanceof OperatorNode operator) {
            if (operator.getOperation().equals(Operation.ASSIGNMENT)) {
                // Assignment has to be handled a little differently
                // First, generate and emit the right-hand side as usual
                Value rhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[1]));
                rhs = this.convertValueType(rhs, 32);
                // If the operator was parsed properly, the first operand must be an identifier
                Identifier identifier = (Identifier) operator.getOperands()[0];
                // Get the stack pointer corresponding to the given identifier
                Symbol symbol = this.getSymbol(identifier.getName());
                Register pointer = symbol.getRegister();

                // Emit a store instruction to change the value of the variable
                this.emitter.emitStore(rhs, pointer);

                return null;
            }
            else {
                // For now, we can just assume that we are dealing with a binary operator
                // Recursively generate the operands first, and obtain the resulting values
                Value lhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[0]));
                lhs = this.convertValueType(lhs, 32);
                Value rhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[1]));
                rhs = this.convertValueType(rhs, 32);

                // Each branch creates an anonymous register to hold the resulting value of the operation
                Register result;

                // Emit the instruction corresponding to the operation
                switch (operator.getOperation()) {
                    case ADDITION -> {
                        result = this.createRegister(32);
                        this.emitter.emitAddition(result, lhs, rhs);
                    }
                    case SUBTRACTION -> {
                        result = this.createRegister(32);
                        this.emitter.emitSubtraction(result, lhs, rhs);
                    }
                    case MULTIPLICATION -> {
                        result = this.createRegister(32);
                        this.emitter.emitMultiplication(result, lhs, rhs);
                    }
                    case DIVISION -> {
                        result = this.createRegister(32);
                        this.emitter.emitDivision(result, lhs, rhs);
                    }
                    case EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "eq", lhs, rhs);
                    }
                    case NOT_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "ne", lhs, rhs);
                    }
                    case LESS_THAN -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "slt", lhs, rhs);
                    }
                    case GREATER_THAN -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sgt", lhs, rhs);
                    }
                    case LESS_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sle", lhs, rhs);
                    }
                    case GREATER_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sge", lhs, rhs);
                    }
                    default -> throw new CompilerError("operation '" + operator.getOperation().getToken() + "' not implemented");
                }

                return result;
            }
        }
        else if (node instanceof VariableDeclarationNode declaration) {
            // Allocate space on the stack for this new variable, creating a register to hold the pointer
            Register pointer = new Register(declaration.getName(), 32);
            this.emitter.emitStackAllocation(pointer);
            // Create a symbol for the local variable and add it to the symbol table
            Symbol symbol = new Symbol(declaration.getName(), pointer);
            this.symbolTable.insert(symbol);

            return null;
        }
        else if (node instanceof PrintNode printStatement) {
            // Generate and emit the "printee" expression
            Value value = Objects.requireNonNull(this.generateNode(printStatement.getPrintee()));
            value = this.convertValueType(value, 32);

            // Emit code to print the result of the expression to standard output.
            // Since this emits a call to printf(), which returns a value, a new register is created to hold that value
            // (and is subsequently never used again)
            Register discardedResult = this.createRegister(32);
            this.emitter.emitPrint(discardedResult, value);

            return null;
        }
        else {
            throw new CompilerError("unrecognized AST node type");
        }
    }

    /**
     * Generate and emit all of the LLVM code needed for a complete AST. This method is the primary public-facing
     * way of using this class.
     * @param writer The destination writer for the emitted LLVM code.
     * @param parser The source of statements in the form of ASTs.
     * @param sourceFilename A string describing the location of the source code for runtime debugging purposes.
     * @param enableDebug A flag enabling debug output as the program is generated.
     * @throws CompilerError Thrown if the program semantics are determined to be invalid.
     */
    public static void generate(@NotNull Writer writer, @NotNull Parser parser, @NotNull String sourceFilename, boolean enableDebug) throws CompilerError {
        // Create an emitter for the generator to use
        Emitter emitter = new Emitter(writer);

        emitter.emitPreamble(sourceFilename);

        // Create an instance of this class to keep internal state
        Generator generator = new Generator(emitter);
        // As each statement is parsed by the parser, generate and emit the code for that statement
        ASTNode statement = parser.parseStatement();
        while (statement != null) {
            if (enableDebug) {
                System.out.println("Parsed statement: " + statement);
            }

            generator.generateNode(statement);
            statement = parser.parseStatement();
        }

        emitter.emitPostamble();
    }
}
