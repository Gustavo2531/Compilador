package coolc.compiler.visitors;

import java.util.HashMap;

import coolc.compiler.CoolSemantic.Klass;
import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.util.ClassVariables;
import coolc.compiler.util.CustomFormalForMethod;
import coolc.compiler.util.CustomKlass;
import coolc.compiler.util.CustomMethodForKlass;
import coolc.compiler.util.Error;
import coolc.compiler.util.SymbolTable;

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
}
