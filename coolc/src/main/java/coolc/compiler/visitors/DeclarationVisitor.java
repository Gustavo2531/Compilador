package coolc.compiler.visitors;

import coolc.compiler.ErrorManager;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAssignExpr;
import coolc.compiler.autogen.node.ABranch;
import coolc.compiler.autogen.node.ACaseExpr;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.AObjectExpr;

public class DeclarationVisitor extends DepthFirstAdapter{
	
	/*
	 * Visitor that will check each declaration in order to fill a Stack with mappings of each type of variable. 
	 * Types: Attribute, parameter, or local variable  
	 */
	
	
	/* 
	 * In/out class 
	 */
	@Override
	public void inAClassDecl (AClassDecl node){
		
	}
	@Override 
	public void outAClassDecl(AClassDecl node){
		
	}
	
	/* 
	 * In/out assignment of variable  
	 */
	@Override 
	public void inAAssignExpr (AAssignExpr node){
		
	}
	@Override 
	public void outAAssignExpr(AAssignExpr node){
		
	}
	
	/* 
	 * In/out case branch
	 */
	@Override 
	public void inABranch (ABranch node){
		
	}
	@Override
	public void outABranch(ABranch node){
		
	}
	
	/* 
	 * In/out case 
	 */
	@Override 
	public void inACaseExpr(ACaseExpr node){
		
	}
	@Override 
	public void outACaseExpr( ACaseExpr node){
		
	}
	/* 
	 * In/out let  
	 */
	@Override 
	public void inALetDecl(ALetDecl node){
		
	}
	@Override 
	public void outALetDecl(ALetDecl node){
		
	}
	
	/* 
	 * In/out object 
	 */
	@Override 
	public void inAObjectExpr(AObjectExpr node){
		
	}
	@Override 
	public void outAObjectExpr(AObjectExpr node){
		
	}
	
	/* 
	 * In/out method  
	 */
	@Override
	public void inAFormal (AFormal node){
		
	}
	@Override 
	public void outAFormal(AFormal node){
		
	}

}
