package token;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Enumeration of "basic" tokens, i.e. tokens with only one representation.
 * This includes:
 * <ul>
 *   <li>Operator tokens, such as {@code +} and {@code ==}</li>
 *   <li>Separator tokens, such as {@code ,} and {@code )}</li>
 *   <li>Keyword tokens, such as {@code if} and {@code else}</li>
 * </ul>
 */
public enum BasicToken implements Token {
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/");

    /**
     * The only representation this basic token can have, in string form.
     */
    private final @NotNull String content;

    BasicToken(@NotNull String content) {
        this.content = content;
    }

    public @NotNull String getContent() {
        return this.content;
    }

    /**
     * Find all matching basic tokens based on the first character.
     * @param firstChar The character which all returned tokens must start with.
     * @return A {@link List} of all matching tokens.
     */
    public static List<BasicToken> findPartialMatches(char firstChar) {
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().charAt(0) == firstChar)
            .toList();
    }

    /**
     * Get the string representation of this basic token.
     * Equivalent to calling {@link #getContent()}.
     * @return The only representation this basic token can have, in string form.
     */
    @Override
    public String toString() {
        return this.getContent();
    }
}
