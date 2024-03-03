package error;

import org.jetbrains.annotations.NotNull;

/**
 * Error thrown by the compiler indicating a problem with the source program.
 */
public class CompilerError extends Exception {
    /**
     * Construct a compiler error with a message.
     * @param message The message to be displayed when the error is thrown.
     * @see Exception#Exception(String)
     */
    public CompilerError(@NotNull String message) {
        super(message);
    }

    /**
     * Construct a compiler error with a message, as well as the underlying cause.
     * @param message The message to be displayed when the error is thrown.
     * @param cause The underlying cause of this error.
     * @see Exception#Exception(String, Throwable)
     */
    public CompilerError(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
