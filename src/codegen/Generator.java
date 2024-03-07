package codegen;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import syntax.ASTNode;
import syntax.OperatorNode;
import token.IntegerLiteral;

import java.io.Writer;

/**
 * Class which takes an AST and handles semantic analysis and code generation.
 * The use of this class as an instance is not allowed outside of this class;
 * instead, external code must call the static method {@link #generate(Writer, ASTNode, String)}.
 * @see Emitter
 */
public class Generator {
    /**
     * Used to generate the syntax for LLVM instructions and write them to the output.
     */
    private final @NotNull Emitter emitter;
    /**
     * Numeric identifier for the virtual register {@link #createRegister()} returns the next time it is called.
     * Initial value is 1.
     */
    private int nextRegisterNumber;

    private Generator(@NotNull Emitter emitter) {
        this.emitter = emitter;
        this.nextRegisterNumber = 1;
    }

    /**
     * Create a new virtual register with a numeric identifier. These identifiers are treated specially
     * by LLVM in that they are required to count up from 1 in the order that the registers are defined.
     * @return A new virtual register identified with the next unused register number.
     */
    private @NotNull Register createRegister() {
        return new Register(Integer.toString(this.nextRegisterNumber++));
    }

    /**
     * Recursively generate the LLVM code for an AST using a postorder traversal.
     * @param node The subtree of the AST to generate.
     * @return The resulting value of the subtree.
     * @throws CompilerError Thrown if any unrecognized AST nodes are encountered (which should not happen).
     */
    private @NotNull Register generateNode(@NotNull ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            // Using the stack is completely unnecessary here, but this way we can get practice using it early on
            // Allocate space on the stack to hold the integer value, and hold the pointer to it in a register
            Register pointer = this.createRegister();
            this.emitter.emitStackAllocation(pointer);
            // Store the integer value from the literal on the stack
            this.emitter.emitStore(literal.getValue(), pointer);
            // Immediately load the integer value from the stack into a register
            Register result = this.createRegister();
            this.emitter.emitLoad(result, pointer);

            return result;
        }
        else if (node instanceof OperatorNode operator) {
            // For now, we can just assume that we are dealing with a binary operator; this will change in the future
            // Recursively generate the operands first, and obtain the resulting values
            Register lhs = this.generateNode(operator.getOperands()[0]);
            Register rhs = this.generateNode(operator.getOperands()[1]);
            // Create an anonymous register to hold the resulting value of the operation
            Register result = this.createRegister();

            // Emit the instruction corresponding to the operation
            switch (operator.getOperation()) {
                case ADDITION -> this.emitter.emitAddition(result, lhs, rhs);
                case SUBTRACTION -> this.emitter.emitSubtraction(result, lhs, rhs);
                case MULTIPLICATION -> this.emitter.emitMultiplication(result, lhs, rhs);
                case DIVISION -> this.emitter.emitDivision(result, lhs, rhs);
            }

            return result;
        }
        else {
            throw new CompilerError("unrecognized AST node type");
        }
    }

    /**
     * Generate and emit all of the LLVM code needed for a complete AST. This method is the primary public-facing
     * way of using this class.
     * @param writer The destination writer for the emitted LLVM code.
     * @param expression The complete AST to generate and emit code for.
     * @param sourceFilename A string describing the location of the source code for debugging purposes.
     * @throws CompilerError Thrown if the program semantics are determined to be invalid.
     */
    public static void generate(@NotNull Writer writer, @NotNull ASTNode expression, @NotNull String sourceFilename) throws CompilerError {
        // Create an emitter for the generator to use
        Emitter emitter = new Emitter(writer);

        emitter.emitPreamble(sourceFilename);
        // Create an instance of this class to keep internal state
        Generator generator = new Generator(emitter);
        // Generate and emit the expression passed in
        Register result = generator.generateNode(expression);
        // Print the result of the expression to standard output
        emitter.emitPrint(result);
        emitter.emitPostamble();
    }
}
