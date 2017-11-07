package coolc.compiler.visitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


import coolc.compiler.ErrorManager;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAssignExpr;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.ALetExpr;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AProgram;
import coolc.compiler.autogen.node.PFormal;
import coolc.compiler.autogen.node.PLetDecl;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.util.TableClass;

import coolc.compiler.util.Error;

import coolc.compiler.util.TableClass;


public class ExampleVisitor extends DepthFirstAdapter {
	private boolean hasMain = false;
	AClassDecl currentClass = null;
	String currentMethod = "";
	/*
	 * outAProgram is the last node visited, so we check here if there was no main method
	 */
	
	
	@Override
	public void outAProgram(AProgram node) {
		if(!hasMain){
    		ErrorManager.getInstance().getErrors().add(Error.NO_MAIN);
    		ErrorManager.getInstance().semanticError("Coolc.semant.noMain");
		}
		
	}
	 public void outALetExpr(ALetExpr node){
		    
			// types.put(node, basicClasses().get(key))
			 //ind--;
		    	LinkedList<PLetDecl> params = node.getLetDecl();
		    	for(int i = 0; i < params.size(); i++){
		    		ALetDecl p = (ALetDecl) params.get(i);
		    		if(p.getObjectId().getText().equals("self")){
		    			ErrorManager.getInstance().getErrors().add(Error.SELF_IN_LET);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.selfInLet");
		    			//types.put(node, basicClasses().get("Let"));
		    			
		    		}
		    	}
			  
		  }

	public void inAMethodFeature(AMethodFeature node){
		
	LinkedList<PFormal> params = node.getFormal();
    	ArrayList<String> repeated = new ArrayList<>();
    	boolean redefined = false;
   
		//System.out.println(depth+clase + node.getObjectId().getText());

   
    	for(int i = 0; i < params.size(); i++){
    		AFormal p = (AFormal) params.get(i);
    		if(!repeated.contains(p.getObjectId().getText())){
    			repeated.add(p.getObjectId().getText());
    		}else{
    			redefined = true;
    		}
    		if(p.getObjectId().getText().equals("self")){
    			ErrorManager.getInstance().getErrors().add(Error.SELF_FORMAL);
    		}
    	}
    	if(redefined){
    		ErrorManager.getInstance().getErrors().add(Error.FORMAL_REDEFINITION);
    	}
    //	String myType = node.getTypeId().getText();
		/**String myType = node.getTypeId().getText();
    		if(!myType.equals("SELF_TYPE")){
	    	if(!TableClass.getInstance().getClasses().containsKey(myType)){
	    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.typeNotFound", node.getObjectId(),node.getTypeId());
	    	}
    	}
    	if(!myType.equals("SELF_TYPE")){
	    	if(!TableClass.getInstance().getClasses().containsKey(myType)){
	    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
	    	}
    	}
    //	ind++;
    	currentMethod = node.getObjectId().getText();
    	String cClass = currentClass.getName().getText();

    	LinkedList<PFormal> params = node.getFormal();
    	
    	for(int i = 0; i < params.size(); i++){
    		AFormal p = (AFormal) params.get(i);**/
		/**if(node.getTypeId().getText().equals("SELF_TYPE")){
			ErrorManager.getInstance().getErrors().add(Error.SELF_TYPE_FORMAL);
			ErrorManager.getInstance().getErrors().add(Error.UNDECL_IDENTIFIER);
			ErrorManager.getInstance().semanticError("Coolc.semant.selfTypeFormal", node.getTypeId().getText());
			//SymbolTable.getInstance().getAuxiliar(ind+cClass+currentMethod).getCertainClass(cClass).getTypes().remove(p.getObjectId().getText());
			
		}**/
    	
    }
	
	/*
	 * When visiting every Class, we check if it is called Main.
	 */
	@Override
	public void inAClassDecl(AClassDecl node) {
		
	
	
		
		
	
		//currentClass = node;
		
	}

	public void outAClassDecl(AClassDecl node){
		
		if(node.getName().getText().equals("Main")){
			hasMain = true;
		}
		
		
		//Por defecto se hereda de Object
		if(node.getInherits() == null){
			node.setInherits(new TTypeId("Object"));
		}
		
		if(node.getInherits().getText().equals("Bool") || node.getInherits().getText().equals("String")){
			ErrorManager.getInstance().getErrors().add(Error.INHERIT_BASIC);
			ErrorManager.getInstance().semanticError("Coolc.semant.inheritBasic",node.getName().getText());
		}

		
		if(node.getInherits().getText().equals("SELF_TYPE")){
			ErrorManager.getInstance().getErrors().add(Error.INHERIT_SELF_TYPE);
		if(node.getName().getText().equals("Main")){
						hasMain = false;
						ErrorManager.getInstance().semanticError("Coolc.semant.inheritSelfType",node.getName().getText());
			}
		}
		
		if(node.getName().getText().equals("Int") || node.getName().getText().equals("SELF_TYPE") || node.getName().getText().equals("String") || node.getName().getText().equals("Object") ||
				node.getName().getText().equals("Bool")){
			ErrorManager.getInstance().getErrors().add(Error.REDEF_BASIC);
			ErrorManager.getInstance().semanticError("Coolc.semant.redefBasic",node.getName().getText());
		}
		
			
	
	}
	public void outAAttributeFeature(AAttributeFeature node){
		
        if(node.getObjectId().getText().equals("self")){
        	
        		ErrorManager.getInstance().getErrors().add(Error.SELF_ATTR);
        		ErrorManager.getInstance().semanticError("Coolc.semant.selfAttr");
        }
	}
	
	public void outAAssignExpr(AAssignExpr node){
    	String s = node.getObjectId().getText();
    	if(s.equals("self")){
    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
    		ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf");
    		return;
    		}
	}
	
}
