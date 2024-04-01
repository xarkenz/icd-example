package syntax;

import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import syntax.ast.*;
import token.BasicToken;
import token.Identifier;
import token.Token;
import token.TokenScanner;

import java.util.ArrayList;
import java.util.List;

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
     * Parse an operand, including any prefix/postfix operators applied, into an abstract syntax tree.
     * This is primarily a helper method for {@link #parseExpression()}.
     * @return The root of the AST representing the parsed operand.
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
     * Parse an expression into an abstract syntax tree using recursive Pratt parsing.
     * <p>
     * Precondition: The current token is the first token of the expression to parse.
     * <p>
     * Postcondition: The current token will be the token which terminated the expression (e.g. semicolon).
     * @return The root of the AST representing the parsed expression.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseExpression() throws CompilerError {
        return this.parseExpression(null);
    }

    /**
     * Parse an expression into an abstract syntax tree using recursive Pratt parsing.
     * <p>
     * Precondition: The current token is the first token of the expression to parse.
     * <p>
     * Postcondition: The current token will be the token which terminated the expression (i.e. a non-operator token
     * or an operator with precedence lower than the parent).
     * @param parentPrecedence The precedence of the parent operator, or null if there is none. Operators will then
     *                         be parsed as long as their precedence level is higher than this level.
     * @return The root of the AST representing the parsed expression.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @NotNull ASTNode parseExpression(@Nullable Precedence parentPrecedence) throws CompilerError {
        // Start by parsing an operand, which will become the left subtree of the next operator
        // (unless the method returns early). This variable will remain as the root of the subtree
        ASTNode subtree = this.parseOperand();

        // Keep parsing until reaching a token which is not recognized as an operator,
        // throwing an error if the end of the file is reached, or until the parent operator takes precedence
        Operation operation = Operation.fromToken(this.scanner.expectToken());
        while (operation != null) {
            // Obtain the precedence value from the operation
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

            // Check the next token for an operator, where the loop will break if none is found
            operation = Operation.fromToken(this.scanner.expectToken());
        }

        return subtree;
    }

    /**
     * Parse a statement into an abstract syntax tree, unless the end of the file has been reached.
     * <p>
     * Precondition: The current token is the first token of the statement to parse, which may be null.
     * <p>
     * Postcondition: The current token will be the first token of the next statement.
     * @return The root of the AST representing the parsed statement, or null if the end of the file has been reached.
     * @throws CompilerError Thrown if the token sequence violates syntax rules.
     */
    public @Nullable ASTNode tryParseStatement() throws CompilerError {
        if (this.scanner.getToken() == null) {
            // The end of the file has been reached
            return null;
        }
        else {
            return this.parseStatement();
        }
    }

    /**
     * Parse a statement into an abstract syntax tree.
     * <p>
     * Precondition: The current token is the first token of the statement to parse, and is not null.
     * <p>
     * Postcondition: The current token will be the first token of the next statement.
     * @return The root of the AST representing the parsed statement.
     * @throws CompilerError Thrown if the token sequence violates syntax rules, or if the first token is null.
     */
    public @NotNull ASTNode parseStatement() throws CompilerError {
        Token firstToken = this.scanner.expectToken();

        if (firstToken.equals(BasicToken.CURLY_LEFT)) {
            // Parse a block of statements
            this.scanner.scanToken();
            // Gather statements until reaching the end of the block
            List<ASTNode> statements = new ArrayList<>();
            while (!this.scanner.expectToken().equals(BasicToken.CURLY_RIGHT)) {
                statements.add(this.parseStatement());
            }
            // Skip to the next token to prepare for the next call to this method
            this.scanner.scanToken();

            // The argument to toArray() here, ASTNode[]::new, is needed for Java to properly
            // infer the array type. It's a reference to the constructor for ASTNode[], which
            // is interpreted as a function like "ASTNode[] new(int length)". Why Java's arrays and generics
            // don't get along well (and hence why the argument is necessary), I don't fully understand.
            return new BlockStatementNode(statements.toArray(ASTNode[]::new));
        }
        else if (firstToken.equals(BasicToken.INT)) {
            // Parse a local variable declaration
            this.scanner.scanToken();
            // The next token must be an identifier
            if (!(this.scanner.expectToken() instanceof Identifier identifier)) {
                throw new CompilerError("expected an identifier, got '" + this.scanner.getToken() + "'");
            }
            this.scanner.scanToken();
            // Ensure the declaration ends with a semicolon
            if (!this.scanner.expectToken().equals(BasicToken.SEMICOLON)) {
                throw new CompilerError("expected a semicolon after local variable declaration");
            }
            // Skip to the next token to prepare for the next call to this method
            this.scanner.scanToken();

            return new VariableDeclarationNode(identifier.getName());
        }
        else if (firstToken.equals(BasicToken.PRINT)) {
            // Parse a print statement
            this.scanner.scanToken();
            ASTNode printee = this.parseExpression();
            // Ensure the printee expression ended on a semicolon
            this.scanner.expectTokenFrom(BasicToken.SEMICOLON);
            // Skip to the next token to prepare for the next call to this method
            this.scanner.scanToken();

            return new PrintNode(printee);
        }
        else if (firstToken instanceof Identifier identifier) {
            // Parse an assignment statement (hardcoded syntax for now)
            this.scanner.scanToken();
            // The next token must be an equals sign
            if (!this.scanner.expectToken().equals(BasicToken.EQUAL)) {
                throw new CompilerError("expected an equals sign for assignment");
            }
            this.scanner.scanToken();
            // Parse the right-hand side of the operator
            ASTNode rightHandSide = this.parseExpression();
            // Ensure the expression ended on a semicolon
            this.scanner.expectTokenFrom(BasicToken.SEMICOLON);
            // Skip to the next token to prepare for the next call to this method
            this.scanner.scanToken();

            // This is just an operator, as our eventual goal is to make assignment an expression
            return new OperatorNode(Operation.ASSIGNMENT, new ASTNode[] { identifier, rightHandSide });
        }
        else if (firstToken.equals(BasicToken.IF)) {
            // Parse a conditional statement, including the 'else' block if it exists
            this.scanner.scanToken();
            // The next token must be an opening parenthesis to hold the condition
            this.scanner.expectTokenFrom(BasicToken.PAREN_LEFT);
            // Parse the condition expression
            this.scanner.scanToken();
            ASTNode condition = this.parseExpression();
            // Ensure the condition expression ended on a closing parenthesis
            this.scanner.expectTokenFrom(BasicToken.PAREN_RIGHT);
            this.scanner.scanToken();
            // Parse the consequent statement for the conditional, which is not required to be a block statement
            ASTNode consequent = this.parseStatement();
            // Greedily check for an 'else' block (which is the typical way to deal with the "dangling else problem")
            ASTNode alternative = null;
            if (this.scanner.getToken() != null && this.scanner.getToken().equals(BasicToken.ELSE)) {
                this.scanner.scanToken();
                // Parse the alternative statement, which is also not required to be a block statement
                alternative = this.parseStatement();
            }

            return new ConditionalNode(condition, consequent, alternative);
        }
        else if (firstToken.equals(BasicToken.WHILE)) {
            // Parse a 'while' loop
            this.scanner.scanToken();
            // The next token must be an opening parenthesis to hold the condition
            this.scanner.expectTokenFrom(BasicToken.PAREN_LEFT);
            // Parse the condition expression
            this.scanner.scanToken();
            ASTNode condition = this.parseExpression();
            // Ensure the condition expression ended on a closing parenthesis
            this.scanner.expectTokenFrom(BasicToken.PAREN_RIGHT);
            this.scanner.scanToken();
            // Parse the body statement for this loop, which is not required to be a block statement
            ASTNode loopBody = this.parseStatement();

            return new WhileLoopNode(condition, loopBody);
        }
        else {
            throw new CompilerError("unexpected token '" + firstToken + "'");
        }
    }
}
