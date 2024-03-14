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
     * Find all matching basic tokens based on the first character.
     * @param firstChar The character which all returned tokens must start with.
     * @return A {@link List} of all matching tokens.
     */
    public static @NotNull List<BasicToken> findPartialMatches(char firstChar) {
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().charAt(0) == firstChar)
            .toList();
    }

    /**
     * Find the basic token exactly matching the given content, if one exists.
     * <p>
     * I didn't really want to use {@link Optional} for this because it can be awkward to work with,
     * but that's what {@link java.util.stream.Stream#findFirst()} returns, so here we are.
     * @param content The content for the token to match.
     * @return The basic token exactly matching the given content, if one exists.
     */
    public static @NotNull Optional<BasicToken> findExactMatch(String content) {
        // Coming from Rust, my intuition was that Stream.findFirst() would accept a predicate,
        // but it seems to just return the first item in the stream...
        return Arrays.stream(BasicToken.values())
            .filter(token -> token.getContent().equals(content))
            .findFirst();
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
