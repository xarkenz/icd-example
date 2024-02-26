package error;

/**
 * Custom exception type for all errors relating to the compiler.
 */
public class CompilerError extends Exception {
    public CompilerError(String message) {
        super(message);
    }

    public CompilerError(String message, Throwable cause) {
        super(message, cause);
    }
}
