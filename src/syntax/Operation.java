package syntax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import token.BasicToken;
import token.Token;

public enum Operation {
    // Additive
    ADDITION(BasicToken.PLUS, Precedence.ADDITIVE),
    SUBTRACTION(BasicToken.MINUS, Precedence.ADDITIVE),
    // Multiplicative
    DIVISION(BasicToken.SLASH, Precedence.MULTIPLICATIVE),
    MULTIPLICATION(BasicToken.STAR, Precedence.MULTIPLICATIVE);

    private final BasicToken token;
    private final Precedence precedence;

    Operation(BasicToken token, Precedence precedence) {
        this.token = token;
        this.precedence = precedence;
    }

    public BasicToken getToken() {
        return this.token;
    }

    public Precedence getPrecedence() {
        return this.precedence;
    }

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
