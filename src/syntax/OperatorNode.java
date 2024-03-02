package syntax;

import org.jetbrains.annotations.NotNull;

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
}
