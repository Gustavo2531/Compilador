package coolc.compiler.visitors;

import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.ACallExpr;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.AIfExpr;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AMinusExpr;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PExpr;
import coolc.compiler.autogen.node.PFormal;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.util.TableClass;

import coolc.compiler.util.Error;

import coolc.compiler.util.TableSymbol;

public class OtherVisitor extends DepthFirstAdapter {
	String clase = "";
	String hereda = "";
	boolean need = false;
	int indice = 0;
	AClassDecl currentClass = null;
	private Map<Node, AClassDecl> types = new HashMap<Node, AClassDecl>();
	
	public void inAClassDecl(AClassDecl node){
		if(!TableClass.getInstance().getClasses().containsKey(node.getInherits().getText())){
			if(!node.getInherits().getText().equals("Object")){
				ErrorManager.getInstance().getErrors().add(Error.CANNOT_INHERIT);
			}else{
				need = false;
			}
		}else{
			clase = node.getName().getText();
			hereda = node.getInherits().getText();
			need = true;
		}
		
		currentClass = node;
	}
	
	public boolean isSubType(String obj, String second){
    	if(obj.equals(second)){
    		return true;
    		}
    	while(!second.equals("Object")){
    		AClassDecl cClass = TableClass.getInstance().getClasses().get(second);
    		second = cClass.getInherits().getText();
    		if(second.equals(obj))
    			return true;
    		}
    	return false;
    }
	
	public void outAAtExpr(AAtExpr node){
		String theClass = node.getTypeId().toString();
		if(theClass.contains("SELF_TYPE")){
			theClass = currentClass.getName().getText();
		}
    	if(node.getTypeId() != null){
    		String other = node.getTypeId().getText();
    		if(!isSubType(other, theClass)){
    			ErrorManager.getInstance().getErrors().add(Error.STATIC_FAIL_TYPE);
    			if(isSubType(theClass, other)){
    				//node.setType("minor");
    			}else{
    				//node.setType("major");
    			}
    			return;
    		}
    	}
		boolean found = false;
		AMethodFeature theMethod = null;
		String f = node.getObjectId().getText();
		while(!theClass.equals("Object")){
			if(TableSymbol.getInstance().getMethod(theClass).getFeatures().containsKey(f)){
				theMethod = TableSymbol.getInstance().getMethod(theClass).getFeatures().get(f);
				found = true;
				break;
			}
			AClassDecl aux = TableClass.getInstance().getClasses().get(theClass);
			theClass = aux.getInherits().getText();
		}
		if(!found){
			theMethod = TableSymbol.getInstance().getMethod("Object").getFeatures().get(f);
		}
		if(theMethod != null){
			LinkedList<PExpr> tries = node.getList();
			LinkedList<PFormal> params = theMethod.getFormal();
			if(tries.size() != params.size()){
				ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
				//node.setType("minor");
				return;
			}else{
				for(int i = 0; i < params.size(); i++){
					String t = tries.get(i).toString();
					AFormal pf = (AFormal) params.get(i);
					String p = pf.getTypeId().getText();
					if(t.contains("SELF_TYPE")){
						String[] parts = t.split(" ");
						t = parts[parts.length - 1];
					}
					if(!isSubType(p, t)){
						ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
						//node.setType("minor");
						return;
					}
				}
				if(theMethod.getTypeId().getText().equals("SELF_TYPE")){
					theClass = node.getTypeId().toString();
					if(theClass.contains("SELF_TYPE")){
						theClass = currentClass.getName().getText();
					}
					//node.setType(theClass);
				}else{
					//node.setType(theMethod.getTypeId().getText());
				}
			}
		}else{
			ErrorManager.getInstance().getErrors().add(Error.DISPATCH_UNDEFINED);
			//node.setType("minor");
			return;
		}
    }
	  

    public void outALetDecl(ALetDecl node){
    	if(node.getExpr() != null){
    		if(!node.getTypeId().getText().equals("SELF_TYPE")){
	    		String s = node.getTypeId().toString();
	    		if(!isSubType(node.getTypeId().getText(), s)){
	    			ErrorManager.getInstance().getErrors().add(Error.BAD_LET_INIT);
	    		}
    		}
    		}
    }
    
	 public void outACallExpr(ACallExpr node){
	    	String name = node.getObjectId().getText();
	    	AClassDecl cAux = currentClass;
	    	String aux = currentClass.getName().getText();
	    	boolean found = false;
	    	AMethodFeature method = null;
	    	while(!aux.equals("Object")){
	    		
	    		
	    		
	    		method =TableSymbol.getInstance().getMethod(aux).getFeatures().get(name);
	    		
	    		if(method != null){
	    			found = true;
	    			break;
	    		}
	    		aux = cAux.getInherits().getText();
	    		cAux =TableClass.getInstance().getClasses().get(aux);
	    	}
			if(!found){
				method = TableSymbol.getInstance().getMethod("Object").getFeatures().get(name);
			}
			if(method != null){
				LinkedList<PExpr> tries = node.getExpr();
				
				LinkedList<PFormal> params = method.getFormal();
				if(tries.size() != params.size()){
					ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
					//node.setType("minor");
					return;
				}else{
					for(int i = 0; i < params.size(); i++){
						String t = tries.get(i).toString();
						AFormal pf = (AFormal) params.get(i);
						String p = pf.getTypeId().getText();
						if(t.contains("SELF_TYPE")){
							String[] parts = t.split(" ");
							t = parts[parts.length - 1];
						}
						if(!isSubType(p, t)){
							ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
							
							//node.setType("minor");
							return;
						}
					}
					if(method.getTypeId().getText().equals("SELF_TYPE")){
					
						//node.setType("SELF_TYPE of " + currentClass.getName().getText());
					}else{
						//node.setType(method.getTypeId().getText());
					}
				}
			}else{
				ErrorManager.getInstance().getErrors().add(Error.DISPATCH_UNDEFINED);
				//node.setType("minor");
				return;
			}
	    }
	 
	   public void outAIfExpr(AIfExpr node){
	    	if(!node.getTest().toString().equals("Bool")){
	    		ErrorManager.getInstance().getErrors().add(Error.BAD_PREDICATE);
	    		//node.setType("minor");
	    	}else{
	    		//String tType = node.getTrue().getTypeAsString();
	    		//String fType = node.getFalse().getTypeAsString();
	    		//String myType = commonAnc(tType, fType);
	    		//node.setType(myType);
	    		}
	    }
	   
	   public void outAClassDecl(AClassDecl node){
			
			if(need){
				HashMap<String, TTypeId> padreVariables = TableSymbol.getInstance().getAuxiliar(indice + clase).getCertainClass(clase).getTypes();
				HashMap<String, TTypeId> herVariables = TableSymbol.getInstance().getAuxiliar(indice + hereda).getCertainClass(hereda).getTypes();
				
				for(String s :padreVariables.keySet()){
					if(herVariables.containsKey(s)){
						ErrorManager.getInstance().getErrors().add(Error.ATTR_INHERITED);
					}
				}
				
				HashMap<String, AMethodFeature> padreMetodos = TableSymbol.getInstance().getMethod(clase).getFeatures();
				HashMap<String, AMethodFeature> herMetodos = TableSymbol.getInstance().getMethod(hereda).getFeatures();
				
				for(String name : padreMetodos.keySet()){
					AMethodFeature mMeth = padreMetodos.get(name);
					if(herMetodos.containsKey(name)){
						AMethodFeature iMeth = herMetodos.get(name);
						if(mMeth.getFormal().size() != iMeth.getFormal().size()){
							ErrorManager.getInstance().getErrors().add(Error.DIFF_N_FORMALS);
						}else{
							for(int i = 0; i < mMeth.getFormal().size(); i++){
								AFormal a = (AFormal) iMeth.getFormal().get(i);
								AFormal b = (AFormal) mMeth.getFormal().get(i);
								
								if(a.getObjectId().getText().equals(b.getObjectId().getText()) &&
										!a.getTypeId().getText().equals(b.getTypeId().getText())){
									ErrorManager.getInstance().getErrors().add(Error.BAD_REDEFINITION);
								}
							}
						}
					}
				}
			}
		}
	 
}
