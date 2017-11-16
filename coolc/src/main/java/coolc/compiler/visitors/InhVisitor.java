package coolc.compiler.visitors;

import java.util.HashMap;
import java.util.LinkedList;

import coolc.compiler.CoolSemantic.Klass;
import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.ACallExpr;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AProgram;
import coolc.compiler.autogen.node.PExpr;
import coolc.compiler.autogen.node.PFormal;

import coolc.compiler.util.ClassVariables;
import coolc.compiler.util.CustomFormalForMethod;
import coolc.compiler.util.CustomKlass;
import coolc.compiler.util.CustomMethodForKlass;
import coolc.compiler.util.Error;
import coolc.compiler.util.SymbolTable;
import coolc.compiler.util.TableClass;
import coolc.compiler.util.TableSymbol;

public class InhVisitor extends DepthFirstAdapter {
	
	CustomKlass currentCustomKlass;
	
	
	@Override
	public void inAClassDecl(AClassDecl node) {
		//System.out.println("Placing: " + node.getName().getText());
		//ClassVariables.getInstance().printKlasses();
		currentCustomKlass = ClassVariables.getInstance().searchKlassWithName(node.getName().getText());
	}
	
	@Override
	public void inAAttributeFeature(AAttributeFeature node) {
		boolean isOverriding = ClassVariables.getInstance().parentHasAttribute(node.getObjectId().getText(), currentCustomKlass);
		if(isOverriding) {
			ErrorManager.getInstance().getErrors().add(Error.ATTR_INHERITED);
			//ErrorManager.getInstance().semanticError("Coolc.semant.attrRedefinition", "n");
		}
	}
	public boolean isSubType(String obj, String second){
    	if(second.contains(obj)){
    		return true;
    		}
    	while(!second.contains("Object")){
    		AClassDecl cClass = TableClass.getInstance().getClasses().get(second);
    		
    		if(cClass==null) {
    			return false;
    		}else {
    			second = cClass.getInherits().getText();
    		}
    		if(second.contains(obj))
    			return true;
    		}
    		
    	return false;
    }
	 public void outACallExpr(ACallExpr node){
	    	String name = node.getObjectId().getText();
	    	CustomKlass cAux = currentCustomKlass;
	    	CustomKlass n = ClassVariables.getInstance().searchKlassWithName(currentCustomKlass.parent);
	    	String aux ="";
	    	if(n != null) {
	    	 	aux= n.name.toString();
	    	}else {
	    		return;
	    	}
	   
	    	boolean found = false;
	    	CustomMethodForKlass method = null;
	    	while(!aux.equals("Object")){
	    		
	    
	    		
	    		method = ClassVariables.getInstance().getMethodMap(n).get(name);
	    				
	    		
	    		if(method != null){
	    			found = true;
	    			break;
	    		}
	    		aux = cAux.parent.toString();
	    		cAux = ClassVariables.getInstance().searchKlassWithName(aux);
	    		
	    		
	    	}
			if(!found){
				 n = ClassVariables.getInstance().searchKlassWithName("Object");
				 if(n==null) {
					 
				 }else {
					 method = ClassVariables.getInstance().getMethodMap(n).get(name);
				 }
			
				//SymbolTable.getInstance().getMethod("Object").getFeatures().get(name);
			}
			if(method != null){
				LinkedList<PExpr> tries = node.getExpr();
				LinkedList<CustomFormalForMethod> params = method.getFormals();
				if(tries.size() != params.size()){
					ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
					//node.setType("minor");
					return;
				}else{
					for(int i = 0; i < params.size(); i++){
						String t = tries.get(i).toString();
						//params.get(i);
						CustomFormalForMethod pf =  params.get(i);
						String p = pf.type.toString();
						/**if(t.contains("SELF_TYPE")){
							String[] parts = t.split(" ");
							t = parts[parts.length - 1];
						}**/
						if(!isSubType(p, t)){
							ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
							//node.setType("minor");
							return;
						}
					}
				}
			}
	    }
	 
	 public void outAAtExpr(AAtExpr node){
			String theClass = node.getExpr().toString();
			
			boolean found = false;
			CustomMethodForKlass theMethod = null;
			String f = node.getObjectId().getText();
		 	CustomKlass n = ClassVariables.getInstance().searchKlassWithName(theClass);
			while(!theClass.contains("Object")){
				
				if(ClassVariables.getInstance().getMethodMap(n)!=null) {
				if( ClassVariables.getInstance().getMethodMap(n).containsKey(f)){
					
					theMethod = ClassVariables.getInstance().getMethodMap(n).get(f);
					found = true;
					break;
				}
				CustomKlass aux = ClassVariables.getInstance().searchKlassWithName(theClass);
				
						//ClassTable.getInstance().getClasses().get(theClass);
				theClass = aux.parent.toString();
				}else {
					break;
				}
			}
			
			if(!found){
				 n = ClassVariables.getInstance().searchKlassWithName("Object");
				 if(n==null) {
					 
				 }else {
					 theMethod = ClassVariables.getInstance().getMethodMap(n).get(f);
				 }
				//theMethod = 
						//SymbolTable.getInstance().getMethod("Object").getFeatures().get(f);
			}
			if(theMethod != null){
				
				LinkedList<PExpr> tries = node.getList();
				LinkedList<CustomFormalForMethod> params = theMethod.getFormals();
				if(tries.size() != params.size()){
					ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
					//node.setType("minor");
					return;
				}else{
					
					for(int i = 0; i < params.size(); i++){
						String t = tries.get(i).toString();
						CustomFormalForMethod pf =  params.get(i);
						String p = pf.type.toString();
						/**if(t.contains("SELF_TYPE")){
							String[] parts = t.split(" ");
							t = parts[parts.length - 1];
						}**/
						if(!isSubType(p, t)){
							ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
							//node.setType("minor");
							return;
						}
					}
					
				}
			}
	    }

	public void inAMethodFeature(AMethodFeature node) {
		CustomKlass n = null;
		HashMap<String, CustomMethodForKlass> hereda = null ;
		HashMap<String, CustomMethodForKlass> parent = null ;
		if(currentCustomKlass !=null) {
			
			hereda = ClassVariables.getInstance().getMethodMap(currentCustomKlass);
			n=ClassVariables.getInstance().searchKlassWithName(currentCustomKlass.parent);
			
		}
		
		if(n != null) {
			parent = ClassVariables.getInstance().getMethodMap(n);
			
		} else {
			return;
		}
		
		for (String name: parent.keySet()) {
			CustomMethodForKlass mMeth = parent.get(name);
			String m=parent.get(name).getMethodName();
			
		if(hereda.containsKey(m)) {
			
			CustomMethodForKlass iMeth = hereda.get(name);
			if (parent.get(name).getFormals().size() != hereda.get(name).getFormals().size()) {
				ErrorManager.getInstance().getErrors().add(Error.DIFF_N_FORMALS);
			
			
				}else {
					
					for(int i = 0; i< mMeth.getFormals().size(); i++) {
						CustomFormalForMethod a = iMeth.getFormals().get(i);
						CustomFormalForMethod b =  mMeth.getFormals().get(i);
						
						if(a.name.toString().equals(b.name.toString()) && !a.type.toString().equals(b.type.toString()) ) {
							ErrorManager.getInstance().getErrors().add(Error.BAD_REDEFINITION);
							
						}
					}
				}
			}
		}
		
		
	
	}
	
	
	/*
	 * BadArgs
	 * */
	public void outAProgram(AProgram node){
		HashMap<String, CustomMethodForKlass> hereda = null ;
		HashMap<String, CustomMethodForKlass> parent = null ;
		HashMap<String, String> variable=null;
		//HashMap<CustomKlass, HashMap<String, CustomMethodForKlass>> classWithAllMethods;
		
		CustomKlass n = null;
		
		if(currentCustomKlass !=null) {
			
			hereda = ClassVariables.getInstance().getMethodMap(currentCustomKlass);
			
			n=ClassVariables.getInstance().searchKlassWithName(currentCustomKlass.parent);
			variable = ClassVariables.getInstance().getVariableMap(currentCustomKlass);
			
		}
		
		if(n != null) {
			parent = ClassVariables.getInstance().getMethodMap(n);
			
			
		} else {
			return;
		}
		
		for(String s : hereda.keySet()) {
			
			
		
			if(parent.containsKey(s)) {
				CustomMethodForKlass iMeth = hereda.get(s);
				for(String cm: variable.keySet()) {
					for(int i = 0; i< iMeth.getFormals().size(); i++) {
						CustomFormalForMethod a = iMeth.getFormals().get(i);
						//CustomFormalForMethod b =  iMeth.getMethodReturnType();
						
						if(cm.contains(s)) {//&&a.name.contains(s)) {
							
							if(cm.contains(a.name)) {
							
							}else {
								ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
								ErrorManager.getInstance().semanticError("Coolc.semant.formalsFailedLong", cm ,"h","i");
								break;
							}		
						}
					}
					break;
				}
			}else {
				ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
				ErrorManager.getInstance().semanticError("Coolc.semant.formalsFailedLong", "m","t","t");
			}	
		}	
		
	}
}
