package coolc.compiler.util;

import coolc.compiler.autogen.node.Node;

public class MySymbolScopeTable {
	private static MySymbolScopeTable instance;
	
	private MySymbolTable<String, Node> symbolTable;
	
	private MySymbolScopeTable () {
		symbolTable = new MySymbolTable<String, Node>();
	}
	
	public static MySymbolScopeTable getInstance() {
		if(instance == null) {
			instance = new MySymbolScopeTable();
		}
		return instance;
	}
	
	public MySymbolTable<String, Node> getSymbolTable() {
		return symbolTable;
	}
}
