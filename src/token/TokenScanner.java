package token;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Class which scans characters from a {@link Reader} into a sequence of tokens.
 * @see Reader
 */
public class TokenScanner {
    /**
     * The source of characters for this {@link TokenScanner}.
     */
    private final @NotNull Reader reader;
    /**
     * The stack used to "unread" or "put back" characters.
     */
    private final @NotNull Stack<Character> putBacks;
    /**
     * The last token which was scanned by a call to {@link #scanToken()}.
     * This is null if {@link #scanToken()} has never been called on this instance,
     * or if the end of the file has been reached.
     */
    private @Nullable Token token;

    /**
     * Construct a new scanner from an existing {@link Reader}.
     * <p>
     * Postcondition: {@link #getToken()} will return null until {@link #scanToken()} is called for the first time.
     * @param reader The source of characters for this {@link TokenScanner}.
     */
    public TokenScanner(@NotNull Reader reader) {
        this.reader = reader;
        this.putBacks = new Stack<>();
        this.token = null;
    }

    /**
     * Get the last token which was scanned by a call to {@link #scanToken()}.
     * This is null if {@link #scanToken()} has never been called on this instance,
     * or if the end of the file has been reached.
     */
    public @Nullable Token getToken() {
        return this.token;
    }

    /**
     * Scan the next token from input.
     * @return The token which was just scanned, or null if the end of the file was reached.
     * (This token can also be accessed calling {@link #getToken()} afterward.)
     * @throws CompilerError Thrown if either an {@link IOException} is thrown while reading characters,
     * or if a malformed token is scanned.
     */
    public @Nullable Token scanToken() throws CompilerError {
        Character firstChar = this.nextNonSpaceChar();

        if (firstChar == null) {
            // The end of the file has been reached
            this.token = null;
        }
        else if (Character.isDigit(firstChar)) {
            // Scan an integer literal
            this.putBack(firstChar);
            this.token = this.scanIntegerLiteral();
        }
        else {
            // Scan an operator token or separator token
            this.putBack(firstChar);
            this.token = this.scanOperatorToken();
        }

        // Return the just scanned token for convenience
        return this.token;
    }

    /**
     * Read the next character from input.
     * @return The next character to use for constructing tokens, or null if the end of the file was reached.
     * @throws CompilerError Thrown if an {@link IOException} is thrown from {@link Reader#read()}.
     */
    private @Nullable Character nextChar() throws CompilerError {
        if (this.putBacks.isEmpty()) {
            try {
                // Read a single character from the Reader (throws IOException)
                int read = this.reader.read();

                if (read < 0) {
                    // End of file has been reached
                    return null;
                }
                else {
                    return (char) read;
                }
            }
            catch (IOException cause) {
                throw new CompilerError("error while reading source file", cause);
            }
        }
        else {
            // Since at least one character has been "unread", pop from the stack to "reread" it
            return this.putBacks.pop();
        }
    }

    /**
     * Put one character back into the input, essentially "unreading" it.
     * The character is put onto a stack which will be popped from the next time {@link #nextChar()} is called.
     * @param toPutBack The last character returned by {@link #nextChar()} which has not been put back already.
     */
    private void putBack(@Nullable Character toPutBack) {
        this.putBacks.push(toPutBack);
    }

    /**
     * Read characters from input until encountering one which is not whitespace.
     * @return The first non-whitespace character encountered, or null if the end of the file was reached.
     * @throws CompilerError Thrown if {@link #nextChar()} throws an error at any point.
     */
    private @Nullable Character nextNonSpaceChar() throws CompilerError {
        Character readChar = this.nextChar();

        while (readChar != null && Character.isSpaceChar(readChar)) {
            readChar = this.nextChar();
        }

        return readChar;
    }

    /**
     * Scan an integer literal token from input.
     * <p>
     * Precondition: The next call to {@link #nextChar()} must not return null.
     * @return The token representing the scanned integer literal.
     * @throws CompilerError Thrown if {@link #nextChar()} throws an error at any point.
     */
    private @NotNull Token scanIntegerLiteral() throws CompilerError {
        int value = 0;
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);

        while (readChar != null && '0' <= readChar && readChar <= '9') {
            int digit = readChar - '0';
            value = value * 10 + digit;

            readChar = this.nextChar();
        }

        this.putBack(readChar);

        return new IntegerLiteral(value);
    }

    /**
     * Scan an operator/separator token from input.
     * <p>
     * Precondition: The next call to {@link #nextChar()} must not return null.
     * @return The token representing the scanned operator/separator.
     * @throws CompilerError Thrown if no clear match was found, or if {@link #nextChar()} throws an error at any point.
     */
    private @NotNull Token scanOperatorToken() throws CompilerError {
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);

        // Get all basic tokens matching the first character read
        List<BasicToken> operatorTokenMatches = BasicToken.findPartialMatches(readChar);

        // TODO: dealing with tokens longer than 1 character
        if (operatorTokenMatches.isEmpty()) {
            throw new CompilerError("unexpected character: " + readChar);
        }
        else if (operatorTokenMatches.size() == 1) {
            return operatorTokenMatches.get(0);
        }
        else {
            throw new CompilerError("conflicting token matches");
        }
    }
}
