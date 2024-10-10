package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a function call as part of an expression.
 * This consists of the "callee," which is the name of the function to be called, as well as the arguments
 * to the function call.
 */
public class FunctionCallNode implements ASTNode {
    private final @NotNull String callee;
    private final @NotNull ASTNode[] arguments;

    public FunctionCallNode(@NotNull String callee, @NotNull ASTNode[] arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }

    public @NotNull String getCallee() {
        return this.callee;
    }

    public @NotNull ASTNode[] getArguments() {
        return this.arguments;
    }

    /**
     * Convert this operator to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this function call node.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("(").append(this.getCallee()).append("(");
        for (ASTNode argument : this.getArguments()) {
            output.append(argument).append(", ");
        }
        return output.append("))").toString();
    }
}
