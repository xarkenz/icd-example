package token;

/**
 * Token representing an integer literal.
 */
public class IntegerLiteral implements Token {
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
        return "(integer " + this.getValue() + ")";
    }
}
