package coolc.compiler;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import coolc.compiler.autogen.node.AAssignExpr;
import coolc.compiler.autogen.node.AFormal;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AMinusExpr;
import coolc.compiler.autogen.node.ANoExpr;
import coolc.compiler.autogen.node.AObjectExpr;
import coolc.compiler.autogen.node.AStrExpr;
import coolc.compiler.autogen.node.PFeature;
import coolc.compiler.autogen.node.PFormal;
import coolc.compiler.autogen.node.PExpr;
import coolc.compiler.autogen.node.TObjectId;
import coolc.compiler.autogen.node.TTypeId;
import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.ABranch;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AIntExpr;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PBranch;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.exceptions.SemanticException;
import coolc.compiler.util.Auxiliar;
import coolc.compiler.util.CClass;

import coolc.compiler.util.Error;
import coolc.compiler.util.MySymbolTable;
import coolc.compiler.util.SymbolTable;
import coolc.compiler.util.TableClass;
import coolc.compiler.util.TableSymbol;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.ALetExpr;
import coolc.compiler.autogen.node.AListExpr;
import coolc.compiler.autogen.node.ADivExpr;
import coolc.compiler.autogen.node.ALeExpr;
import coolc.compiler.autogen.node.ALtExpr;
import coolc.compiler.autogen.node.AMultExpr;
import coolc.compiler.autogen.node.ANegExpr;
import coolc.compiler.autogen.node.APlusExpr;
import coolc.compiler.autogen.node.AProgram;
import coolc.compiler.autogen.node.AWhileExpr;
import coolc.compiler.autogen.node.AEqExpr;
import coolc.compiler.autogen.node.ACallExpr;
import coolc.compiler.autogen.node.ACaseExpr;
import coolc.compiler.visitors.ExampleVisitor;
import coolc.compiler.visitors.OtherVisitor;

import coolc.compiler.autogen.node.PLetDecl;



public class CoolSemantic implements SemanticFacade {
	private Map<Node, AClassDecl> types = new HashMap<Node, AClassDecl>();
	private Map<Node, Klass> types2 = new HashMap<Node, Klass>();
	SymbolTable<String, Klass> symbolTable2 = new MySymbolTable<String, Klass>();
	private Map<String, Klass> klasses = new TreeMap<String, Klass>();
	private Klass STR, BOOL, INT, OBJECT, ERROR, VOID, IO;

	private Start start;
	private PrintStream out;
	
	public class Klass{
		public String name;
		Klass parent;
		TreeMap<String, Method> methods = new TreeMap<String,Method>();
		TreeMap<String, Klass> vars = new TreeMap<String,Klass>();
	}
	
	public class Method{
		Klass type;
		TreeMap<String, Klass> vars = new TreeMap<String,Klass>();
	}
	
	class SelfDecl extends Klass{
				private Klass klass;
				SelfDecl(Klass k){
					this.klass = k;
				}	
			}
	//private boolean hasSelfTypeParameterPosition = false;
	
	public class ExampleVisitor2 extends DepthFirstAdapter {
		private boolean hasMain = false;
		private HashMap<String, Boolean> classDeclMap = new HashMap<>();
		AClassDecl currentClass = null;
		String currentMethod = "";
		private LinkedList<String> missingClasses = new LinkedList<>();
		private LinkedList<String> missingMethodReturnTypes = new LinkedList<>();
		String clase = "";
		String metodo = "";
		int depth = 0;
		@Override
		public void inAClassDecl(AClassDecl node) {
			//System.out.println("Placing: " + node.getName().getText());
					symbolTable2.openScope();
					
		}
		@Override
		public void inAAttributeFeature(AAttributeFeature node) {
			try {
				symbolTable2.put(node.getObjectId().getText(), klasses.get(node.getTypeId().getText()));
				}catch(SemanticException e) {
				//e.printStackTrace();
				}
			}
		public void inAMethodFeature(AMethodFeature node){
			symbolTable2.openScope();
		LinkedList<PFormal> params = node.getFormal();
	    	ArrayList<String> repeated = new ArrayList<>();
	    	boolean redefined = false;
	   
			//System.out.println(depth+clase + node.getObjectId().getText());
	    	try {
	    	symbolTable2.closeScope();
	    	if(node.getTypeId().getText().equals(node.getTypeId().getText())) {
	    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.typeNotFound", node.getObjectId().getText(), node.getTypeId().getText());
	    	}
	   
	    	for(int i = 0; i < params.size(); i++){
	    		AFormal p = (AFormal) params.get(i);
	    		if(!repeated.contains(p.getObjectId().getText())){
	    			repeated.add(p.getObjectId().getText());
	    		}else{
	    			redefined = true;
	    		}
	    		if(p.getObjectId().getText().equals("self")){
	    			ErrorManager.getInstance().getErrors().add(Error.SELF_FORMAL);
	    		}
	    	}
	    	if(redefined){
	    		ErrorManager.getInstance().getErrors().add(Error.FORMAL_REDEFINITION);
	    	}
	    	}catch(SemanticException e) {
	    					//e.printStackTrace(); 
	    		
	    		
	    	}
	    //	String myType = node.getTypeId().getText();
			/**String myType = node.getTypeId().getText();
	    		if(!myType.equals("SELF_TYPE")){
		    	if(!TableClass.getInstance().getClasses().containsKey(myType)){
		    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.typeNotFound", node.getObjectId(),node.getTypeId());
		    	}
	    	}
	    	if(!myType.equals("SELF_TYPE")){
		    	if(!TableClass.getInstance().getClasses().containsKey(myType)){
		    		ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
		    	}
	    	}
	    //	ind++;
	    	currentMethod = node.getObjectId().getText();
	    	String cClass = currentClass.getName().getText();

	    	LinkedList<PFormal> params = node.getFormal();
	    	
	    	for(int i = 0; i < params.size(); i++){
	    		AFormal p = (AFormal) params.get(i);**/
			/**if(node.getTypeId().getText().equals("SELF_TYPE")){
				ErrorManager.getInstance().getErrors().add(Error.SELF_TYPE_FORMAL);
				ErrorManager.getInstance().getErrors().add(Error.UNDECL_IDENTIFIER);
				ErrorManager.getInstance().semanticError("Coolc.semant.selfTypeFormal", node.getTypeId().getText());
				//SymbolTable.getInstance().getAuxiliar(ind+cClass+currentMethod).getCertainClass(cClass).getTypes().remove(p.getObjectId().getText());
				
			}**/
	    	
	    }
		/*
		 * outAProgram is the last node visited, so we check here if there was no main method
		 */
		
	


 	
		
		
		@Override
		public void outAFormal(AFormal node) {
			try {
				symbolTable2.put(node.getObjectId().getText(), 
				klasses.get(node.getTypeId().getText()));
			}catch (SemanticException e) {
				//e.printStackTrace();
			}
			if(node.getTypeId().getText().equals("SELF_TYPE")) {
				ErrorManager.getInstance().getErrors().add(Error.SELF_TYPE_FORMAL);
				ErrorManager.getInstance().semanticError("Coolc.semant.selfTypeFormal", node.getObjectId());
			}
		}
		
		@Override
		public void outAListExpr(AListExpr node) {
			types2.put(node, types2.get(node.getExpr().getLast()));
		}
		
		@Override
		public void outAProgram(AProgram node) {
			if(!hasMain){
	    		ErrorManager.getInstance().getErrors().add(Error.NO_MAIN);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.noMain");
			}
			
			for(int i = 0; i < missingClasses.size(); i += 2) {
				String className = missingClasses.get(i);
				String inheritClass = missingClasses.get(i+1);
				if( classDeclMap.get(inheritClass) == null || classDeclMap.get(inheritClass) == false ) {
					ErrorManager.getInstance().getErrors().add(Error.CANNOT_INHERIT);
					ErrorManager.getInstance().semanticError("Coolc.semant.undefined", className, inheritClass);
				}
			}
			
			for( int i = 0; i < missingMethodReturnTypes.size(); i += 2 ) {
				String className = missingMethodReturnTypes.get(i);
				String returnType = missingMethodReturnTypes.get(i+1);
				if ( classDeclMap.get(returnType) == null || classDeclMap.get(returnType) == false ) {
					ErrorManager.getInstance().getErrors().add(Error.TYPE_NOT_FOUND);
					ErrorManager.getInstance().semanticError("Coolc.semant.typeNotFound", className, returnType);
				} 
			}
			
			
			classDeclMap = new HashMap<>();
			missingClasses = new LinkedList<>();
			missingMethodReturnTypes = new LinkedList<>();
		}
		
		 public void outALetExpr(ALetExpr node){
			    
				// types.put(node, basicClasses().get(key))
				 //ind--;
			    	LinkedList<PLetDecl> params = node.getLetDecl();
			    	for(int i = 0; i < params.size(); i++){
			    		ALetDecl p = (ALetDecl) params.get(i);
			    		if(p.getObjectId().getText().equals("self")){
			    			ErrorManager.getInstance().getErrors().add(Error.SELF_IN_LET);
			    			ErrorManager.getInstance().semanticError("Coolc.semant.selfInLet");
			    			//types.put(node, basicClasses().get("Let"));
			    			
			    		}
			    	}
				  
			  }
		 
		@Override
		public void outAMethodFeature(AMethodFeature node) {
			//System.out.println("Checking in out: " + node.getName().getText());
			if( !(node.getTypeId().getText().equals("Object") || node.getTypeId().getText().equals("Bool") || node.getTypeId().getText().equals("Int") || node.getTypeId().getText().equals("String")) ) {
				missingMethodReturnTypes.add(node.getObjectId().getText());
				missingMethodReturnTypes.add(node.getTypeId().getText());
			}	
		}

		
		
		/*
		 * When visiting every Class, we check if it is called Main.
		 */
	
		
		public void outAClassDecl(AClassDecl node){
			try {
				symbolTable2.closeScope();
			}catch(SemanticException e){
				//e.printStackTrace();
			}
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
			
			if( classDeclMap.get(node.getName().getText()) == null ||  classDeclMap.get(node.getName().getText()) == false ) {
				classDeclMap.put(node.getName().getText(), true);
			} else {
				ErrorManager.getInstance().getErrors().add(Error.REDEFINED);
				ErrorManager.getInstance().semanticError("Coolc.semant.redefined", node.getName().getText());
			}
			
			//System.out.println("Checking in out: " + node.getName().getText());
			if( !(node.getInherits().getText().equals("Object") || node.getInherits().getText().equals("Bool") || node.getInherits().getText().equals("Int") || node.getInherits().getText().equals("String")) ) {
				missingClasses.add(node.getName().getText());
				missingClasses.add(node.getInherits().getText());
			}
			
			
		}
		
		public void outAObjectExpr(AObjectExpr node) {
 			try {
 				Klass k = symbolTable2.get(node.getObjectId().getText());
 				types2.put(node, k);
 			}catch (SemanticException e){
 				//e.printStackTrace();
 			}
 		}
		public void outAAttributeFeature(AAttributeFeature node){
			
	        if(node.getObjectId().getText().equals("self")){
	        	
	        		ErrorManager.getInstance().getErrors().add(Error.SELF_ATTR);
	        		ErrorManager.getInstance().semanticError("Coolc.semant.selfAttr");
	        }
		}
		
		public void outAAssignExpr(AAssignExpr node){
	    	String s = node.getObjectId().getText();
	    	if(s.equals("self")){
	    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
	    		ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf");
	    		return;
	    		}
		}
		
		 public void inACaseExpr(ACaseExpr node){
		        depth++;
		        LinkedList<PBranch> params = node.getBranch();
		    	boolean redefined = false;
		    	ArrayList<String> repeated = new ArrayList<>();
		    	
		    	Auxiliar vartemp = new Auxiliar(depth+clase+metodo, new ArrayList<CClass>());
		    	CClass curry = new CClass(clase,new HashMap<String,TTypeId>());
		   TableSymbol.getInstance().getVariables().add(vartemp);
				TableSymbol.getInstance().getAuxiliar(depth+clase+metodo).getLista().add(curry);
				for(int i = 0; i < params.size(); i++){
		    		ABranch p = (ABranch) params.get(i);
		    		if(!repeated.contains(p.getTypeId().getText())){
		    			repeated.add(p.getTypeId().getText());
		    		}else{
		    			redefined = true;
		    		}
		    	}
		    	if(redefined){
		    		ErrorManager.getInstance().getErrors().add(Error.DUPLICATE_BRANCH);
		    	}else{
			    	for(int i = 0; i < params.size(); i++){
			    		ABranch p = (ABranch) params.get(i);
			    		TableSymbol.getInstance().getAuxiliar(depth+clase+metodo).getCertainClass(clase).getTypes().put(p.getObjectId().getText(), p.getTypeId());
			    	}
		    	}
		    }
		    public void outACaseExpr(ACaseExpr node){
		        depth--;
		    }
		
	}

	class Pass1 extends DepthFirstAdapter {
		int ind = 0;
		AClassDecl currentClass;
		String currentMethod="";
		String clase = "";
		String hereda = "";
		boolean need = false;
		int indice = 0;
		
		
	    
	    

		 public void outAWhileExpr(AWhileExpr node){
		    	if(!node.getTest().toString().contains("Bool")){
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_LOOP);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.badLoop");
		    		//node.setType("minor");
		    	}else{
		    		//node.setType("Object");
		    	}
		   }
		 public boolean isSubType3(String obj, String second){
		    	if(second.contains(obj)){
		    		return true;
		    		}
		    	while(!second.contains("Object")){
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
		 public void inAMethodFeature(AMethodFeature node){
		    	ind++;
		    	currentMethod = node.getObjectId().getText();
		    //	String cClass = currentClass.getName().getText();

		    	LinkedList<PFormal> params = node.getFormal();
		    	
		    	for(int i = 0; i < params.size(); i++){
		    		AFormal p = (AFormal) params.get(i);
		    		if(p.getTypeId().getText().equals("SELF_TYPE")){
		    			ErrorManager.getInstance().getErrors().add(Error.SELF_TYPE_FORMAL);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.selfTypeFormal",p.getTypeId().getText());
		    			//TableSymbol.getInstance().getAuxiliar(ind+cClass+currentMethod).getCertainClass(cClass).getTypes().remove(p.getObjectId().getText());
		    			
		    		}
		    		}
		    }
		    public void outAMethodFeature(AMethodFeature node){
		    	ind--;
		    	currentMethod = "";
		    	
		    	if(node.getTypeId().getText().equals("SELF_TYPE")){
		    		if(!node.getExpr().toString().contains("SELF_TYPE")){
		    			ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.badInferred", node.getTypeId().getText(), node.getExpr().toString(),"SELF_TYPE");
		    		}
		    		return;
		    	}
		    		if(ErrorManager.getInstance().getErrors().size() == 0){
		    		String mType = node.getTypeId().getText();
		    		String eType = node.getExpr().toString();
		    				    		if(!isSubType2(mType, eType)){
		    			ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.badInferred", mType, eType,mType);
		    		}
		    	}
		    }
		    
		 public void outAObjectExpr(AObjectExpr node){
		    	boolean exists = false;
				if(node.getObjectId().getText().equals("self")){
				//	node.setType("SELF_TYPE of " + currentClass.getName().getText());
					return;
				}
		    	for(int i = ind; i > -1; i--){
		    		if(i == 0){
		            	AClassDecl cAux = currentClass;
		            	String aux;
		            	String lastAux="";
		            	if(cAux == null) {
		            		aux=lastAux;
		            	}else {
		            		aux = cAux.getName().getText();
		            		lastAux=aux;
		            	}
		            	while(!aux.equals("Object")){
		            		
		            		if (TableSymbol.getInstance().getAuxiliar(i+aux).getCertainClass(aux) == null) {
		            			aux = "ni idea";
		            			break;
		            		}else {
		            		if(TableSymbol.getInstance().getAuxiliar(i+aux).getCertainClass(aux).getTypes().containsKey(node.getObjectId().getText())){
		                    		
		            			//node.setType(TableSymbol.getInstance().getAuxiliar(i+aux).getCertainClass(aux).getTypes().get(node.getObjectId().getText()).getText());
		                    
		            			
		                    					
		                            			exists = true;
		                    					
		                    				}
		            		if(exists){
		            			break;
		            		}
		            		aux = cAux.getInherits().getText();
		            		cAux = TableClass.getInstance().getClasses().get(aux);
		            		}
		            	}
		    		}else{
		            	AClassDecl cAux = currentClass;
		            	String aux;
		            	String lastAux="";
		            	if(cAux == null) {
		            		aux=lastAux;
		            	}else {
		            		aux = cAux.getName().getText();
		            		lastAux=aux;
		            	}
		            	while(!aux.equals("Object")){
		            		
		            		Auxiliar mVars = TableSymbol.getInstance().getAuxiliar(i+aux+currentMethod);
		            		if(mVars != null){
		            			CClass vars = mVars.getCertainClass(aux);
			            		if(vars != null && vars.getTypes().containsKey(node.getObjectId().getText())){
			            			
			            			
			            			//node.setType(TableSymbol.getInstance().getAuxiliar(i+aux+currentMethod).getCertainClass(aux).getTypes()
			            			//		.get(node.getObjectId().getText()).getText());
			            			

				            		exists = true;
			            		}
		            		}
		            		if(exists){
		            			break;
		            		}
		            	 	if(cAux == null) {
		            	 		aux= "ni idea";
		            	 		break;
		            	 	}else {
		            	 		aux = cAux.getInherits().getText();
			            		
		            	 	}
		            	 	cAux = TableClass.getInstance().getClasses().get(aux);
		            	}
		    		}
		    		if(exists){
		    			break;
		    		}
		    	}
		    	if(!exists){
		    		ErrorManager.getInstance().getErrors().add(Error.UNDECL_IDENTIFIER);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.undeclIdentifier",node.getObjectId().getText());
		    		//node.setType("minor");
		    		return;
		    	}else{
		    		if(node.toString().equals("SELF_TYPE")){
		    			//node.setType("SELF_TYPE of " + currentClass.getName().getText());
		    		}
		    	}
		    }
		 public void outAEqExpr(AEqExpr node){
		    	if(!node.getL().toString().contains("Bool") | !node.getR().toString().contains("Bool")){
		    		ErrorManager.getInstance().getErrors().add(Error.BASIC_COMPARE);
	    			ErrorManager.getInstance().semanticError("Coolc.semant.basicCompare");
		    	}else {
		    		types.put(node, basicClasses().get("Bool"));
		    	}
		    	if(!node.getL().toString().contains("String") | !node.getR().toString().contains("String")) {
		    		ErrorManager.getInstance().getErrors().add(Error.BASIC_COMPARE);
	    			ErrorManager.getInstance().semanticError("Coolc.semant.basicCompare");
		    	}else {
		    		types.put(node, basicClasses().get("String"));
		    		}
		    }
		 
		  public void outAAtExpr(AAtExpr node){
			  
				String theClass = node.getExpr().toString();
				if(theClass.contains("SELF_TYPE")){
					theClass = currentClass.getName().getText();
					//ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
					//TTypeId h=new TTypeId("major");
					//node.setTypeId(h);
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
		    	boolean found = false;
				AMethodFeature theMethod = null;
				String f = node.getObjectId().getText();
				while(!theClass.equals("Object")){
					if(TableSymbol.getInstance().getMethod(theClass).getFeatures().containsKey(f)){
						theMethod = TableSymbol.getInstance().getMethod(theClass).getFeatures().get(f);
						found = true;
						break;
					}
					AClassDecl aux = TableClass.getInstance().getClasses().get(theClass);
					if(aux==null) {
						break;
					}else {
						theClass =aux.getInherits().getText() ;
					}
					
				}
				if(!found){
					theMethod = TableSymbol.getInstance().getMethod("Object").getFeatures().get(f);
				}
				if(theMethod != null){
					LinkedList<PExpr> tries = node.getList();
					LinkedList<PFormal> params = theMethod.getFormal();
					if(tries.size() != params.size()){
						ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
						ErrorManager.getInstance().semanticError("Coolc.semant.formalsFailedLong",tries.getFirst().toString(),params.getFirst().toString(), tries.getLast().toString());
						//node.setType("minor");
						return;
					}else{
						for(int i = 0; i < params.size(); i++){
							String t = tries.get(i).toString();
							AFormal pf = (AFormal) params.get(i);
							String p = pf.getTypeId().getText();
							/**if(t.contains("SELF_TYPE")){
								String[] parts = t.split(" ");
								t = parts[parts.length - 1];
							}**/
							if(!isSubType3(p, t)){
								ErrorManager.getInstance().getErrors().add(Error.FORMALS_FAILED_LONG);
								ErrorManager.getInstance().semanticError("Coolc.semant.formalsFailedLong",t,p,p);
								//node.setType("minor");
								return;
							}
						}
						if(theMethod.getTypeId().getText().equals("SELF_TYPE")){
							theClass = node.getTypeId().toString();
							if(theClass.contains("SELF_TYPE")){
								theClass = currentClass.getName().getText();
							}
							//node.setType(theClass);
						}else{
							//node.setType(theMethod.getTypeId().getText());
						}
					}
				}else{
					ErrorManager.getInstance().getErrors().add(Error.DISPATCH_UNDEFINED);
					ErrorManager.getInstance().semanticError("Coolc.semant.dispatchUndefined",node.getExpr().toString());
					//node.setType("minor");
					return;
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
		
		  
			public boolean isSubType2(String obj, String second){
		    	if(obj.contains(second)){
		    		return true;
		    		}
		    	while(!second.contains("Object")){
		    		AClassDecl cClass = TableClass.getInstance().getClasses().get(second);
		    		
		    		if(cClass==null) {
		    			return false;
		    		}else {
		    			second = cClass.getInherits().getText();
		    		}
		    			//second = cClass.getInherits().getText();
		    		
		    		if(second.equals(obj))
		    			return true;
		    		}
		    		
		    	return false;
		    }
		//assignnoconform.cool
		//TestBad 2
		 public void outAAssignExpr(AAtExpr node){

		    	String s = node.getTypeId().toString();
		    	String Obj = node.getObjectId().toString();
		 
		    	if(s.equals("self")){
		    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf", s);
		    		//ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
		    		//TTypeId h=new TTypeId("major");
				//node.setTypeId(h);
		    		return;
		    	}
		    	/**assignnoconform.cool
				//TestBad 2**/
		    	if(isSubType(s, node.toString())){
		    		//node.setType(node.getExpr().getTypeAsString());
		    	}else{
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_ASSIGNMENT);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.badAssignment", node.toString(), s, s);
		    		
		    		//node.setType("minor");
		    	}
		    }
		 public void outAAssignExpr(AAssignExpr node){
		    	String s = node.getObjectId().getText();
		    	
		   String typeObj = node.getExpr().toString();
		 
		    	if(s.contains("self")){
		    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf",s);
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.badInferred");
		    		//node.setType("major");
		    		return;
		    	}
		    	/**assignnoconform.cool
				//TestBad 2**/
		    	if(isSubType(typeObj, node.toString())){
		    		//node.setType(node.getExpr().getTypeAsString());
		    	}else{
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_ASSIGNMENT);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.badAssignment", node.toString(), typeObj, node.getObjectId().toString());
		    		//node.setType("minor");
		    	}
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
		    		String s = node.getExpr().toString();
		    		if(!isSubType3(node.getTypeId().getText(), s)){
		    			ErrorManager.getInstance().getErrors().add(Error.BAD_LET_INIT);
		    			ErrorManager.getInstance().semanticError("Coolc.semant.badLetInit");
		    		}
	    		}
	    	}
	    	
	    	
	    		types = new HashMap<Node, AClassDecl>();
	    }
		
		
	    

	}
	
	class P2 extends DepthFirstAdapter{
		
		
		
		
		LinkedList<PFeature> featList= new LinkedList<PFeature>();
		AClassDecl error = new AClassDecl(
				new TTypeId("error"),
				new TTypeId("_no_class"),
				featList
				);
		
		public void outAMinusExpr(AMinusExpr node){
		
			/**badarith.cool
			//TestBad 6**/
			if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
	    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
	    			types.put(node,error);
	    			return;
	    		//node.setType("minor");
	    		}else{
	    		//node.setType("Int");
	    		}
			
			types.put(node, basicClasses().get("Int"));
	    }
		
		public void outAPlusExpr(APlusExpr node){
			
			/**badarith.cool
			//TestBad 6**/
			if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
    			types.put(node,error);
    			return;
    		//node.setType("minor");
    		}else{
    		//node.setType("Int");
    		}
		
		types.put(node, basicClasses().get("Int"));
	    }

	    public void outAMultExpr(AMultExpr node){
	    	/**badarith.cool
			//TestBad 6**/
	    	if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
    			types.put(node,error);
    			return;
    		//node.setType("minor");
    		}else{
    		//node.setType("Int");
    		}
		
		types.put(node, basicClasses().get("Int"));
	    }

	    public void outADivExpr(ADivExpr node){
	    	/**badarith.cool
			//TestBad 6**/
	    	if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
    			types.put(node,error);
    			return;
    		//node.setType("minor");
    		}else{
    		//node.setType("Int");
    		}
		
		types.put(node, basicClasses().get("Int"));
	    }

	    public void outALtExpr(ALtExpr node){
	    	/**badarith.cool
			//TestBad 6**/
	    	if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
    			types.put(node,error);
    			return;
    		//node.setType("minor");
    		}else{
    		//node.setType("Int");
    		}
		
		types.put(node, basicClasses().get("Int"));
	    }
	    
	    public void outALeExpr(ALeExpr node){	
	    	/**badarith.cool
			//TestBad 6**/
	    	if(!node.getL().toString().contains("Int") || !node.getR().toString().contains("Int")){
    			ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
    			ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getL().toString());
    			types.put(node,error);
    			return;
    		//node.setType("minor");
    		}else{
    		//node.setType("Int");
    		}
		
		types.put(node, basicClasses().get("Int"));
    }

	    public void outANegExpr(ANegExpr node){
	    	
	    	if(node.getExpr().toString().contains("Int")){
	    		//node.setType("Int");
	    	}else{
	    		/**badarith.cool
				//TestBad 6**/
	    		ErrorManager.getInstance().getErrors().add(Error.NOT_INT_PARAMS);
	    		ErrorManager.getInstance().semanticError("Coolc.semant.notIntParams", node.getExpr().toString());
	    		//node.setType("minor");
	    	}
	    	types = new HashMap<Node, AClassDecl>();
	    }
	    
	    public void outAEqExpr(AEqExpr node){
	    	String h="";
	    	String h2="";
	    	if(node.getL().toString().contains("Int") ||
	    			node.getL().toString().contains("Bool") ||
	    			node.getL().toString().contains("String")){
	    		if(node.getL().toString().contains("Int")) {
	    			h="Int";
	    		}else if (node.getL().toString().contains("Bool")) {
	    			h="Bool";
	    		}else {
	    			 h="String";
	    		}
	    		
	    		if(node.getR().toString().contains("Int")) {
	    			h2="Int";
	    		}else if (node.getR().toString().contains("Bool")) {
	    			h2="Bool";
	    		}else {
	    			 h2="String";
	    		}
	    		
	    		
	  
	    	
	    		if(!h.equals(h2)){
	    			ErrorManager.getInstance().getErrors().add(Error.BASIC_COMPARE);
	    			ErrorManager.getInstance().semanticError("Coolc.semant.basicCompare");
	    			//node.setType("minor");
	    			return;
	    		}else{
	    			//node.setType("Bool");
	    		}
	    	}else{
	    		//node.setType("Bool");
	    	}
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
		basicClasses();
		
		start.apply(new OtherVisitor());
		
		start.apply(new ExampleVisitor());
	
		
		start.apply(new Pass1());
		
		
		start.apply(new P2());
		
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
	public Map<Node, Klass> getTypes() {
		return this.types2;
	}
	
	public Map<Node, AClassDecl> getTypesACl() {
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
				OBJECT = new Klass();
				OBJECT.name = "Object";
				OBJECT.parent = null;
				OBJECT.methods = null;
		 		OBJECT.vars = null;
		 		
		 		IO = new Klass();

		 		
		 		INT = new Klass();
		 		INT.name = "Int";

		 		
		 		BOOL = new Klass();

				
		 		STR = new Klass();
				STR.name ="String";
				STR.parent = OBJECT;

				
				ERROR = new Klass();

				
				
				VOID = new Klass();

				klasses.put("Object", OBJECT);
		
				klasses.put("Int", INT);
			klasses.put("String", STR);
				
		map.put("Object", ObjectClass);
		map.put("IO", IOClass);
		map.put("Int", IntClass);
		map.put("Bool", BoolClass);
		map.put("String", StringClass);
		
		return map;
		
	}

}
