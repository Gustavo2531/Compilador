package coolc.compiler.visitors;

import java.util.LinkedList;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.AAttributeFeature;
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
import coolc.compiler.util.ClassVariables;
import coolc.compiler.util.CustomFormalForMethod;
import coolc.compiler.util.CustomKlass;
import coolc.compiler.util.CustomMethodForKlass;
import coolc.compiler.util.Error;

import coolc.compiler.util.TableSymbol;

public class OtherVisitor extends DepthFirstAdapter {
	CustomKlass currentClass;
	CustomMethodForKlass currentMethod;
	
	@Override
	public void inAClassDecl(AClassDecl node) {
		String inherits = "";
		if (node.getInherits() == null) {
			inherits = "Object";
		} else {
			inherits = node.getInherits().getText();
		}
		currentClass = new CustomKlass(node.getName().getText(), inherits);
		ClassVariables.getInstance().putClass(currentClass);
	}
	
	@Override
	public void outAAttributeFeature(AAttributeFeature node) {
		ClassVariables.getInstance().getVariableMap(currentClass).put(node.getObjectId().getText(), node.getTypeId().getText());
	}
	
	@Override
	public void outAMethodFeature(AMethodFeature node) {
		ClassVariables.getInstance().getMethodMap(currentClass).put(node.getObjectId().getText(), currentMethod);
	}
	
	@Override
	public void inAMethodFeature(AMethodFeature node) {
		currentMethod = new CustomMethodForKlass(node.getObjectId().getText(), node.getTypeId().getText());
	}
	
	@Override
	public void outAFormal(AFormal node) {
		String formalId = node.getObjectId().getText();
		String formalType = node.getTypeId().getText();
		
		currentMethod.getFormals().add(new CustomFormalForMethod(formalId, formalType));
	}
}
