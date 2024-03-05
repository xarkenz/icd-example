package codegen;

import org.jetbrains.annotations.NotNull;

public class Register {
    private final @NotNull String identifier;

    public Register(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return "%" + this.identifier;
    }
}
