package syntax;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import token.Token;
import token.TokenScanner;

/**
 * Class which parses a sequence of scanned tokens into an abstract syntax tree.
 * For binary expressions, a simple recursive parsing method is used to enforce operator precedence,
 * though it incorrectly uses right-to-left associativity. This issue will be addressed later.
 * @see TokenScanner
 * @see ASTNode
 */
public class Parser {
    /**
     * The source of tokens used to parse syntax.
     */
    private final @NotNull TokenScanner scanner;

    /**
     * Construct a new parser from an existing {@link TokenScanner}.
     * Precondition: The next token scanned will be the first token of the source program.
     * @param scanner The source of tokens to be used by this {@link Parser}.
     * @throws CompilerError Thrown if the scanner throws an error while scanning the first token.
     */
    public Parser(@NotNull TokenScanner scanner) throws CompilerError {
        // Parsing correctly requires the scanner to have a valid current token, so scan the first token
        scanner.scanToken();
        this.scanner = scanner;
    }

    /**
     * Parse an operand, including any prefix/postfix operators applied, into an {@link ASTNode}.
     * This is primarily a helper method for {@link #parseExpression()}.
     * @return The root of the subtree representing the parsed operand.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseOperand() throws CompilerError {
        Token token = this.scanner.expectToken();

        if (token instanceof ASTNode node) {
            // The token also serves as an AST node, so it can simply become a leaf of the tree
            this.scanner.scanToken();
            return node;
        }
        else {
            throw new CompilerError("expected an operand, got '" + token + "'");
        }
    }

    /**
     * Parse a multiplicative expression into an {@link ASTNode} using basic recursive parsing.
     * A multiplicative expression may be an operand, a multiplication operation, or a division operation.
     * @return The parsed expression via the root of the AST.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseMultiplicativeExpression() throws CompilerError {
        // Start by parsing an operand, which will become the left subtree of the next operator
        // (unless the method returns early)
        ASTNode leftHandSide = this.parseOperand();

        if (this.scanner.getToken() == null) {
            // We have reached the end of the file, so there is no operator or right-hand side
            return leftHandSide;
        }
        else {
            // Ensure the current token is an operator, and obtain the operation type
            Operation operation = Operation.fromToken(this.scanner.getToken());
            if (operation == null) {
                throw new CompilerError("expected an operator, got '" + this.scanner.getToken() + "'");
            }
            // Ensure the current token is a multiplicative operator so we are following operator precedence
            if (operation != Operation.MULTIPLICATION && operation != Operation.DIVISION) {
                return leftHandSide;
            }

            // Recursively parse another multiplicative expression to use as the right child of the operator.
            // (This enforces right-to-left associativity, which is not correct for arithmetic operators,
            // but that will be addressed with Pratt parsing later)
            this.scanner.scanToken();
            ASTNode rightHandSide = this.parseMultiplicativeExpression();

            // Finally, create the operator node
            return new OperatorNode(operation, new ASTNode[] { leftHandSide, rightHandSide });
        }
    }

    /**
     * Parse an additive expression into an {@link ASTNode} using basic recursive parsing.
     * An additive expression may be a multiplicative expression, an addition operation, or a subtraction operation.
     * @return The parsed expression via the root of the AST.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseAdditiveExpression() throws CompilerError {
        // Start by parsing a multiplicative expression, which will become the left subtree of the next operator
        // (unless the method returns early)
        ASTNode leftHandSide = this.parseMultiplicativeExpression();

        if (this.scanner.getToken() == null) {
            // We have reached the end of the file, so there is no operator or right-hand side
            return leftHandSide;
        }
        else {
            // Ensure the current token is an operator, and obtain the operation type.
            Operation operation = Operation.fromToken(this.scanner.getToken());
            if (operation == null) {
                throw new CompilerError("expected an operator, got '" + this.scanner.getToken() + "'");
            }
            // Ensure the current token is a multiplicative operator so we are following operator precedence
            if (operation != Operation.ADDITION && operation != Operation.SUBTRACTION) {
                return leftHandSide;
            }

            // Recursively parse another additive expression to use as the right child of the operator.
            // (This enforces right-to-left associativity, which is not correct for arithmetic operators,
            // but that will be addressed with Pratt parsing later)
            this.scanner.scanToken();
            ASTNode rightHandSide = this.parseAdditiveExpression();

            // Finally, create the operator node
            return new OperatorNode(operation, new ASTNode[] { leftHandSide, rightHandSide });
        }
    }

    /**
     * Parse an expression into an {@link ASTNode} using basic recursive parsing.
     * This is equivalent to calling {@link #parseAdditiveExpression()} due to it being the lowest precedence level.
     * @return The parsed expression via the root of the AST.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseExpression() throws CompilerError {
        // Start recursion at the lowest precedence level
        return this.parseAdditiveExpression();
    }
}
