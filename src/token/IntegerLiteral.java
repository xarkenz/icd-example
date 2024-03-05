package token;

/**
 * Representation of a 32-bit integer literal from the source code.
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

    /**
     * Get the string representation of this literal's integer value in base 10.
     * @return The string representation of this literal's integer value in base 10.
     */
    @Override
    public String toString() {
        return "(integer " + this.getValue() + ")";
    }
}
