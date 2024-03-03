package cli;

import error.CompilerError;
import syntax.ASTNode;
import syntax.OperatorNode;
import token.IntegerLiteral;

/**
 * Temporary miniature interpreter for evaluating an AST.
 * This will be replaced by the LLVM-IR generator once it is implemented.
 */
public class Interpreter {
    /**
     * Recursively evaluate an AST using a postorder traversal.
     * <p>
     * While this interpreter is only temporary, the postorder traversal used in this method
     * lays the foundation for the approach we will use to generate LLVM-IR in the next step.
     * @param node The subtree of the AST to evaluate.
     * @return The calculated result of the subtree.
     * @throws CompilerError Thrown if any unrecognized AST nodes are encountered (which should not happen).
     */
    public int interpretNode(ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            return literal.getValue();
        }
        else if (node instanceof OperatorNode operator) {
            // Evaluate the left-hand side and right-hand side
            int leftValue = this.interpretNode(operator.getOperands()[0]);
            int rightValue = this.interpretNode(operator.getOperands()[1]);

            // Apply the corresponding operation to the interpreted values
            return switch (operator.getOperation()) {
                case ADDITION -> leftValue + rightValue;
                case SUBTRACTION -> leftValue - rightValue;
                case MULTIPLICATION -> leftValue * rightValue;
                case DIVISION -> leftValue / rightValue;
            };
        }
        else {
            throw new CompilerError("unrecognized AST node type");
        }
    }
}
