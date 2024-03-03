package syntax;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import token.Token;
import token.TokenScanner;

/**
 * Class which parses a sequence of scanned tokens into an abstract syntax tree.
 * For binary expressions, a recursive implementation of Pratt parsing is used
 * based on the C operator precedence table.
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
     * Parse an expression into an {@link ASTNode} using recursive Pratt parsing.
     * @return The parsed expression via the root of the AST.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseExpression() throws CompilerError {
        return this.parseExpression(null);
    }

    /**
     * Parse an expression into an {@link ASTNode} using recursive Pratt parsing.
     * @param parentPrecedence The precedence of the parent operator, or null if there is none. Operators will then
     * be parsed as long as their precedence level is higher than this level.
     * @return The parsed expression via the root of the AST.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseExpression(@Nullable Precedence parentPrecedence) throws CompilerError {
        // Start by parsing an operand, which will become the left subtree of the next operator
        // (unless the method returns early). This variable will remain as the root of the subtree
        ASTNode subtree = this.parseOperand();

        while (this.scanner.getToken() != null) {
            // Check the current token and gather information about it
            Operation operation = Operation.fromToken(this.scanner.getToken());
            if (operation == null) {
                throw new CompilerError("expected an operator, got '" + this.scanner.getToken() + "'");
            }
            Precedence currentPrecedence = operation.getPrecedence();

            // This condition is crucial for respecting operator precedence. If the parent takes precedence,
            // it must be made into a subtree before this operator is incorporated
            if (parentPrecedence != null && parentPrecedence.compareTo(currentPrecedence) >= 0) {
                break;
            }

            // Recursively parse another expression to use as the right child of the operator
            this.scanner.scanToken();
            ASTNode rightHandSide = this.parseExpression(currentPrecedence);

            // Finally, create the operator node using left-to-right associativity
            subtree = new OperatorNode(operation, new ASTNode[] { subtree, rightHandSide });
        }

        return subtree;
    }
}
