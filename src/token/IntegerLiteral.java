package token;

import syntax.ASTNode;

/**
 * Representation of an integer literal from the source code.
 * Serves as a token, but is inserted directly into the AST.
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

    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
