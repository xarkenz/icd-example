package token;

import org.jetbrains.annotations.NotNull;
import syntax.ast.ASTNode;

/**
 * Representation of an identifier from the source code.
 * Serves as a {@link Token} which is then inserted directly into the AST as an {@link ASTNode}.
 */
public class Identifier implements Token, ASTNode {
    /**
     * The name of this identifier, equivalent to its string representation.
     */
    private final @NotNull String name;

    public Identifier(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Get the string representation of this identifier.
     * Equivalent to calling {@link #getName()}.
     * @return The name of this identifier.
     */
    @Override
    public String toString() {
        return this.getName();
    }
}
