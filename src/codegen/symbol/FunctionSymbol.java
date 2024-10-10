package codegen.symbol;

import codegen.value.Register;
import org.jetbrains.annotations.NotNull;

/**
 * An entry in the symbol table representing a function.
 * @see SymbolTable
 */
public class FunctionSymbol extends Symbol {
    /**
     * The number of parameters this function requires.
     */
    private final int parameterCount;

    public FunctionSymbol(@NotNull String name, @NotNull Register register, int parameterCount) {
        super(name, register);
        this.parameterCount = parameterCount;
    }

    public int getParameterCount() {
        return this.parameterCount;
    }
}
