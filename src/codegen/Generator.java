package codegen;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import syntax.ASTNode;
import syntax.OperatorNode;
import token.IntegerLiteral;

import java.io.Writer;

public class Generator {
    private final @NotNull Emitter emitter;
    private int nextRegisterNumber;

    private Generator(@NotNull Emitter emitter) {
        this.emitter = emitter;
        this.nextRegisterNumber = 1;
    }

    private @NotNull Register createRegister() {
        return new Register(Integer.toString(this.nextRegisterNumber++));
    }

    private @NotNull Register generateNode(@NotNull ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            Register pointer = this.createRegister();
            this.emitter.emitStackAllocation(pointer);
            this.emitter.emitStore(literal.getValue(), pointer);
            Register result = this.createRegister();
            this.emitter.emitLoad(result, pointer);
            return result;
        }
        else if (node instanceof OperatorNode operator) {
            // Evaluate the left-hand side and right-hand side
            Register lhs = this.generateNode(operator.getOperands()[0]);
            Register rhs = this.generateNode(operator.getOperands()[1]);

            Register result = this.createRegister();

            // Apply the corresponding operation to the interpreted values
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

    public static void generate(@NotNull Writer writer, @NotNull ASTNode expression, @NotNull String sourceFilename) throws CompilerError {
        Emitter emitter = new Emitter(writer);
        emitter.emitPreamble(sourceFilename);
        Generator generator = new Generator(emitter);
        Register result = generator.generateNode(expression);
        emitter.emitPrint(result);
        emitter.emitPostamble();
    }
}
