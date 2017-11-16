package coolc.compiler.util;

import java.util.LinkedList;

public class CustomMethodForKlass {
	
	private String methodName;
	private String returnsType;
	private LinkedList<CustomFormalForMethod> formals;
	
	public CustomMethodForKlass(String methodName, String returnsType) {
		this.methodName = methodName;
		this.returnsType = returnsType;
		formals = new LinkedList<CustomFormalForMethod>();
	}
	
	public LinkedList<CustomFormalForMethod> getFormals(){
		return formals;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public String getMethodReturnType() {
		return returnsType;
	}
}
