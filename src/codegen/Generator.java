package codegen;

import codegen.symbol.FunctionSymbol;
import codegen.symbol.Symbol;
import codegen.symbol.SymbolTable;
import codegen.value.ImmediateInt32;
import codegen.value.Register;
import codegen.value.Value;
import error.CompilerError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import syntax.Operation;
import syntax.Parser;
import syntax.ast.*;
import token.Identifier;
import token.IntegerLiteral;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class which takes an AST and handles semantic analysis and code generation.
 * The use of this class as an instance is not allowed outside of this class;
 * instead, external code must call the static method {@link #generate(Writer, Parser, String, boolean)}.
 * @see Emitter
 */
public class Generator {
    /**
     * Used to generate the syntax for LLVM instructions and write them to the output.
     */
    private final @NotNull Emitter emitter;
    /**
     * The symbol table used to store all symbols local to the current function during generation.
     * This is cleared each time a function finishes generating.
     */
    private final @NotNull SymbolTable localSymbolTable;
    /**
     * The symbol table used to store all global symbols during generation.
     */
    private final @NotNull SymbolTable globalSymbolTable;
    /**
     * Numeric identifier for the virtual register {@link #createRegister(int)} returns the next time it is called.
     * Initial value is 0. (This was previously 1, but since we are now explicitly declaring the label
     * for the first basic block in the function, the implicit "{@code 0:}" label no longer exists.)
     */
    private int nextRegisterNumber;
    /**
     * Numeric suffix for the label {@link #createLabel()} returns the next time it is called.
     * Initial value is 0.
     */
    private int nextLabelNumber;

    private Generator(@NotNull Emitter emitter) {
        this.emitter = emitter;
        this.localSymbolTable = new SymbolTable();
        this.globalSymbolTable = new SymbolTable();
        this.nextRegisterNumber = 0;
        this.nextLabelNumber = 0;
    }

    /**
     * Create a new virtual register with a numeric identifier. These identifiers are treated specially
     * by LLVM in that they are required to count up from 0 in the order that the registers are defined.
     * @param bitCount The number of bits in the integer this register will store.
     * @return A new virtual register identified with the next unused register number.
     */
    private @NotNull Register createRegister(int bitCount) {
        String identifier = Integer.toString(this.nextRegisterNumber++);
        return new Register(identifier, bitCount, false);
    }

    /**
     * Create a new basic block label with a unique identifier. Unlike the identifiers produced by
     * {@link #createRegister(int)}, these identifiers use the format {@code .block.N}, where {@code N}
     * is the numeric suffix assigned to the label to distinguish it, counting up from 0.
     * <p>
     * The reason for using a special format for labels instead of simply using numbers is that
     * the generator needs to use identifiers for labels before they are defined, making it
     * impractical to figure out where in the register number ordering the labels will fall.
     * @return A new label identified with the format above, suffixed with the next unused numeric suffix.
     */
    private @NotNull Label createLabel() {
        String suffix = Integer.toString(this.nextLabelNumber++);
        return new Label(".block." + suffix);
    }

    /**
     * Get a symbol from the local symbol table by name, throwing an exception if it does not exist.
     * @param name The name to search for in the local symbol table.
     * @return The relevant symbol in the local symbol table with the given name.
     * @throws CompilerError Thrown if the given name does not correspond to a symbol in the local symbol table.
     */
    private @NotNull Symbol getLocalSymbol(@NotNull String name) throws CompilerError {
        Symbol symbol = this.localSymbolTable.find(name);

        if (symbol == null) {
            throw new CompilerError("undefined local symbol '" + name + "'");
        }
        else {
            return symbol;
        }
    }

    /**
     * Get a function symbol from the global symbol table by name, throwing an exception if it does not exist.
     * @param name The name to search for in the global symbol table.
     * @return The relevant function symbol in the global symbol table with the given name.
     * @throws CompilerError Thrown if the given name does not correspond to a symbol in the global symbol table,
     *     or if the symbol it corresponds to is not a function symbol.
     */
    private @NotNull FunctionSymbol getGlobalFunctionSymbol(@NotNull String name) throws CompilerError {
        Symbol symbol = this.globalSymbolTable.find(name);

        if (symbol == null) {
            throw new CompilerError("undefined global function symbol '" + name + "'");
        }
        else if (!(symbol instanceof FunctionSymbol functionSymbol)) {
            throw new CompilerError("global symbol '" + name + "' is not a function");
        }
        else {
            return functionSymbol;
        }
    }

    /**
     * Attempt to convert a value to a given type. If the value already has the given type, no conversion is done.
     * @param value The value to convert.
     * @param targetBitCount The desired integer width for the value to have after the conversion.
     * @return A new value with the desired width if a conversion was performed, or the input value otherwise.
     * @throws CompilerError Thrown if a conversion could not be performed between the two types.
     */
    private @NotNull Value convertValueType(@NotNull Value value, int targetBitCount) throws CompilerError {
        if (value.getBitCount() == targetBitCount) {
            // We already have a value of the desired type, so leave it unchanged
            return value;
        }

        // Regardless of what we do, we'll need an output register
        Register result = this.createRegister(targetBitCount);

        if (value.getBitCount() == 1) {
            // Since we are converting from a boolean to something else, perform a zero extension
            // instead of a sign extension so the boolean isn't treated as a sign bit
            this.emitter.emitZeroExtension(result, value);
        }
        else if (targetBitCount == 1) {
            // Since we are converting to a boolean from something else, compare whether the value != 0
            // instead of truncating so the boolean value isn't derived from the least significant bit only
            this.emitter.emitComparison(result, "ne", value, new ImmediateInt32(0));
        }
        else {
            throw new CompilerError("unsupported conversion from i" + value.getBitCount() + " to i" + targetBitCount);
        }

        return result;
    }

    /**
     * Recursively generate and emit the LLVM code for an AST using a postorder traversal.
     * @param node The subtree of the AST to generate.
     * @return The resulting value of the subtree, or null if there is none.
     * @throws CompilerError Thrown if any unrecognized AST nodes are encountered (which should not happen).
     */
    private @Nullable Value generateNode(@NotNull ASTNode node) throws CompilerError {
        if (node instanceof IntegerLiteral literal) {
            // Simply turn the integer literal into an immediate value
            return new ImmediateInt32(literal.getValue());
        }
        else if (node instanceof Identifier identifier) {
            // Get the stack pointer corresponding to the given identifier
            Symbol symbol = this.getLocalSymbol(identifier.getName());
            Register pointer = symbol.getRegister();

            // Emit a load instruction to obtain the local variable's value
            Register result = this.createRegister(32);
            this.emitter.emitLoad(result, pointer);

            return result;
        }
        else if (node instanceof OperatorNode operator) {
            if (operator.getOperation().equals(Operation.ASSIGNMENT)) {
                // Assignment has to be handled a little differently
                // First, generate and emit the right-hand side as usual
                Value rhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[1]));
                rhs = this.convertValueType(rhs, 32);
                // If the operator was parsed properly, the first operand must be an identifier
                Identifier identifier = (Identifier) operator.getOperands()[0];
                // Get the stack pointer corresponding to the given identifier
                Symbol symbol = this.getLocalSymbol(identifier.getName());
                Register pointer = symbol.getRegister();

                // Emit a store instruction to change the value of the variable
                this.emitter.emitStore(rhs, pointer);

                return null;
            }
            else {
                // For now, we can just assume that we are dealing with a binary operator
                // Recursively generate the operands first, and obtain the resulting values
                Value lhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[0]));
                lhs = this.convertValueType(lhs, 32);
                Value rhs = Objects.requireNonNull(this.generateNode(operator.getOperands()[1]));
                rhs = this.convertValueType(rhs, 32);

                // Each branch creates an anonymous register to hold the resulting value of the operation
                Register result;

                // Emit the instruction corresponding to the operation
                switch (operator.getOperation()) {
                    case ADDITION -> {
                        result = this.createRegister(32);
                        this.emitter.emitAddition(result, lhs, rhs);
                    }
                    case SUBTRACTION -> {
                        result = this.createRegister(32);
                        this.emitter.emitSubtraction(result, lhs, rhs);
                    }
                    case MULTIPLICATION -> {
                        result = this.createRegister(32);
                        this.emitter.emitMultiplication(result, lhs, rhs);
                    }
                    case DIVISION -> {
                        result = this.createRegister(32);
                        this.emitter.emitDivision(result, lhs, rhs);
                    }
                    case REMAINDER -> {
                        result = this.createRegister(32);
                        this.emitter.emitRemainder(result, lhs, rhs);
                    }
                    case EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "eq", lhs, rhs);
                    }
                    case NOT_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "ne", lhs, rhs);
                    }
                    case LESS_THAN -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "slt", lhs, rhs);
                    }
                    case GREATER_THAN -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sgt", lhs, rhs);
                    }
                    case LESS_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sle", lhs, rhs);
                    }
                    case GREATER_EQUAL -> {
                        result = this.createRegister(1);
                        this.emitter.emitComparison(result, "sge", lhs, rhs);
                    }
                    default -> throw new CompilerError("operation '" + operator.getOperation().getToken() + "' not implemented");
                }

                return result;
            }
        }
        else if (node instanceof FunctionCallNode functionCall) {
            // Find the symbol for the function to call in the global symbol table
            FunctionSymbol callee = this.getGlobalFunctionSymbol(functionCall.getCallee());
            // Check the number of arguments to ensure it matches the number of parameters
            if (functionCall.getArguments().length != callee.getParameterCount()) {
                throw new CompilerError("function '" + callee.getName() + "' expects " + callee.getParameterCount()
                    + " arguments but " + functionCall.getArguments().length + " were given");
            }
            List<Value> argumentValues = new ArrayList<>(functionCall.getArguments().length);
            for (ASTNode argument : functionCall.getArguments()) {
                Value argumentValue = Objects.requireNonNull(this.generateNode(argument));
                argumentValues.add(argumentValue);
            }

            Register result = this.createRegister(32);
            this.emitter.emitFunctionCall(result, callee.getRegister(), argumentValues);

            return result;
        }
        else if (node instanceof BlockStatementNode block) {
            // Generate and emit each statement of the block in order
            for (ASTNode statement : block.getStatements()) {
                this.generateNode(statement);
            }

            return null;
        }
        else if (node instanceof VariableDeclarationNode declaration) {
            // Allocate space on the stack for this new variable, creating a register to hold the pointer
            Register pointer = new Register(declaration.getName(), 32, false);
            this.emitter.emitStackAllocation(pointer);
            // Create a symbol for the local variable and add it to the symbol table
            Symbol symbol = new Symbol(declaration.getName(), pointer);
            this.localSymbolTable.insert(symbol);

            return null;
        }
        else if (node instanceof PrintNode printStatement) {
            // Generate and emit the "printee" expression
            Value printee = Objects.requireNonNull(this.generateNode(printStatement.getPrintee()));
            printee = this.convertValueType(printee, 32);

            // Emit code to print the result of the expression to standard output.
            // Since this emits a call to printf(), which returns a value, a new register is created to hold that value
            // (and is subsequently never used again)
            Register discardedResult = this.createRegister(32);
            this.emitter.emitPrint(discardedResult, printee);

            return null;
        }
        else if (node instanceof ConditionalNode conditional) {
            // Generate and emit the condition expression, then convert to i1 if necessary
            Value condition = Objects.requireNonNull(this.generateNode(conditional.getCondition()));
            condition = this.convertValueType(condition, 1);

            // These two cases aren't that different, but they are separated here for clarity
            if (conditional.getAlternative() == null) {
                // There is no alternative path for this conditional, so only two labels are needed
                Label consequentLabel = this.createLabel();
                Label tailLabel = this.createLabel();
                this.emitter.emitConditionalBranch(condition, consequentLabel, tailLabel);

                // Generate and emit the consequent path
                this.emitter.emitLabel(consequentLabel);
                this.generateNode(conditional.getConsequent());
                // Emit a branch to the tail label to exit the conditional
                this.emitter.emitUnconditionalBranch(tailLabel);

                // No alternative path, so the conditional ends here
                this.emitter.emitLabel(tailLabel);
            }
            else {
                // There is an alternative path for this conditional, so three labels are needed
                Label consequentLabel = this.createLabel();
                Label alternativeLabel = this.createLabel();
                Label tailLabel = this.createLabel();
                this.emitter.emitConditionalBranch(condition, consequentLabel, alternativeLabel);

                // Generate and emit the consequent path
                this.emitter.emitLabel(consequentLabel);
                this.generateNode(conditional.getConsequent());
                // Emit a branch to the tail label to exit the conditional
                this.emitter.emitUnconditionalBranch(tailLabel);

                // Generate and emit the alternative path
                this.emitter.emitLabel(alternativeLabel);
                this.generateNode(conditional.getAlternative());
                // Emit a branch to the tail label to exit the conditional
                this.emitter.emitUnconditionalBranch(tailLabel);

                // Both paths are finished, so the conditional ends here
                this.emitter.emitLabel(tailLabel);
            }

            return null;
        }
        else if (node instanceof WhileLoopNode whileLoop) {
            // The condition must be calculated at the beginning of each iteration, so we need to
            // start a new basic block before generating the condition expression
            Label continueLabel = this.createLabel();
            this.emitter.emitUnconditionalBranch(continueLabel);
            this.emitter.emitLabel(continueLabel);
            // Generate and emit the condition expression, then convert to i1 if necessary
            Value condition = Objects.requireNonNull(this.generateNode(whileLoop.getCondition()));
            condition = this.convertValueType(condition, 1);
            // Emit the conditional branch to test the loop condition
            Label loopBodyLabel = this.createLabel();
            Label breakLabel = this.createLabel();
            this.emitter.emitConditionalBranch(condition, loopBodyLabel, breakLabel);

            // Generate the loop body
            this.emitter.emitLabel(loopBodyLabel);
            this.generateNode(whileLoop.getLoopBody());
            // Emit a branch to the condition to start the next iteration
            this.emitter.emitUnconditionalBranch(continueLabel);

            // The loop will branch here once the condition is false
            this.emitter.emitLabel(breakLabel);

            return null;
        }
        else if (node instanceof ReturnNode returnStatement) {
            // Generate the value that will be returned
            Value returnValue = Objects.requireNonNull(this.generateNode(returnStatement.getReturnValue()));

            // Emit the instruction to return the value from the function
            this.emitter.emitReturn(returnValue);

            // Since "ret" is a terminator instruction, LLVM will implicitly insert a label after it if none is
            // present. To account for this, we can simply increment the virtual register number
            this.nextRegisterNumber++;

            return null;
        }
        else if (node instanceof FunctionDefinitionNode functionDefinition) {
            // Store the parameter names for creating the variables for each parameter later
            List<String> parameterNames = new ArrayList<>(functionDefinition.getParameters().length);
            // Create a register for each parameter value in the signature
            List<Register> parameterValues = new ArrayList<>(functionDefinition.getParameters().length);
            for (VariableDeclarationNode parameterDeclaration : functionDefinition.getParameters()) {
                parameterNames.add(parameterDeclaration.getName());
                parameterValues.add(this.createRegister(32));
            }

            // Create a global register for the function and add it to the symbol table
            // Doing this before generating the function body allows for recursive calls
            Register function = new Register(functionDefinition.getName(), 0, true);
            FunctionSymbol functionSymbol = new FunctionSymbol(functionDefinition.getName(), function, functionDefinition.getParameters().length);
            this.globalSymbolTable.insert(functionSymbol);

            // Emit the start of the function definition; the code below will emit code within the definition
            this.emitter.emitFunctionStart(function, parameterValues);
            // Emit a label for the first basic block in the function
            this.emitter.emitLabel(this.createLabel());
            // Store each parameter on the stack so it can be used like any other variable
            for (int index = 0; index < parameterValues.size(); index++) {
                // Allocate space on the stack for this new variable, creating a register to hold the pointer
                Register pointer = new Register(parameterNames.get(index), 32, false);
                this.emitter.emitStackAllocation(pointer);
                // Store the argument value in the newly created variable
                this.emitter.emitStore(parameterValues.get(index), pointer);
                // Create a symbol for the local variable and add it to the symbol table
                Symbol symbol = new Symbol(parameterNames.get(index), pointer);
                this.localSymbolTable.insert(symbol);
            }
            // Generate the function body
            this.generateNode(functionDefinition.getBody());
            // Emit the end of the function definition
            this.emitter.emitFunctionEnd();

            // Clear the local symbol table and reset the register and label numbering
            this.localSymbolTable.clear();
            this.nextRegisterNumber = 0;
            this.nextLabelNumber = 0;

            return null;
        }
        else {
            throw new CompilerError("unrecognized AST node type");
        }
    }

    /**
     * Generate and emit all of the LLVM code needed for a complete AST. This method is the primary public-facing
     * way of using this class.
     * @param writer The destination writer for the emitted LLVM code.
     * @param parser The source of statements in the form of ASTs.
     * @param sourceFilename A string describing the location of the source code for runtime debugging purposes.
     * @param enableDebug A flag enabling debug output as the program is generated.
     * @throws CompilerError Thrown if the program semantics are determined to be invalid.
     */
    public static void generate(@NotNull Writer writer, @NotNull Parser parser, @NotNull String sourceFilename, boolean enableDebug) throws CompilerError {
        // Create an emitter for the generator to use
        Emitter emitter = new Emitter(writer);

        emitter.emitPreamble(sourceFilename);

        // Create an instance of this class to keep internal state
        Generator generator = new Generator(emitter);
        // As each statement is parsed by the parser, generate and emit the code for that statement
        ASTNode statement = parser.parseTopLevelStatement();
        while (statement != null) {
            if (enableDebug) {
                System.out.println("Parsed statement: " + statement);
            }

            generator.generateNode(statement);
            statement = parser.parseTopLevelStatement();
        }

        emitter.emitPostamble();
    }
}
