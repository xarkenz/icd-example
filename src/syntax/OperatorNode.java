package syntax;

import org.jetbrains.annotations.NotNull;

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
     * @return An approximation of the syntax used to create this operator node.
     */
    @Override
    public String toString() {
        return switch (this.operation) {
            case ADDITION -> "(" + this.operands[0] + " + " + this.operands[1] + ")";
            case SUBTRACTION -> "(" + this.operands[0] + " - " + this.operands[1] + ")";
            case MULTIPLICATION -> "(" + this.operands[0] + " * " + this.operands[1] + ")";
            case DIVISION -> "(" + this.operands[0] + " / " + this.operands[1] + ")";
        };
    }
}
