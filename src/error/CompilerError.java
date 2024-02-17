package error;

public class CompilerError extends Exception {
    public CompilerError(String message) {
        super(message);
    }

    public CompilerError(String message, Throwable cause) {
        super(message, cause);
    }
}
