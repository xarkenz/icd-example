package token;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    // Operators
    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/"),
    EQUAL("="),
    DOUBLE_EQUAL("=="),
    BANG_EQUAL("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    // Separators
    SEMICOLON(";"),
    // Keywords
    INT("int"),
    PRINT("print");

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
     * Find all basic tokens starting with a given prefix.
     * @param prefix The prefix which all returned tokens must start with.
     * @return A {@link List} of all matching tokens.
     */
    public static @NotNull List<BasicToken> findPartialMatches(String prefix) {
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().startsWith(prefix))
            .toList();
    }

    /**
     * Find the basic token exactly matching the given content, if one exists.
     * @param content The content for the token to match.
     * @return The basic token exactly matching the given content, if one exists, or null otherwise.
     */
    public static @Nullable BasicToken findExactMatch(String content) {
        // Coming from Rust, my intuition was that Stream.findFirst() would accept a predicate,
        // but it seems to just return the first item in the stream...
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().equals(content))
            .findFirst()
            // Turn the Optional<BasicToken> into a @Nullable BasicToken
            .orElse(null);
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
