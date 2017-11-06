package coolc.compiler.util;

import java.util.HashMap;


import coolc.compiler.autogen.node.AClassDecl;

public class TableClass {
	private static TableClass instance;
	private HashMap<String, AClassDecl> classes;
	
	private TableClass(){
		classes = new HashMap<>();
	}
	
	public void setClasses(HashMap<String, AClassDecl> classes){
		this.classes = classes;
	}
	
	public HashMap<String, AClassDecl> getClasses(){
		return classes;
	}

	public static TableClass getInstance(){
		if(instance == null){
			instance = new TableClass();
		}
		return instance;
	}
	
	public void reset(){
		classes = new HashMap<>();
	}
}