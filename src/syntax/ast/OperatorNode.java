package syntax.ast;

import org.jetbrains.annotations.NotNull;
import syntax.Operation;

/**
 * {@link ASTNode} representing an operator as part of an expression.
 * This consists of the {@link Operation} being done, as well as the operand(s).
 * @see Operation
 */
public class OperatorNode implements ASTNode {
    private final @NotNull Operation operation;
    private final @NotNull ASTNode[] operands;

    public OperatorNode(@NotNull Operation operation, @NotNull ASTNode[] operands) {
        this.operation = operation;
        this.operands = operands;
    }

    public @NotNull Operation getOperation() {
        return this.operation;
    }

    public @NotNull ASTNode[] getOperands() {
        return this.operands;
    }

    /**
     * Convert this operator to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this operator node.
     */
    @Override
    public String toString() {
        return "(" + this.operands[0] + " " + this.operation.getToken() + " " + this.operands[1] + ")";
    }
}
