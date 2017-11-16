package coolc.compiler.visitors;

import java.util.HashMap;

import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.util.ClassVariables;
import coolc.compiler.util.CustomKlass;
import coolc.compiler.util.CustomMethodForKlass;
import coolc.compiler.util.Error;

public class InhVisitor extends DepthFirstAdapter {
	
	CustomKlass currentCustomKlass;
	CustomMethodForKlass currentMethodForKlass;
	
	
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
	
	@Override
	public void inAMethodFeature(AMethodFeature node) {
		HashMap<String, CustomMethodForKlass> currentClassMethods = ClassVariables.getInstance().getMethodMap(currentCustomKlass);
		if(currentClassMethods != null) {
			currentMethodForKlass = currentClassMethods.get(node.getObjectId().getText());
			if(currentMethodForKlass != null) {
				//System.out.println("I have definition for: " + node.getObjectId().getText());
				if(!ClassVariables.getInstance().validOverridingMethodTypes(currentCustomKlass, currentMethodForKlass)) {
					ErrorManager.getInstance().getErrors().add(Error.BAD_REDEFINITION);
					ErrorManager.getInstance().semanticError("Coolc.semant.badRedefinition", currentMethodForKlass.getMethodName(), "n", "n");
					//Coolc.semant.badRedefinition=In redefined method [%s], parameter type [%s] is different from original type [%s].\n
				}
			}
		}
	}
}
