package coolc.compiler.visitors;


import java.util.LinkedList;

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
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PExpr;
import coolc.compiler.autogen.node.PFormal;

public class DeclarationVisitor extends DepthFirstAdapter{
	/*
	 * Visitor that will check each declaration in order to fill a Stack with mappings of each type of variable. 
	 * Types: Attribute, parameter, or local variable  
	 */
	
	//attributes, parameters, local values 
	private int level =0; 
	Multimap<String, Node> map= ArrayListMultimap.create(); 
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
		LinkedList <PFormal> fea = node.getFormal(); 
		for( PFormal x : fea){
			map.put("Parameter",x); 
		}
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
		if(getLevel()>2){
			map.put("Local Variable", node.getExpr()); 
		}else{
			map.put("Attribute", node.getExpr()); 
		}
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
		if(getLevel()>2){
			map.put("Local Variable", node.getExpr()); 
		}else{
			map.put("Attribute", node.getExpr()); 
		}
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
		if(getLevel()>2){
			map.put("Local Variable", node); 
		}else{
			map.put("Attribute", node); 
		}
	}
	@Override 
	public void outAFormal(AFormal node){
		this.level++; 
	}
	

}
