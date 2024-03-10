package token;

import syntax.ast.ASTNode;

/**
 * Representation of a 32-bit integer literal from the source code.
 * Serves as a {@link Token} which is then inserted directly into the AST as an {@link ASTNode}.
 */
public class IntegerLiteral implements Token, ASTNode {
    /**
     * The integer value represented by this literal.
     */
    private final int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    /**
     * Get the string representation of this literal's integer value in base 10.
     * @return The string representation of this literal's integer value in base 10.
     * @see Integer#toString(int)
     */
    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
