package coolc.compiler.util;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import coolc.compiler.exceptions.SemanticException;

public class MySymbolTable<K,V> implements SymbolTable<K,V>{
	private Stack<HashMap<K, V>> layers;
	
	public MySymbolTable() {
		layers = new Stack<HashMap<K, V>>();
	}
	public void openScope() {
		layers.push(new HashMap<K, V>());
	}

	public boolean isEmpty() {
		return layers.size() == 1 && layers.peek().isEmpty();
	}

	public boolean containsKey(K key) {
		//assert key instanceof String;
		
		for(int i = layers.size()-1; i>=0; i--) {
			if (layers.get(i).containsKey(key)) return true;
		}
		
		return false;
	}

	public boolean containsValue(Object value) {
		return false;
	}

	/**public String get(Object key) {
		assert key instanceof String;
		
		for(int i = layers.size()-1; i>=0; i--) {
			if (layers.get(i).containsKey(key)) 
				return layers.get(i).get(key);
		}
		
		return null;
	}**/

		/**
		 * Creates a new mapping between K and V
		 */
		
		
		/**
		 * Destroy last mapping between K and V (LIFO)
		 */
		public void closeScope() throws SemanticException{
			
			if (layers.size() <= 0) {
				
				 throw new SemanticException();
		    }else {

		    		layers.pop();
			
		    }
			//return layers.peek().remove(key);
		}
		
		/**
		 * Searches for K in all mappings starting from last, exception if not found
		 */
		public V get(K s) throws SemanticException{
			for(int i = layers.size()-1; i>=0; i--) {
					if (layers.get(i).containsKey(s)) 
						return layers.get(i).get(s);
				}
			
				throw new SemanticException();
				
				
			
		}
		
		/**
		 * Relates K to V in current mapping, does not allow remapping of symbols
		 * in the same scope
		 */
		public void put (K k, V v) throws SemanticException{
			
			if(layers.peek().get(k) != null) {
				throw new SemanticException();
			} else {
				layers.peek().put(k, v);
			}
				/**try {
						get(k);
						
				
				} catch (SemanticException e) {
					layers.peek().put(k, v);
					throw new SemanticException();
				
				}**/
		}

		

	
	

}
