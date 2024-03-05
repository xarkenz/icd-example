package codegen;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.Writer;

public class Emitter {
    private final @NotNull PrintWriter writer;

    public Emitter(@NotNull Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public void emitPreamble(@NotNull String sourceFilename) {
        this.writer.println("source_filename = \"" + sourceFilename + "\"");
        this.writer.println("target triple = \"x86_64-pc-linux-gnu\"");
        this.writer.println();
        this.writer.println("@print_int_fstring = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\"");
        this.writer.println();
        this.writer.println("define i32 @main() {");
    }

    public void emitPostamble() {
        this.writer.println("\tret i32 0");
        this.writer.println("}");
        this.writer.println();
        this.writer.println("declare i32 @printf(i8*, ...)");
    }

    public void emitStackAllocation(@NotNull Register pointer) {
        this.writer.println("\t" + pointer + " = alloca i32");
    }

    public void emitStore(int value, @NotNull Register pointer) {
        this.writer.println("\tstore i32 " + value + ", i32* " + pointer);
    }

    public void emitLoad(@NotNull Register result, @NotNull Register pointer) {
        this.writer.println("\t" + result + " = load i32, i32* " + pointer);
    }

    public void emitAddition(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = add nsw i32 " + lhs + ", " + rhs);
    }

    public void emitSubtraction(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = sub nsw i32 " + lhs + ", " + rhs);
    }

    public void emitMultiplication(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = mul nsw i32 " + lhs + ", " + rhs);
    }

    public void emitDivision(@NotNull Register result, @NotNull Register lhs, @NotNull Register rhs) {
        this.writer.println("\t" + result + " = sdiv i32 " + lhs + ", " + rhs);
    }

    public void emitPrint(@NotNull Register value) {
        this.writer.println("\tcall i32(i8*, ...) @printf(i8* bitcast ([4 x i8]* @print_int_fstring to i8*), i32 " + value + ")");
    }
}
