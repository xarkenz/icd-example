package codegen.value;

/**
 * Interface representing any form of value in LLVM.
 * This allows for polymorphic behavior in handling values during LLVM code generation.
 */
public interface Value {
    /**
     * Assuming this value represents an integer, obtain the width of the integer in bits.
     * This is basically just a stopgap solution until a more sophisticated type system is implemented.
     * @return The width of the integer represented by this value in bits.
     */
    int getBitCount();
}
