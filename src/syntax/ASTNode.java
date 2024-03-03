package syntax;

/**
 * Interface representing a node in an AST (abstract syntax tree).
 * This allows nodes to be more specialized in the data they contain.
 * <p>
 * An abstract syntax tree, as the name suggests, is a tree representation of the program's syntax.
 * It is "abstract" in the sense that nodes are (usually) not tied directly to tokens. They are designed
 * to represent the syntax in a more general way, focusing on expressions and statements.
 * @see Parser
 */
public interface ASTNode {
}
