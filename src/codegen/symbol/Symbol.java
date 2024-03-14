package codegen.symbol;

import codegen.value.Register;
import org.jetbrains.annotations.NotNull;

/**
 * An entry in the symbol table which has a name and a value in the form of a register.
 * For local variables, this value represents the pointer to the stack space it uses.
 * @see SymbolTable
 */
public class Symbol {
    /**
     * The name used as the key for this symbol in the symbol table.
     */
    private final @NotNull String name;
    /**
     * The value of this symbol, which an identifier matching this symbol evaluates to.
     */
    private final @NotNull Register register;

    public Symbol(@NotNull String name, @NotNull Register register) {
        this.name = name;
        this.register = register;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull Register getRegister() {
        return this.register;
    }
}
