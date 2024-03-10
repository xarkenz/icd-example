package syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import syntax.ast.OperatorNode;
import token.BasicToken;
import token.Token;

/**
 * Enumeration of the different types of operations allowed for an {@link OperatorNode}.
 * This includes unary and binary operations, as well as function calls.
 * @see OperatorNode
 * @see Precedence
 */
public enum Operation {
    // Additive
    ADDITION(BasicToken.PLUS, Precedence.ADDITIVE),
    SUBTRACTION(BasicToken.MINUS, Precedence.ADDITIVE),
    // Multiplicative
    DIVISION(BasicToken.SLASH, Precedence.MULTIPLICATIVE),
    MULTIPLICATION(BasicToken.STAR, Precedence.MULTIPLICATIVE);

    /**
     * The key token denoting this operation, if applicable.
     */
    private final @Nullable BasicToken token;
    /**
     * The precedence level assigned to this operation.
     */
    private final @NotNull Precedence precedence;

    Operation(@Nullable BasicToken token, @NotNull Precedence precedence) {
        this.token = token;
        this.precedence = precedence;
    }

    public @Nullable BasicToken getToken() {
        return this.token;
    }

    public @NotNull Precedence getPrecedence() {
        return this.precedence;
    }

    /**
     * Find the operation corresponding to a token, if one exists.
     * @param token A token potentially denoting an operation.
     * @return The operation denoted by the given token, or null if no such operation exists.
     */
    public static @Nullable Operation fromToken(@NotNull Token token) {
        if (token instanceof BasicToken basicToken) {
            return switch (basicToken) {
                case PLUS -> Operation.ADDITION;
                case MINUS -> Operation.SUBTRACTION;
                case STAR -> Operation.MULTIPLICATION;
                case SLASH -> Operation.DIVISION;
                default -> null;
            };
        }
        else {
            return null;
        }
    }
}
