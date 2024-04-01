package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a {@code while} loop, which checks its boolean condition
 * at the beginning of every iteration and breaks if it is false.
 * This consists of the condition expression and the loop body statement.
 */
public class WhileLoopNode implements ASTNode {
    /**
     * The condition expression, used to determine whether to break before an iteration starts.
     */
    private final @NotNull ASTNode condition;
    /**
     * The loop body, which is executed in each iteration where the condition is true.
     */
    private final @NotNull ASTNode loopBody;

    public WhileLoopNode(@NotNull ASTNode condition, @NotNull ASTNode loopBody) {
        this.condition = condition;
        this.loopBody = loopBody;
    }

    public @NotNull ASTNode getCondition() {
        return this.condition;
    }

    public @NotNull ASTNode getLoopBody() {
        return this.loopBody;
    }

    /**
     * Convert this {@code while} loop to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this loop.
     */
    @Override
    public String toString() {
        return "(while " + this.getCondition() + " " + this.getLoopBody() + ")";
    }
}
