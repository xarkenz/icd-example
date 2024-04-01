package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a block of multiple statements enclosed in curly braces.
 * This consists of the array of statements contained by the block statement.
 */
public class BlockStatementNode implements ASTNode {
    private final @NotNull ASTNode[] statements;

    public BlockStatementNode(@NotNull ASTNode[] statements) {
        this.statements = statements;
    }

    public @NotNull ASTNode[] getStatements() {
        return statements;
    }

    /**
     * Convert this block statement to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this block statement.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("{ ");
        for (ASTNode statement : this.getStatements()) {
            output.append(statement).append("; ");
        }
        return output.append("}").toString();
    }
}
