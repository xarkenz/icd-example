package codegen;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Abstraction layer between the {@link Generator} and {@link Writer}.
 * Allows the generator to simply call a method to write an instruction or other LLVM code
 * without having to construct the LLVM syntax manually.
 */
public class Emitter {
    /**
     * The destination writer for the emitted LLVM code.
     * A {@link PrintWriter} wrapper is used for convenience.
     */
    private final @NotNull PrintWriter writer;

    public Emitter(@NotNull Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    /**
     * Emit the preamble to the LLVM file, which includes the source filename (for debugging purposes),
     * the target triple, the format string used to print integers, and the opening of the {@code main} function.
     * @param sourceFilename A string describing the location of the source code for debugging purposes.
     */
    public void emitPreamble(@NotNull String sourceFilename) {
        this.writer.println("source_filename = \"" + sourceFilename + "\"");
        this.writer.println("target triple = \"x86_64-pc-linux-gnu\"");
        this.writer.println();
        this.writer.println("@print_int_fstring = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\"");
        this.writer.println();
        this.writer.println("define i32 @main() {");
    }

    /**
     * Emit the postamble to the LLVM file, which includes the closing of the {@code main} function
     * and the external declaration of the {@code printf} function.
     */
    public void emitPostamble() {
        this.writer.println("\tret i32 0");
        this.writer.println("}");
        this.writer.println();
        this.writer.println("declare i32 @printf(i8*, ...)");
    }

    /**
     * Emit the {@code alloca} instruction, which allocates space on the stack and outputs a pointer to that space.
     * For example:
     * <p>
     * {@code %pointer = alloca i32}
     * @param pointer The output register for the instruction.
     */
    public void emitStackAllocation(@NotNull Register pointer) {
        this.writer.println("\t" + pointer + " = alloca i32");
    }

    /**
     * Emit the {@code store} instruction, which assigns a value from a register to a location in memory.
     * For example:
     * <p>
     * {@code store i32 %value, i32* %pointer}
     * @param value The value to store in memory.
     * @param pointer The address in memory to modify.
     */
    public void emitStore(int value, @NotNull Register pointer) {
        this.writer.println("\tstore i32 " + value + ", i32* " + pointer);
    }

    /**
     * Emit the {@code load} instruction, which initializes a register with the value at a location in memory.
     * For example:
     * <p>
     * {@code %result = load i32, i32* %pointer}
     * @param result The output register for the instruction.
     * @param pointer The address in memory to retrieve the value from.
     */
    public void emitLoad(@NotNull Register result, @NotNull Register pointer) {
        this.writer.println("\t" + result + " = load i32, i32* " + pointer);
    }

    /**
     * Emit the {@code add} instruction, which adds two integers and outputs their sum.
     * For example:
     * <p>
     * {@code %result = add nsw i32 %lhs, %rhs}
     * @param result The register which will contain the sum.
     * @param lhs The left-hand side of the operation (addend).
     * @param rhs The right-hand side of the operation (addend).
     */
    public void emitAddition(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = add nsw i32 " + lhs + ", " + rhs);
    }

    /**
     * Emit the {@code sub} instruction, which subtracts two integers and outputs their difference.
     * For example:
     * <p>
     * {@code %result = sub nsw i32 %lhs, %rhs}
     * @param result The register which will contain the difference.
     * @param lhs The left-hand side of the operation (minuend).
     * @param rhs The right-hand side of the operation (subtrahend).
     */
    public void emitSubtraction(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = sub nsw i32 " + lhs + ", " + rhs);
    }

    /**
     * Emit the {@code mul} instruction, which multiplies two integers and outputs their product.
     * For example:
     * <p>
     * {@code %result = mul nsw i32 %lhs, %rhs}
     * @param result The register which will contain the product.
     * @param lhs The left-hand side of the operation (multiplicand).
     * @param rhs The right-hand side of the operation (multiplier).
     */
    public void emitMultiplication(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = mul nsw i32 " + lhs + ", " + rhs);
    }

    /**
     * Emit the {@code sdiv} instruction, which subtracts two signed integers and outputs their quotient.
     * For example:
     * <p>
     * {@code %result = sdiv i32 %lhs, %rhs}
     * @param result The register which will contain the quotient.
     * @param lhs The left-hand side of the operation (dividend).
     * @param rhs The right-hand side of the operation (divisor).
     */
    public void emitDivision(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = sdiv i32 " + lhs + ", " + rhs);
    }

    /**
     * Emits a call to the {@code printf} function in order to print an integer value followed by a newline.
     * @param result The register which will contain the number of characters printed. (Will be discarded.)
     * @param value The integer value to print.
     */
    public void emitPrint(@NotNull Register result, @NotNull Register value) {
        this.writer.println("\t" + result + " = call i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 " + value + ")");
    }
}
