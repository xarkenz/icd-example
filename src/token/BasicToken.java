package token;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of "basic" tokens, i.e. tokens with only one representation.
 * This includes:
 *   - Operator tokens, such as '+' and '=='
 *   - Separator tokens, such as ',' and ')'
 *   - Keyword tokens, such as 'if' and 'else'
 */
public enum BasicToken implements Token {
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/");

    /**
     * The only representation this basic token can have, in string form.
     */
    private final String content;

    BasicToken(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    /**
     * Find all matching basic tokens based on the first character.
     * @param firstChar: The character which all returned tokens must start with.
     * @return A List of all matching tokens.
     */
    public static List<BasicToken> findPartialMatches(char firstChar) {
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().charAt(0) == firstChar)
            .toList();
    }

    @Override
    public String toString() {
        return "(basic " + this.getContent() + ")";
    }
}
