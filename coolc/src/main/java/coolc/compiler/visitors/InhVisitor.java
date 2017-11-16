package coolc.compiler.visitors;

import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.util.ClassVariables;
import coolc.compiler.util.CustomKlass;
import coolc.compiler.util.Error;

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
}
