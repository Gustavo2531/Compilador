package coolc.compiler;

import java.io.PrintStream;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AMinusExpr;
import coolc.compiler.autogen.node.ANoExpr;
import coolc.compiler.autogen.node.AStrExpr;
import coolc.compiler.autogen.node.PFeature;
import coolc.compiler.autogen.node.PFormal;
import coolc.compiler.autogen.node.TObjectId;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AIntExpr;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.exceptions.SemanticException;

import coolc.compiler.util.Error;
import coolc.compiler.util.TableClass;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.ALetExpr;
import coolc.compiler.autogen.node.ADivExpr;
import coolc.compiler.autogen.node.ALeExpr;
import coolc.compiler.autogen.node.ALtExpr;
import coolc.compiler.autogen.node.AMultExpr;
import coolc.compiler.autogen.node.ANegExpr;
import coolc.compiler.autogen.node.APlusExpr;
import coolc.compiler.visitors.ExampleVisitor;
import coolc.compiler.visitors.OtherVisitor;
import coolc.compiler.autogen.node.PLetDecl;



public class CoolSemantic implements SemanticFacade {
	private Map<Node, AClassDecl> types = new HashMap<Node, AClassDecl>();
	private Start start;
	private PrintStream out;
	//private boolean hasSelfTypeParameterPosition = false;
	
	class Pass1 extends DepthFirstAdapter {
		int ind = 0;
		AClassDecl currentClass;
		  public void outAAtExpr(AAtExpr node){
			  
				String theClass = node.getTypeId().toString();
				if(theClass.contains("SELF_TYPE")){
					theClass = currentClass.getName().getText();
				}
		    	if(node.getTypeId() != null){
		    		String other = node.getTypeId().getText();
		    		if(!isSubType(other, theClass)){
		    			ErrorManager.getInstance().getErrors().add(Error.STATIC_FAIL_TYPE);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.staticFailType", node.getExpr().toString(),node.getTypeId().toString());
		    			if(isSubType(theClass, other)){
		    				//types.put(node, minor);
		    			}else{
		    				//node.setType("major");
		    			}
		    			return;
		    		}
		    	}
		  }
		  
		  
		public boolean isSubType(String obj, String second){
	    	if(obj.equals(second)){
	    		return true;
	    		}
	    	while(!second.equals("Object")){
	    		AClassDecl cClass = TableClass.getInstance().getClasses().get(second);
	    		
	    		if(cClass==null) {
	    			return false;
	    		}else {
	    			second = cClass.getInherits().getText();
	    		}
	    		if(second.equals(obj))
	    			return true;
	    		}
	    		
	    	return false;
	    }
		
		 public void outAAssignExpr(AAtExpr node){
			
		    	String s = node.getTypeId().toString();
		    	String Obj = node.getObjectId().toString();
		 
		    	if(s.equals("self")){
		    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf");
		    		return;
		    	}
		    	if(isSubType(s, types.get(node.getExpr()).getName().toString())){
		    		//node.setType(node.getExpr().getTypeAsString());
		    	}else{
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_ASSIGNMENT);
		    		
		    		//node.setType("minor");
		    	}
		    }
		public void outAMinusExpr(AMinusExpr node){
			AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		
	    		//node.setType("minor");
	    	}else{
	    		//node.setType("Int");
	    		}
	    types = new HashMap<Node, AClassDecl>();
	    }
		
		public void inALetExpr(ALetExpr node){
	        ind++;
	    }

	    public void outALetExpr(ALetExpr node){
	    	ind--;
	    	LinkedList<PLetDecl> params = node.getLetDecl();
	    	for(int i = 0; i < params.size(); i++){
	    		ALetDecl p = (ALetDecl) params.get(i);
	    		if(p.getObjectId().getText().equals("self")){
	    			ErrorManager.getInstance().getErrors().add(Error.SELF_IN_LET);
	    			ErrorManager.getInstance().semanticError("Coolc.semant.selfInLet");
	    			//node.setType("minor");
	    			return;
	    		}
	    	}
		    //String myType = types.get(node.getExpr()).getName().getText();
		    //types = new HashMap<Node, AClassDecl>();
		    //node.setType(myType);
	    }

	    public void outALetDecl(ALetDecl node){
	    	if(node.getExpr() != null){
	    		if(!node.getTypeId().getText().equals("SELF_TYPE")){
	    			
		    		String s = (types.get(node.getExpr())).getInherits().getText();
		    		if(!isSubType(node.getTypeId().getText(), s)){
		    			ErrorManager.getInstance().getErrors().add(Error.BAD_LET_INIT);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.badLetInit");
		    		}
	    		}
	    		
	    		
	    		}
	    		types = new HashMap<Node, AClassDecl>();
	    }
		
		public void outAPlusExpr(APlusExpr node){
			AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		//node.setType("minor");
	    	}else{
	    		//node.setType("Int");
	    	}
	    	types = new HashMap<Node, AClassDecl>();
	    }

	    public void outAMultExpr(AMultExpr node){
	    	AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		
	    	}else{
	    	
	    		}
	    	types = new HashMap<Node, AClassDecl>();
	    }

	    public void outADivExpr(ADivExpr node){
	    	AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		
	    		
	    	}else{
	    		
	    	}
	    	types = new HashMap<Node, AClassDecl>();
	    }

	    public void outALtExpr(ALtExpr node){
	    	AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		//node.setType("minor");
	    	}else{
	    		//node.setType("Bool");
	    	}
	    	types = new HashMap<Node, AClassDecl>();
	    }

	    public void outALeExpr(ALeExpr node){
	     	AClassDecl c;
			AClassDecl c2;
			c=types.get(node.getL());
			c2=types.get(node.getR());
	    	if(!c.getName().toString().equals("Int") ||
	    			!c2.getName().toString().equals("Int")){
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    		//node.setType("minor");
	    	}else{
	    		//node.setType("Bool");
	    		}
	    		
	    }

	    public void outANegExpr(ANegExpr node){
	    	AClassDecl c;
			c=types.get(node.getExpr());
	    	if(c.getName().toString().equals("Int")){
	    		//node.setType("Int");
	    	}else{
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getExpr().toString());
	    		//node.setType("minor");
	    	}
	    	types = new HashMap<Node, AClassDecl>();
	    }
	    

	}
	
	class TypeChecker extends DepthFirstAdapter {
		public void outAIntExpr(AIntExpr node){
	        types.put(node, basicClasses().get("Int"));
	    }
		
		public void outAStrExpr(AStrExpr node){
	        types.put(node, basicClasses().get("String"));
	    }
		
	}
	
	
	@Override
	public void setup(Start start, PrintStream out) {
		this.start = start;
		this.out = out;
	}

	
	@Override
	public void check() throws SemanticException {
		start.apply(new ExampleVisitor());
		
		if(ErrorManager.getInstance().getErrors().size() > 0){
			throw new SemanticException();
		}
		
		start.apply(new Pass1());
		if(ErrorManager.getInstance().getErrors().size() > 0){
			throw new SemanticException();
		}
		
	
		
		
		
		
		start.apply(new TypeChecker());
	}

	@Override
	public Set<Error> getErrors() {
		return ErrorManager.getInstance().getErrors();
	}

	@Override
	public Map<Node, AClassDecl> getTypes() {
		return this.types;
	}
	
	public Map<String, AClassDecl> basicClasses() {
		// The following demonstrates how to create dummy parse trees to
		// refer to basic Cool classes. There's no need for method
		// bodies -- these are already built into the runtime system.

		// IMPORTANT: The results of the following expressions are
		// stored in local variables. You will want to do something
		// with those variables at the end of this method to make this
		// code meaningful.

		// The Object class has no parent class. Its methods are
		// cool_abort() : Object aborts the program
		// type_name() : Str returns a string representation
		// of class name
		// copy() : SELF_TYPE returns a copy of the object
		LinkedList<PFeature> featList;
		LinkedList<PFormal> formalList;
		PFeature pf;
		
		featList = new LinkedList<PFeature>();
		
		pf = new AMethodFeature(
				new TObjectId("abort"),
				new LinkedList<PFormal>(),
				new TTypeId("Object"),
				new ANoExpr()
				);		
		featList.add(pf);
		
		pf = new AMethodFeature(
				new TObjectId("type_name"),
				new LinkedList<PFormal>(),
				new TTypeId("String"),
				new ANoExpr()
				);
		featList.add(pf);
		
		pf = new AMethodFeature(
				new TObjectId("copy"),
				new LinkedList<PFormal>(),
				new TTypeId("SELF_TYPE"),
				new ANoExpr()
				);
		featList.add(pf);
		
		AClassDecl ObjectClass = new AClassDecl(
				new TTypeId("Object"),
				new TTypeId("_no_class"),
				featList
				);
		
		// The IO class inherits from Object. Its methods are
		// out_string(Str) : SELF_TYPE writes a string to the output
		// out_int(Int) : SELF_TYPE "    an int    " "     "
		// in_string() : Str reads a string from the input
		// in_int() : Int "   an int     " "     "
		featList = new LinkedList<PFeature>();
		
		formalList = new LinkedList<PFormal>();		
		formalList.add(new AFormal(new TObjectId("arg"), new TTypeId("String")));		
		pf = new AMethodFeature(
				new TObjectId("out_string"),
				formalList,
				new TTypeId("SELF_TYPE"),
				new ANoExpr()
				);		
		featList.add(pf);

		formalList = new LinkedList<PFormal>();		
		formalList.add(new AFormal(new TObjectId("arg"), new TTypeId("Int")));
		pf = new AMethodFeature(
				new TObjectId("out_int"),
				formalList,
				new TTypeId("SELF_TYPE"),
				new ANoExpr()
				);		
		featList.add(pf);

		formalList = new LinkedList<PFormal>();
		pf = new AMethodFeature(
				new TObjectId("in_string"),
				formalList,
				new TTypeId("String"),
				new ANoExpr()
				);		
		featList.add(pf);

		pf = new AMethodFeature(
				new TObjectId("in_int"),
				formalList,
				new TTypeId("Int"),
				new ANoExpr()
				);		
		featList.add(pf);

		AClassDecl IOClass = new AClassDecl(
				new TTypeId("IO"),
				new TTypeId("Object"),
				featList
				);


		// The Int class has no methods and only a single attribute, the
		// "val" for the integer.
		featList = new LinkedList<PFeature>();
		pf = new AAttributeFeature(
				new TObjectId("_val"),
				new TTypeId("_prim_slot"),
				new ANoExpr()
				);		
		featList.add(pf);
		
		AClassDecl IntClass = new AClassDecl(
				new TTypeId("Int"),
				new TTypeId("Object"),
				featList
				);

		// Bool also has only the "val" slot.
		featList = new LinkedList<PFeature>();
		pf = new AAttributeFeature(
				new TObjectId("_val"),
				new TTypeId("_prim_slot"),
				new ANoExpr()
				);		
		featList.add(pf);
		
		AClassDecl BoolClass = new AClassDecl(
				new TTypeId("Bool"),
				new TTypeId("Object"),
				featList
				);

		// The class Str has a number of slots and operations:
		// val the length of the string
		// str_field the string itself
		// length() : Int returns length of the string
		// concat(arg: Str) : Str performs string concatenation
		// substr(arg: Int, arg2: Int): Str substring selection
		featList = new LinkedList<PFeature>();
		pf = new AAttributeFeature(
				new TObjectId("_val"),
				new TTypeId("_prim_slot"),
				new ANoExpr()
				);		
		featList.add(pf);

		pf = new AAttributeFeature(
				new TObjectId("_str_field"),
				new TTypeId("_prim_slot"),
				new ANoExpr()
				);		
		featList.add(pf);
		
		pf = new AMethodFeature(
				new TObjectId("length"),
				new LinkedList<PFormal>(),
				new TTypeId("Int"),
				new ANoExpr()
				);
		featList.add(pf);		

		formalList = new LinkedList<PFormal>();
		formalList.add(new AFormal(new TObjectId("arg"), new TTypeId("String")));
		pf = new AMethodFeature(
				new TObjectId("concat"),
				formalList,
				new TTypeId("String"),
				new ANoExpr()
				);		
		featList.add(pf);

		formalList = new LinkedList<PFormal>();
		formalList.add(new AFormal(new TObjectId("arg"), new TTypeId("Int")));
		formalList.add(new AFormal(new TObjectId("arg2"), new TTypeId("Int")));
		pf = new AMethodFeature(
				new TObjectId("substr"),
				formalList,
				new TTypeId("String"),
				new ANoExpr()
				);		
		featList.add(pf);
		
		
		AClassDecl StringClass = new AClassDecl(
				new TTypeId("String"),
				new TTypeId("Object"),
				featList
				);
		

		/*
		 * Do something with Object_class, IO_class, Int_class, Bool_class, and
		 * Str_class here 
		 * 
		 */

		Map<String, AClassDecl> map = new TreeMap<String, AClassDecl>();
		map.put("Object", ObjectClass);
		map.put("IO", IOClass);
		map.put("Int", IntClass);
		map.put("Bool", BoolClass);
		map.put("String", StringClass);
		
		return map;
		
	}

}
