package syntax.ast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link ASTNode} representing a conditional statement denoted by the
 * {@code if} and {@code else} keywords. This consists of the condition expression,
 * the consequent path, and optionally an alternative path.
 */
public class ConditionalNode implements ASTNode {
    /**
     * The condition expression, used to determine which path to take.
     */
    private final @NotNull ASTNode condition;
    /**
     * The consequent statement, which is executed if the condition evaluates to true.
     */
    private final @NotNull ASTNode consequent;
    /**
     * The alternative statement, which is executed if the condition evaluates to false.
     * A value of null indicates that the alternative behavior is to simply skip past
     * the consequent path.
     */
    private final @Nullable ASTNode alternative;

    public ConditionalNode(@NotNull ASTNode condition, @NotNull ASTNode consequent, @Nullable ASTNode alternative) {
        this.condition = condition;
        this.consequent = consequent;
        this.alternative = alternative;
    }

    public @NotNull ASTNode getCondition() {
        return this.condition;
    }

    public @NotNull ASTNode getConsequent() {
        return this.consequent;
    }

    public @Nullable ASTNode getAlternative() {
        return this.alternative;
    }

    /**
     * Convert this conditional statement to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this conditional.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("(if ").append(this.getCondition())
            .append(" ").append(this.getConsequent());
        if (this.getAlternative() != null) {
            output.append(" ").append(this.getAlternative());
        }
        return output.append(")").toString();
    }
}
