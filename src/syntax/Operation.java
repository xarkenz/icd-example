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
    // Assignment
    ASSIGNMENT(BasicToken.EQUAL, Precedence.ASSIGNMENT),
    // Equality
    EQUAL(BasicToken.DOUBLE_EQUAL, Precedence.EQUALITY),
    NOT_EQUAL(BasicToken.BANG_EQUAL, Precedence.EQUALITY),
    // Inequality
    LESS_THAN(BasicToken.LESS_THAN, Precedence.INEQUALITY),
    GREATER_THAN(BasicToken.GREATER_THAN, Precedence.INEQUALITY),
    LESS_EQUAL(BasicToken.LESS_EQUAL, Precedence.INEQUALITY),
    GREATER_EQUAL(BasicToken.GREATER_EQUAL, Precedence.INEQUALITY),
    // Additive
    ADDITION(BasicToken.PLUS, Precedence.ADDITIVE),
    SUBTRACTION(BasicToken.MINUS, Precedence.ADDITIVE),
    // Multiplicative
    MULTIPLICATION(BasicToken.STAR, Precedence.MULTIPLICATIVE),
    DIVISION(BasicToken.SLASH, Precedence.MULTIPLICATIVE),
    REMAINDER(BasicToken.PERCENT, Precedence.MULTIPLICATIVE);

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
//                case EQUAL -> Operation.ASSIGNMENT;
                case DOUBLE_EQUAL -> Operation.EQUAL;
                case BANG_EQUAL -> Operation.NOT_EQUAL;
                case LESS_THAN -> Operation.LESS_THAN;
                case GREATER_THAN -> Operation.GREATER_THAN;
                case LESS_EQUAL -> Operation.LESS_EQUAL;
                case GREATER_EQUAL -> Operation.GREATER_EQUAL;
                case PLUS -> Operation.ADDITION;
                case MINUS -> Operation.SUBTRACTION;
                case STAR -> Operation.MULTIPLICATION;
                case SLASH -> Operation.DIVISION;
                case PERCENT -> Operation.REMAINDER;
                default -> null;
            };
        }
        else {
            return null;
        }
    }
}
