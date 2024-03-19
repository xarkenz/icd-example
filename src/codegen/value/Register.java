package codegen.value;

import org.jetbrains.annotations.NotNull;

/**
 * A virtual register in LLVM, which can be used as an instruction result or operand.
 */
public class Register implements Value {
    /**
     * The identifier given to this register, excluding the prefix.
     */
    private final @NotNull String identifier;
    /**
     * The width of the integer stored in this register in bits.
     */
    private final int bitCount;

    public Register(@NotNull String identifier, int bitCount) {
        this.identifier = identifier;
        this.bitCount = bitCount;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getBitCount() {
        return this.bitCount;
    }

    /**
     * Convert this register to a string for use in emitting LLVM.
     * @return A string of the form {@code %identifier}.
     */
    @Override
    public String toString() {
        return "%" + this.getIdentifier();
    }
}
