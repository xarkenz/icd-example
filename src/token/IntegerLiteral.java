package token;

public record IntegerLiteral(int value) implements Token {
    @Override
    public String toString() {
        return "(integer " + this.value() + ")";
    }
}
