package token;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
     * Get the last token which was scanned, ensuring it is not null.
     * @return The last token which was scanned.
     * @throws CompilerError Thrown if the end of the file was reached, in which case the token would be null.
     */
    public @NotNull Token expectToken() throws CompilerError {
        if (this.token == null) {
            throw new CompilerError("unexpected end of file");
        }
        else {
            return this.token;
        }
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
        else if (Character.isLetter(firstChar) || firstChar == '_') {
            // Scan an identifier or keyword (which can contain digits after the first character)
            this.putBack(firstChar);
            this.token = this.scanIdentifierOrKeyword();
        }
        else {
            if (firstChar == '/') {
                Character secondChar = this.nextChar();
                if (secondChar != null && secondChar == '/') {
                    this.skipLineComment();
                    return this.scanToken();
                }
                this.putBack(secondChar);
            }

            // Scan an operator token or separator token
            this.putBack(firstChar);
            this.token = this.scanOperatorOrSeparator();
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

        // Skip characters until reaching null or a non-whitespace character
        while (readChar != null && Character.isWhitespace(readChar)) {
            readChar = this.nextChar();
        }

        // Return the character (or null) which caused the loop to end
        return readChar;
    }

    /**
     * Read characters from input until encountering a newline or the end of the file.
     * @throws CompilerError Thrown if {@link #nextChar()} throws an error at any point.
     */
    private void skipLineComment() throws CompilerError {
        Character readChar = this.nextChar();

        // Skip characters until reaching null or a newline character
        while (readChar != null && readChar != '\n') {
            readChar = this.nextChar();
        }
    }

    /**
     * Scan an integer literal token from input.
     * <p>
     * Precondition: The next call to {@link #nextChar()} must not return null.
     * @return The token representing the scanned integer literal.
     * @throws CompilerError Thrown if {@link #nextChar()} throws an error at any point.
     */
    private @NotNull Token scanIntegerLiteral() throws CompilerError {
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);

        int value = 0;
        // Integer literals can only consist of digits, so stop at the first non-digit character
        while (readChar != null && '0' <= readChar && readChar <= '9') {
            // Typical base 10 integer conversion, shift left by 1 place value and add the new digit.
            // "Shift left by 1 place value" as in multiplying by 10 because it's base 10
            int digit = readChar - '0';
            value = value * 10 + digit;

            readChar = this.nextChar();
        }

        // Since the last character read was either null or potentially part of another token, unread it
        this.putBack(readChar);

        return new IntegerLiteral(value);
    }

    /**
     * Scan an identifier or keyword token from input.
     * <p>
     * Precondition: The next call to {@link #nextChar()} must not return null.
     * @return The token representing the scanned identifier or keyword.
     * @throws CompilerError Thrown if {@link #nextChar()} throws an error at any point.
     */
    private @NotNull Token scanIdentifierOrKeyword() throws CompilerError {
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);

        StringBuilder wordBuilder = new StringBuilder();
        // By this point, the first character read should not be a digit, so we can just stop at
        // the first character that is not alphanumeric or an underscore
        while (readChar != null && (Character.isLetterOrDigit(readChar) || readChar == '_')) {
            // This character will be part of the word, so append it to the existing content.
            // Calling Character.charValue() here is probably not necessary,
            // but it forces the StringBuilder.append(char) overload to be picked (I love overloaded methods...)
            wordBuilder.append(readChar.charValue());

            readChar = this.nextChar();
        }

        // Since the last character read was either null or potentially part of another token, unread it
        this.putBack(readChar);

        // Determine whether the scanned word matches a keyword
        String word = wordBuilder.toString();
        BasicToken keyword = BasicToken.findExactMatch(word);

        if (keyword != null) {
            // A matching keyword was found, so return the basic token representing it
            return keyword;
        }
        else {
            // The word is not a keyword, so treat it as an identifier
            return new Identifier(word);
        }
    }

    /**
     * Scan an operator/separator token from input using a "maximal munch" approach.
     * <p>
     * What "maximal munch" means here is that the longest possible matching token is scanned.
     * for example, given the input "===", the first token "munched" would be "==" rather than "=",
     * as the former is a longer match. The next token "munched" would then be "=".
     * <p>
     * Precondition: The next call to {@link #nextChar()} must not return null.
     * @return The token representing the scanned operator/separator.
     * @throws CompilerError Thrown if no match was found, or if {@link #nextChar()} throws an error at any point.
     */
    private @NotNull Token scanOperatorOrSeparator() throws CompilerError {
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);
        // Save this for a potential error later on
        char firstChar = readChar;

        // Firstly, "munch" characters as long as they could possibly contribute to forming a token
        StringBuilder munch = new StringBuilder();
        while (readChar != null) {
            // This character will be part of the word, so append it to the existing content.
            // Calling Character.charValue() here is probably not necessary,
            // but it forces the StringBuilder.append(char) overload to be picked (I love overloaded methods...)
            munch.append(readChar.charValue());

            // Get the list of possible basic tokens based on the content we have read so far
            List<BasicToken> partialMatches = BasicToken.findPartialMatches(munch.toString());

            if (partialMatches.isEmpty()) {
                // The character we just read cannot possibly be part of the token
                // Unread the offending character and stop reading new characters
                this.putBack(readChar);
                munch.deleteCharAt(munch.length() - 1);
                break;
            }

            readChar = this.nextChar();
        }

        // Then, determine the exact token which was "munched"
        BasicToken matchedToken = BasicToken.findExactMatch(munch.toString());
        // Backtrack if needed until the content forms a valid token. This loop is usually skipped,
        // but would be handy if, for example, we had tokens for "." and "..." but not "..".
        // In that case, for the input "..=", the munch here would be "..", and we would have to
        // backtrack to ".". I don't think this can actually happen right now, but it's nice for generalization.
        while (matchedToken == null && !munch.isEmpty()) {
            // Unread one character
            char unreadChar = munch.charAt(munch.length() - 1);
            this.putBack(unreadChar);
            munch.deleteCharAt(munch.length() - 1);
            // Try matching a token again
            matchedToken = BasicToken.findExactMatch(munch.toString());
        }

        if (matchedToken != null) {
            // A matching token was found, so return the basic token representing it
            return matchedToken;
        } else {
            // The sequence did not form any token, so report the first character as invalid
            throw new CompilerError("unexpected character: " + firstChar + " (" + (int) firstChar + ")");
        }
    }
}
