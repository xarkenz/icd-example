package cli;

import error.CompilerError;
import syntax.ASTNode;
import syntax.OperatorNode;
import token.IntegerLiteral;

public class Interpreter {
    public int interpretNode(ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            return literal.getValue();
        }
        else if (node instanceof OperatorNode operator) {
            int leftValue = this.interpretNode(operator.getOperands()[0]);
            int rightValue = this.interpretNode(operator.getOperands()[1]);

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
