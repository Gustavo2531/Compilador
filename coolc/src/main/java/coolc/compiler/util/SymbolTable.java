package coolc.compiler.util;

import coolc.compiler.exceptions.SemanticException;

public interface SymbolTable<K, V> {
	/**
	 * Creates a new mapping between K and V
	 */
	public void openScope();
	
	/**
	 * Destroy last mapping between K and V (LIFO)
	 */
	public void closeScope() throws SemanticException;
	
	/**
	 * Searches for K in all mappings starting from last, exception if not found
	 */
	public V get(K s) throws SemanticException;
	
	/**
	 * Relates K to V in current mapping, does not allow remapping of symbols
	 * in the same scope
	 */
	public void put (K k, V v) throws SemanticException;
}
