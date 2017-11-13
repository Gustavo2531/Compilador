package coolc.compiler;

import org.testng.annotations.Test;

import coolc.compiler.util.MySymbolTable;
import coolc.compiler.util.SymbolTable;
import coolc.compiler.exceptions.SemanticException;;

public class SymbolTableTest {

	@Test
	public void testSimplePutGet() throws SemanticException {
		/*
		 * class Main { main(key : value): Object { key }; };
		 * mplements SymbolTable<K,V>
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			assert "value".equals(table.get("key"));
	}
	
	@Test(expectedExceptions = SemanticException.class)
	public void tesRedefinition() throws SemanticException {
		/*
		 * class Main { main(key : value, key : value): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.put("key", "value");
	}

	@Test
	public void testPutGet1() throws SemanticException {
		/*
		 * class Main { key : value; main(): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			assert "value".equals(table.get("key"));
	}

	@Test
	public void testPutGet2() throws SemanticException {
		/*
		 * class Main { key : value; main(key2 : value2): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			table.put("key2", "value2");
			assert "value".equals(table.get("key"));
	}
	
	@Test
	public void testPutGet3() throws SemanticException {
		/*
		 * class Main { key : value; main(): Object { let ?:? in key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			table.openScope();
			assert "value".equals(table.get("key"));
	}

	@Test
	public void testPutGet4() throws SemanticException {
		/*
		 * class Main { key : value; main(): Object { let key2:value2 in key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			table.openScope();
			table.put("key2", "value2");
			table.closeScope();
			assert "value".equals(table.get("key"));
	}
	
	@Test
	public void testPutGet5() throws SemanticException {
		/*
		 * class A { key : value }
		 * class Main { key : value; main(): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
		table.closeScope();
		table.openScope();
			table.put("key", "value");
	
		assert "value".equals(table.get("key"));
	}

	@Test(expectedExceptions = SemanticException.class)
	public void testPutGet6() throws SemanticException {
		/*
		 * class A { key : value }
		 * class Main { main(): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
		table.closeScope();
		table.openScope();
	
		assert "value".equals(table.get("key"));
	}
	
	@Test
	public void testPutGet7() throws SemanticException {
		/*
		 * class Main { key : value; main(key : newvalue): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			table.put("key", "new value");
	
		assert "new value".equals(table.get("key"));
	}

	@Test
	public void testPutGet8() throws SemanticException {
		/*
		 * class Main { key : value; main(key : newvalue): Object { key }; other(): Object { key }; };
		 */
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.openScope();
			table.put("key", "value");
			table.openScope();
			table.put("key", "new value");
			table.closeScope();
	
		assert "value".equals(table.get("key"));
	}
	
	@Test(expectedExceptions = SemanticException.class)
	public void test() throws SemanticException {
		SymbolTable<String, String> table = new MySymbolTable<String, String>();
		table.closeScope();
	}
}
