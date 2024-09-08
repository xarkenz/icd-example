package syntax.ast;

import org.jetbrains.annotations.NotNull;

/**
 * {@link ASTNode} representing a top-level function definition.
 * This consists of the name of the function, its parameters, and the body statement.
 * (For now, all types are implied to be {@code int}.)
 */
public class FunctionDefinitionNode implements ASTNode {
    /**
     * The name of the function defined.
     */
    private final @NotNull String name;
    /**
     * The list of parameters this function is defined with.
     */
    private final @NotNull VariableDeclarationNode[] parameters;
    /**
     * The {@link BlockStatementNode} representing the function body.
     */
    private final @NotNull ASTNode body;

    public FunctionDefinitionNode(@NotNull String name, @NotNull VariableDeclarationNode[] parameters, @NotNull ASTNode body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull VariableDeclarationNode[] getParameters() {
        return this.parameters;
    }

    public @NotNull ASTNode getBody() {
        return this.body;
    }

    /**
     * Convert this function definition to a string, primarily for debug purposes.
     * @return A representation of the syntax used to create this function definition.
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("(int ").append(this.getName()).append("(");
        for (VariableDeclarationNode parameter : this.getParameters()) {
            output.append(parameter).append(", ");
        }
        return output.append(") ").append(this.getBody()).append(")").toString();
    }
}
