package codegen;

import org.jetbrains.annotations.NotNull;

/**
 * Representation of a virtual register in LLVM.
 */
public class Register {
    /**
     * The identifier given to this register, excluding the prefix.
     */
    private final @NotNull String identifier;

    public Register(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    /**
     * Convert this register to a string for use in emitting LLVM.
     * @return A string of the form {@code %identifier}.
     */
    @Override
    public String toString() {
        return "%" + this.identifier;
    }
}
