package coolc.compiler.visitors;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAssignExpr;
import coolc.compiler.autogen.node.ABranch;
import coolc.compiler.autogen.node.ACaseExpr;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AObjectExpr;
import coolc.compiler.autogen.node.PExpr;

public class DeclarationVisitor extends DepthFirstAdapter{
	/*
	 * Visitor that will check each declaration in order to fill a Stack with mappings of each type of variable. 
	 * Types: Attribute, parameter, or local variable  
	 */
	
	//attributes, parameters, local values 
	private int level =0; 
	Multimap<String, PExpr> map= ArrayListMultimap.create(); 
	public int getLevel(){
		return this.level;
	}
	
	/* 
	 * In/out class 
	 */
	@Override
 	public void inAClassDecl (AClassDecl node){	
		this.level++;
	}
	@Override 
	public void outAClassDecl(AClassDecl node){
		this.level--;
	}
	
	/*
	 * In/out method 
	 */
	@Override 
	public void inAMethodFeature(AMethodFeature node){
		this.level++;
		map.put("Parameter", node.getExpr());
	}
	@Override 
	public void outAMethodFeature(AMethodFeature node){
		this.level--; 
	}
	
	/* 
	 * In/out assignment of variable  
	 */
	@Override 
	public void inAAssignExpr (AAssignExpr node){
		this.level++; 
		map.put("Local Variable", node.getExpr()); 
	}
	@Override 
	public void outAAssignExpr(AAssignExpr node){
		this.level--; 
	}
	
	/* 
	 * In/out case branch
	 */
	@Override 
	public void inABranch (ABranch node){
		this.level++; 
	}
	@Override
	public void outABranch(ABranch node){
		this.level--; 
	}
	
	/* 
	 * In/out case 
	 */
	@Override 
	public void inACaseExpr(ACaseExpr node){
		this.level++; 
	}
	@Override 
	public void outACaseExpr( ACaseExpr node){
		this.level--; 
	}
	
	
	/* 
	 * In/out let  
	 */
	@Override 
	public void inALetDecl(ALetDecl node){
		this.level++; 
		map.put("Local Variable", node.getExpr()); 
	}
	@Override 
	public void outALetDecl(ALetDecl node){
		this.level++; 
	}
	
	/* 
	 * In/out object 
	 */
	@Override 
	public void inAObjectExpr(AObjectExpr node){
		this.level++; 
	}
	@Override 
	public void outAObjectExpr(AObjectExpr node){
		this.level++; 
	}
	
	/* 
	 * In/out method  
	 */
	@Override
	public void inAFormal (AFormal node){
		this.level++; 
	}
	@Override 
	public void outAFormal(AFormal node){
		this.level++; 
	}
	

}
