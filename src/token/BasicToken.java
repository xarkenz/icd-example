package token;

import java.util.Arrays;
import java.util.List;

public enum BasicToken implements Token {
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/");

    private final String content;

    BasicToken(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public static List<BasicToken> findOperatorToken(char firstChar) {
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().charAt(0) == firstChar)
            .toList();
    }

    @Override
    public String toString() {
        return "(basic " + this.getContent() + ")";
    }
}
