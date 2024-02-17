package token;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class TokenScanner {
    private final @NotNull Reader reader;
    private final @NotNull Stack<Character> putBacks;
    private @Nullable Token token;

    public TokenScanner(@NotNull Reader reader) {
        this.reader = reader;
        this.putBacks = new Stack<>();
        this.token = null;
    }

    public @Nullable Token getToken() {
        return this.token;
    }

    public @Nullable Token scanToken() throws CompilerError {
        Character firstChar = this.nextNonSpaceChar();

        if (firstChar == null) {
            this.token = null;
        }
        else if (Character.isDigit(firstChar)) {
            this.putBack(firstChar);
            this.token = this.scanIntegerLiteral();
        }
        else {
            this.putBack(firstChar);
            this.token = this.scanOperatorToken();
        }

        return this.token;
    }

    private @Nullable Character nextChar() throws CompilerError {
        if (this.putBacks.isEmpty()) {
            try {
                int read = this.reader.read();

                if (read < 0) {
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
            return this.putBacks.pop();
        }
    }

    private void putBack(@Nullable Character toPutBack) {
        this.putBacks.push(toPutBack);
    }

    private @Nullable Character nextNonSpaceChar() throws CompilerError {
        Character readChar = this.nextChar();

        while (readChar != null && Character.isSpaceChar(readChar)) {
            readChar = this.nextChar();
        }

        return readChar;
    }

    private @NotNull Token scanIntegerLiteral() throws CompilerError {
        int value = 0;
        Character readChar = this.nextChar();

        while (readChar != null && '0' <= readChar && readChar <= '9') {
            int digit = readChar - '0';
            value = value * 10 + digit;

            readChar = this.nextChar();
        }

        this.putBack(readChar);

        return new IntegerLiteral(value);
    }

    private @NotNull Token scanOperatorToken() throws CompilerError {
        Character readChar = this.nextChar();
        Objects.requireNonNull(readChar);

        List<BasicToken> operatorTokenMatches = BasicToken.findOperatorToken(readChar);

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
