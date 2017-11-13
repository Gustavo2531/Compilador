package coolc.compiler.util;

import java.util.HashMap;
import java.util.LinkedList;

public class ClassVariables {
	private HashMap<String, LinkedList<String>> classWithVariables;
	private static ClassVariables instance;
	
	private ClassVariables() {
		classWithVariables = new HashMap<>();
	}
	
	public static ClassVariables getInstance() {
		if(instance == null) {
			instance = new ClassVariables();
		} 
		return instance;
	}
	
	public void putClass(String className) {
		classWithVariables.put(className, new LinkedList<String>());
	}
	
	public LinkedList<String> getVariableList(String className) {
		return classWithVariables.get(className);
	}
}
