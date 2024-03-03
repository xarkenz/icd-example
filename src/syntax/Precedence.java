package syntax;

/**
 * The property of an operation dictating the order in which it should be evaluated
 * relative to other operations (hence, its precedence). This property is used in Pratt parsing to generalize
 * the handling of operator precedence.
 * <p>
 * This was constructed by taking
 * the <a href="https://en.cppreference.com/w/c/language/operator_precedence">C operator precedence table</a>
 * and reversing the levels so the highest level corresponds to the most precedence.
 * <p>
 * I was hoping to keep the enum values in the order they are listed on the table linked above,
 * but for whatever reason, the default {@link Comparable} implementation on {@link Enum} is marked as final.
 * Now why would they do that?
 * @see Operation
 */
public enum Precedence {
    COMMA(1),           // ,
    ASSIGNMENT(2),      // =, *=, /=, %=, +=, -=, <<=, >>=, &=, ^=, |=
    CONDITIONAL(3),     // ? :
    LOGICAL_OR(4),      // ||
    LOGICAL_AND(5),     // &&
    BITWISE_OR(6),      // |
    BITWISE_XOR(7),     // ^
    BITWISE_AND(8),     // &
    EQUALITY(9),        // ==, !=
    INEQUALITY(10),     // <, >, <=, >=
    BIT_SHIFT(11),      // <<, >>
    ADDITIVE(12),       // +, -
    MULTIPLICATIVE(13), // *, /, %
    PREFIX(14),         // +_, -_, !_, ~_, &_, *_, ++_, --_, cast, sizeof
    POSTFIX(15);        // ., ->, _++, _--, _(), _[]

    /**
     * The integer corresponding to this precedence level.
     */
    private final int level;

    Precedence(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}
