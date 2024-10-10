package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a {@code return} statement.
 * This consists of a return value, which is the value to return from the current function.
 */
public class ReturnNode implements ASTNode {
    private final @NotNull ASTNode returnValue;

    public ReturnNode(@NotNull ASTNode returnValue) {
        this.returnValue = returnValue;
    }

    public @NotNull ASTNode getReturnValue() {
        return this.returnValue;
    }

    /**
     * Convert this statement to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this return statement.
     */
    @Override
    public String toString() {
        return "(return " + this.returnValue + ")";
    }
}
