package codegen.value;

/**
 * A constant integer value in LLVM, which can be used as an instruction operand.
 */
public class Immediate implements Value {
    /**
     * The constant value for this immediate.
     */
    private final int value;

    public Immediate(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    /**
     * Convert this immediate to a string for use in emitting LLVM.
     * @return The string representation of this immediate's integer value in base 10.
     * @see Integer#toString(int)
     */
    @Override
    public String toString() {
        return Integer.toString(this.getValue());
    }
}
