package codegen.value;

/**
 * A constant boolean (1-bit integer) value in LLVM, which can be used as an instruction operand.
 */
public class ImmediateBool implements Value {
    /**
     * The constant value for this immediate.
     */
    private final boolean value;

    public ImmediateBool(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public int getBitCount() {
        return 1;
    }

    /**
     * Convert this immediate to a string for use in emitting LLVM.
     * @return The string "true" if the immediate value is {@code true}, or "false" otherwise.
     * @see Boolean#toString(boolean)
     */
    @Override
    public String toString() {
        return Boolean.toString(this.getValue());
    }
}
