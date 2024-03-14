package codegen.symbol;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom hash table implementation mapping symbol names to the symbols they represent.
 * Conflict resolution is handled via chaining (i.e. each bucket is a linked list).
 * No rehashing is done currently, though this may change in the future.
 * @see Symbol
 */
public class SymbolTable {
    /**
     * A node in a linked list of {@link Symbol} objects, used for buckets in the symbol table.
     * The linked list this creates is singly-linked, so each node only references the next node in the list.
     */
    private static class Node {
        public @NotNull Symbol symbol;
        public @Nullable Node next;

        public Node(@NotNull Symbol symbol, @Nullable Node next) {
            this.symbol = symbol;
            this.next = next;
        }
    }

    /**
     * The default number of buckets allocated for the hash table.
     */
    public static final int DEFAULT_CAPACITY = 64;

    /**
     * The hash table itself, which is an array of linked-list buckets. The length of the array is the
     * "capacity" of the table.
     */
    private final @Nullable Node[] buckets;

    /**
     * Create a new symbol table with capacity of {@link #DEFAULT_CAPACITY}.
     */
    public SymbolTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Create a new symbol table with a given capacity.
     * @param capacity The number of buckets to allocate for the hash table.
     */
    public SymbolTable(int capacity) {
        this.buckets = new Node[capacity];
    }

    /**
     * @return The number of buckets allocated for the hash table.
     */
    public int getCapacity() {
        return this.buckets.length;
    }

    /**
     * Implementation of a Fowler-Noll-Vo (FNV) hash function transforming a string key to an index in the table.
     * Specifically, a FNV-1a hash is performed, which does a bitwise XOR then multiplication in a loop.
     * <p>
     * More information about this algorithm can be found
     * <a href="https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function#FNV-1a_hash">here</a>.
     * @param key The string to perform the hash function on.
     * @return The remainder from dividing the hashed value by the hash table size. This produces a valid index.
     */
    private int computeHash(@NotNull String key) {
        final long FNV_OFFSET_BASIS = 0xCBF29CE484222325L;
        final long FNV_PRIME = 0x100000001B3L;

        long hash = FNV_OFFSET_BASIS;
        for (byte byteValue : key.getBytes()) {
            hash ^= Byte.toUnsignedLong(byteValue);
            hash *= FNV_PRIME;
        }

        // It would be nice if we could just use the modulo operator here, but Java doesn't have unsigned longs
        return (int) Long.remainderUnsigned(hash, this.getCapacity());
    }

    /**
     * Insert a new symbol into the symbol table based on its name. In a name conflict, no entries are overwritten;
     * however, {@link #find(String)} will return the most recently inserted symbol.
     * @param symbol The symbol to insert into the symbol table.
     */
    public void insert(@NotNull Symbol symbol) {
        // Obtain the bucket index using the hash
        int bucketIndex = this.computeHash(symbol.getName());

        // Push the symbol at the beginning of the linked-list bucket
        this.buckets[bucketIndex] = new Node(symbol, this.buckets[bucketIndex]);
    }

    /**
     * Search for a symbol in the symbol table by name.
     * @param name The name to use as the key when searching.
     * @return The most recently inserted symbol with the given name, if it exists, or null otherwise.
     */
    public @Nullable Symbol find(@NotNull String name) {
        // Obtain the bucket index using the hash
        int bucketIndex = this.computeHash(name);

        // Perform a linear search until a symbol matching the name is found
        Node currentNode = this.buckets[bucketIndex];
        while (currentNode != null && !currentNode.symbol.getName().equals(name)) {
            currentNode = currentNode.next;
        }

        // Return the symbol found, or null if the bucket didn't contain the item
        return currentNode == null ? null : currentNode.symbol;
    }
}
