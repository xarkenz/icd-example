package codegen;

import org.jetbrains.annotations.NotNull;

/**
 * A label for a basic block in LLVM, which is not treated as a {@link codegen.value.Value}
 * for our purposes, considering that it usually is not interchangeable with any other kind of value.
 * However, they will still appear as though they are virtual registers in the emitted LLVM.
 */
public class Label {
    /**
     * The identifier given to this label, excluding the {@code %} prefix.
     */
    private final @NotNull String identifier;

    public Label(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    /**
     * Convert this label to a string for use in emitting LLVM.
     * @return A string of the form {@code %identifier}.
     */
    @Override
    public String toString() {
        return "%" + this.getIdentifier();
    }
}
