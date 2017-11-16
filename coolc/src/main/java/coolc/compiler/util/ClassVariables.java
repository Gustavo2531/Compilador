package coolc.compiler.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ClassVariables {
	private HashMap<CustomKlass, HashMap<String, String>> classWithVariables;
	private HashMap<CustomKlass, HashMap<String, CustomMethodForKlass>> classWithMethods;
	
	private static ClassVariables instance;
	
	private ClassVariables() {
		classWithVariables = new HashMap<>();
		classWithMethods = new HashMap<>();
	}
	
	public static ClassVariables getInstance() {
		if(instance == null) {
			instance = new ClassVariables();
		} 
		return instance;
	}
	
	public void reset() {
		instance = new ClassVariables();
	}
	
	public void putClass(CustomKlass klass) {
		classWithVariables.put(klass, new HashMap<String, String>());
		classWithMethods.put(klass, new HashMap<String, CustomMethodForKlass>());
	}
	
	public HashMap<String, String> getVariableMap(CustomKlass klass) {
		return classWithVariables.get(klass);
	}
	
	public HashMap<String, CustomMethodForKlass> getMethodMap(CustomKlass klass){
		return classWithMethods.get(klass);
	}
	
	public CustomKlass searchKlassWithName(String name) {
		CustomKlass foundKlass = null;
		Iterator it = classWithVariables.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if((((CustomKlass)pair.getKey()).name).equals(name)) {
	        		foundKlass = (CustomKlass)pair.getKey();
	        }
	    }
		
		
		return foundKlass;
	}
	
	public void printKlasses() {
		Iterator it = classWithVariables.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println("Has: " + ((CustomKlass)pair.getKey()).name);
	    }
	}
	
	public boolean hasAttribute(CustomKlass klass, String attr) {
		
		HashMap<String, String> klassVariables = classWithVariables.get(klass);
		if(klassVariables != null) {
			String var = klassVariables.get(attr);
			return var != null;
		}
		
		return false;
	}
	
	public boolean parentHasAttribute(String attr, CustomKlass klass) {
		return parentHasAttribute (attr, klass, 0);
	}
	
	
	private boolean parentHasAttribute(String attr, CustomKlass klass, int foundAttributeN) {
		CustomKlass parent;
		
		if(klass.name.equals("Object")) {
			return foundAttributeN > 1;
		}
		
		if(hasAttribute(klass, attr)) {
			foundAttributeN++;
		} 
		
		if(foundAttributeN > 1) {
			return true;
		} else {
			parent = searchKlassWithName(klass.parent);
			
			if(parent != null) {
				return parentHasAttribute(attr, parent, foundAttributeN);
			} else {
				return false;
			}
		}
	}
	
	public boolean validOverridingMethodTypes(CustomKlass currentKlass, CustomMethodForKlass currentMethod) {
		return validOverridingMethodTypes(currentKlass, currentMethod, null);
	}
	
	private boolean validOverridingMethodTypes(CustomKlass currentKlass, CustomMethodForKlass currentMethod, CustomKlass parentKlass) {
		
		if(parentKlass != null) {
			parentKlass = searchKlassWithName(parentKlass.parent);
			if(parentKlass == null) {
				return true;
			}
		}
		
		if(parentKlass == null && !currentKlass.name.equals("Object")) {
			if(!currentKlass.parent.equals("Object")) {
				parentKlass = searchKlassWithName(currentKlass.parent);
				if(parentKlass == null) {
					return true;
				}
			} else {
				return true;
			}
		} else if(currentKlass.name.equals("Object")){
			return true;
		}
		
		HashMap<String, CustomMethodForKlass> parentKlassMethods = classWithMethods.get(parentKlass);
		if(parentKlassMethods != null) {
			CustomMethodForKlass parentMethod = parentKlassMethods.get(currentMethod.getMethodName());
			
			if(parentMethod != null) {
				boolean correctTypes = true;
				
				for(CustomFormalForMethod currentFormal : currentMethod.getFormals()) {
					String currentId = currentFormal.name;
					String currentType = currentFormal.type;
					
					for(CustomFormalForMethod parentFormal : parentMethod.getFormals()) {
						if(parentFormal.name.equals(currentId)) {
							if(!parentFormal.type.equals(currentType)) {
								correctTypes = false;
							}
							break;
						}
					}
					
					if(!correctTypes) {
						break;
					}
				}
				
				if(!correctTypes) {
					return false;
				} else {
					return validOverridingMethodTypes(currentKlass, currentMethod, parentKlass);
				}
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
}
