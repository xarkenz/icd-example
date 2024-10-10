package codegen.value;

import org.jetbrains.annotations.NotNull;

/**
 * A virtual register in LLVM, which can be used as an instruction result or operand.
 */
public class Register implements Value {
    /**
     * The identifier given to this register, excluding the {@code %} prefix.
     */
    private final @NotNull String identifier;
    /**
     * The width of the integer stored in this register in bits.
     */
    private final int bitCount;
    /**
     * {@code true} if this is a global register starting with {@code @}, or {@code false} if this is
     * a local register starting with {@code %}.
     */
    private final boolean global;

    public Register(@NotNull String identifier, int bitCount, boolean global) {
        this.identifier = identifier;
        this.bitCount = bitCount;
        this.global = global;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public int getBitCount() {
        return this.bitCount;
    }

    public boolean isGlobal() {
        return this.global;
    }

    /**
     * Convert this register to a string for use in emitting LLVM.
     * @return A string of the form {@code @identifier} if global or {@code %identifier} if local.
     */
    @Override
    public String toString() {
        if (this.isGlobal()) {
            return "@" + this.getIdentifier();
        }
        else {
            return "%" + this.getIdentifier();
        }
    }
}
