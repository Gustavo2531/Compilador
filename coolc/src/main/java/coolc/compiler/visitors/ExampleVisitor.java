package coolc.compiler.visitors;

import coolc.compiler.ErrorManager;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AProgram;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.util.TableClass;
import coolc.compiler.util.Error;
import coolc.compiler.util.TableClass;


public class ExampleVisitor extends DepthFirstAdapter {
	private boolean hasMain = false;
	
	/*
	 * outAProgram is the last node visited, so we check here if there was no main method
	 */
	
	
	@Override
	public void outAProgram(AProgram node) {

	}

	/*
	 * When visiting every Class, we check if it is called Main.
	 */
	@Override
	public void inAClassDecl(AClassDecl node) {
		
	
	
		
	/*	if(!TableClass.getInstance().getClasses().containsKey(node.getInherits().getText())){
			if(!node.getInherits().getText().equals("Object")){
				ErrorManager.getInstance().getErrors().add(Error.CANNOT_INHERIT);
				ErrorManager.getInstance().semanticError("Coolc.semant.cannotInherit",node.getName().getText());
			}
		}
	 * 
	 * */
		
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
	
}
