package syntax;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import token.Token;
import token.TokenScanner;

public class Parser {
    private final @NotNull TokenScanner scanner;

    public Parser(@NotNull TokenScanner scanner) throws CompilerError {
        // Parsing correctly requires the scanner to have a valid current token, so scan the first token
        scanner.scanToken();
        this.scanner = scanner;
    }

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

    public @NotNull ASTNode parseExpression() throws CompilerError {
        return this.parseExpression(null);
    }

    public @NotNull ASTNode parseExpression(@Nullable Precedence parentPrecedence) throws CompilerError {
        ASTNode subtree = this.parseOperand();

        while (this.scanner.getToken() != null) {
            Operation operation = Operation.fromToken(this.scanner.getToken());
            if (operation == null) {
                throw new CompilerError("expected an operator, got '" + this.scanner.getToken() + "'");
            }
            Precedence currentPrecedence = operation.getPrecedence();

            if (parentPrecedence != null && parentPrecedence.compareTo(currentPrecedence) >= 0) {
                break;
            }

            this.scanner.scanToken();
            ASTNode rightHandSide = this.parseExpression(currentPrecedence);

            subtree = new OperatorNode(operation, new ASTNode[] { subtree, rightHandSide });
        }

        return subtree;
    }
}
