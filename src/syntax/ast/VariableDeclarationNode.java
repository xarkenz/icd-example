package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a local variable declaration.
 * This consists of the name of the variable declared (for now, the type is implied as {@code int}.)
 */
public class VariableDeclarationNode implements ASTNode {
    private final @NotNull String name;

    public VariableDeclarationNode(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Convert this declaration to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this local variable declaration.
     */
    @Override
    public String toString() {
        return "(int " + this.name + ")";
    }
}
