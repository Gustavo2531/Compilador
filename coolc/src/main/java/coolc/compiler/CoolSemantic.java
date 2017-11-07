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
import coolc.compiler.autogen.node.ANoExpr;
import coolc.compiler.autogen.node.AStrExpr;
import coolc.compiler.autogen.node.PFeature;
import coolc.compiler.autogen.node.PFormal;
import coolc.compiler.autogen.node.TObjectId;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
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
import coolc.compiler.visitors.ExampleVisitor;
import coolc.compiler.autogen.node.PLetDecl;



public class CoolSemantic implements SemanticFacade {
	private Map<Node, AClassDecl> types = new HashMap<Node, AClassDecl>();
	private Start start;
	private PrintStream out;
	

	class TypeChecker extends DepthFirstAdapter {
		public void outAIntExpr(AIntExpr node){
	        types.put(node, basicClasses().get("Int"));
	    }
		
		public void outAStrExpr(AStrExpr node){
	        types.put(node, basicClasses().get("String"));
	    }
		
		

		public void inAMethodFeature(AMethodFeature node){
		    //	String myType = node.getTypeId().getText();
				String myType = node.getTypeId().getText();
		    		if(!myType.equals("SELF_TYPE")){
			    	if(!TableClass.getInstance().getClasses().containsKey(myType)){
			    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
			    		ErrorManager.getInstance().semanticError("Coolc.semant.typeNotFound", node.getObjectId(),node.getTypeId());
			    	}
		    	}
		}
	
		public void inAClassDecl(AClassDecl node) {
			
			
			
			
			if(!TableClass.getInstance().getClasses().containsKey(node.getInherits().getText())){
				if(!node.getInherits().getText().equals("Object")){
					ErrorManager.getInstance().getErrors().add(Error.CANNOT_INHERIT);
					ErrorManager.getInstance().semanticError("Coolc.semant.cannotInherit",node.getName().getText());
				}
			}
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
		
		this.start.apply(new TypeChecker());
		//if(ErrorManager.getInstance().getErrors().size() > 0){
			//throw new SemanticException();
		//}
		/**start.apply(new NewVisitor(out));
		

		
		// The visitors may have added erros to the set
		if(ErrorManager.getInstance().getErrors().size() > 0){
			throw new SemanticException();
		}**/
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
