package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a {@code print} statement.
 * This consists of a "printee," which is the value to print during execution.
 */
public class PrintNode implements ASTNode {
    private final @NotNull ASTNode printee;

    public PrintNode(@NotNull ASTNode printee) {
        this.printee = printee;
    }

    public @NotNull ASTNode getPrintee() {
        return this.printee;
    }

    /**
     * Convert this statement to a string, primarily for debug purposes.
     * @return An approximation of the syntax used to create this print statement.
     */
    @Override
    public String toString() {
        return "(print " + this.printee + ")";
    }
}
