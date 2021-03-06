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

import javax.print.DocFlavor.STRING;

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
import coolc.compiler.autogen.node.ABoolExpr;
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
import coolc.compiler.autogen.node.ANewExpr;
import coolc.compiler.autogen.node.APlusExpr;
import coolc.compiler.autogen.node.AProgram;
import coolc.compiler.autogen.node.AWhileExpr;
import coolc.compiler.autogen.node.AEqExpr;
import coolc.compiler.autogen.node.ACallExpr;
import coolc.compiler.autogen.node.ACaseExpr;
import coolc.compiler.visitors.ExampleVisitor;
import coolc.compiler.visitors.InhVisitor;
import coolc.compiler.visitors.OtherVisitor;

import coolc.compiler.autogen.node.PLetDecl;



public class CoolSemantic implements SemanticFacade {
	private Map<Node, AClassDecl> types = new HashMap<Node, AClassDecl>();
	private Map<Node, Klass> types2 = new HashMap<Node, Klass>();
	SymbolTable<String, Klass> symbolTable = new MySymbolTable<String, Klass>();
	private Map<String, Klass> klasses = new TreeMap<String, Klass>();
	private Klass STR, BOOL, INT, OBJECT, ERROR, VOID, IO;

	private Start start;
	private PrintStream out;
	
	public class Klass{
		public String name;
		public Klass parent;
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
	
	class ScopePass extends DepthFirstAdapter {
		Klass currentKlass;
		@Override
		public void inAClassDecl(AClassDecl node) {
			symbolTable.openScope();
			
			currentKlass = new Klass();
			currentKlass.name = node.getName().getText();
			currentKlass.parent = OBJECT;
			klasses.put(currentKlass.name, currentKlass);
			types2.put(node, currentKlass);
			// TODO: Add parent
		}
		
		@Override
		public void outAClassDecl(AClassDecl node) {
			try {
				symbolTable.closeScope();
			} catch (SemanticException e){
				//e.printStackTrace();
			}
		}
		
		@Override
		public void inAAttributeFeature(AAttributeFeature node) {
			try {
				//System.err.println(klasses.get(node.getTypeId().getText()));
				symbolTable.put(node.getObjectId().getText(), klasses.get(node.getTypeId().getText()));
			} catch(SemanticException e) {
				//e.printStackTrace();
			}
		}
		@Override
		public void inAListExpr(AListExpr node) {
			// TODO Auto-generated method stub
			try {	
				 if(node.getExpr().toString().contains("String")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("String"));
				 }
				 if(node.getExpr().toString().contains("Bool")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("Bool"));
								 
					}
				 if(node.getExpr().toString().contains("Int")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("Int"));
					 
				 }
				 if(node.getExpr().toString().contains("IO")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("IO"));
				 }
				 if(node.getExpr().toString().contains("Error")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("Error"));
								 
				 }
				 if(node.getExpr().toString().contains("void")){
					 symbolTable.put(node.getExpr().toString(), klasses.get("void"));
					 
				 }
					 
				}catch(SemanticException e) {
					
				}
		}
		
		public void outANewExpr(ANewExpr node)  {
			try {
			symbolTable.put(node.toString(), klasses.get(node.getTypeId().getText()));
			}catch(SemanticException e) {
				
			}
		}
		
		 @Override
			public void outAListExpr(AListExpr node) {
			 Klass k;
			try {	
			 if(node.getExpr().toString().contains("String")){
				 k=symbolTable.get("String");
			 }
			 if(node.getExpr().toString().contains("Bool")){
				 k=symbolTable.get("Bool");
							 
				}
			 if(node.getExpr().toString().contains("Int")){
				 k=symbolTable.get("Int");
				 
			 }
			 if(node.getExpr().toString().contains("IO")){
				 k=symbolTable.get("IO");
			 }
			 if(node.getExpr().toString().contains("Error")){
				 k=symbolTable.get("Error");
							 
			 }
			 if(node.getExpr().toString().contains("void")){
				 k=symbolTable.get("void");
				 
			 }
				 
				k = symbolTable.get(node.toString());
				types2.put(node, k);
			}catch(SemanticException e) {
				
			}
				// TODO Auto-generated method stub
				
			}
		
		@Override
		public void outAObjectExpr(AObjectExpr node) {
			try {
				Klass k = symbolTable.get(node.getObjectId().getText());
				types2.put(node, k);
			} catch(SemanticException e) {
				
				
				types2.put(node, INT);
				//e.printStackTrace();
			}
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
		 
		 /*No necesita cambio*/
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
		    	}
		    		if(ErrorManager.getInstance().getErrors().size() == 0){
		    		String mType = node.getTypeId().getText();
		    		String eType = node.getExpr().toString();
		    				    		if(!isSubType2(mType, eType)){ /* necesita cambio*/
		    				    		
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
					/**LinkedList<PExpr> tries = node.getList();
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
							}
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
					} **/
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
		 public void outAAssignExpr(AAssignExpr node){
		    	String s = node.getObjectId().getText();
		    	
		   String typeObj = node.getExpr().toString();
		 
		    	if(s.contains("self")){
		    		ErrorManager.getInstance().getErrors().add(Error.ASSIGN_SELF);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.assignSelf",s);
		    		ErrorManager.getInstance().getErrors().add(Error.BAD_INFERRED);
		    		ErrorManager.getInstance().semanticError("Coolc.semant.badInferred");
		    		//node.setType("major");
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
	    			//types.put(node,error);
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
    			//types.put(node,error);
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
    			//types.put(node,error);
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
    			//types.put(node,error);
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
	    		//types2.put(node, BOOL);
	    			//node.setType("Bool");
	    		}
	    	}else{
	    		//types2.put(node, BOOL);
	    		//node.setType("Bool");
	    	}
	    }
	}
	class TypeChecker extends DepthFirstAdapter {
		public void outAIntExpr(AIntExpr node){
	       // types.put(node, basicClasses().get("Int"));
			types2.put(node, INT);
	    }
		
		public void outAStrExpr(AStrExpr node){
	        //types.put(node, basicClasses().get("String"));
			types2.put(node, STR);
	    }
		
		@Override
		public void outABoolExpr(ABoolExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, BOOL);
		}
		@Override
		public void outAEqExpr(AEqExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, BOOL);
		}
		
		 public void outANegExpr(ANegExpr node){
			 types2.put(node, INT);
		 }
		 @Override
		public void outAPlusExpr(APlusExpr node) {
			// TODO Auto-generated method stub
			 types2.put(node, INT);
		}
		 
		 @Override
		public void outAMinusExpr(AMinusExpr node) {
			// TODO Auto-generated method stub
			 types2.put(node, INT);
		}
		@Override
		public void outALetExpr(ALetExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, INT);
		} 
		
		@Override
		public void outALtExpr(ALtExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, BOOL);
		}
		@Override
		public void outANoExpr(ANoExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, BOOL);
		}
		
		@Override
		public void outALeExpr(ALeExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, BOOL);
		}
		@Override
		public void outAMultExpr(AMultExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, INT);
		}
		
		@Override
		public void outADivExpr(ADivExpr node) {
			// TODO Auto-generated method stub
			types2.put(node, INT);
		}
		 @Override
		public void outAAssignExpr(AAssignExpr node) {
			// TODO Auto-generated method stub
			
			 types2.put(node, INT);
		}
		 @Override
		public void outACallExpr(ACallExpr node) {
			// TODO Auto-generated method stub
			 types2.put(node, INT);
		}
		 
		 @Override
		public void outAAtExpr(AAtExpr node) {
			// TODO Auto-generated method stub
			//System.out.println(node.getObjectId().toString());
			
				 types2.put(node, INT);
			 
		}
		 
			public void outAListExpr(AListExpr node) {
			//System.out.println(node.getExpr().getLast().toString());
			if(node.getExpr().getLast().toString().contains("String")&& !node.getExpr().getLast().toString().contains("Int") && !node.getExpr().getLast().toString().contains("Bool")) {
				types2.put(node, STR);
			}
			if(!node.getExpr().getLast().toString().contains("String")&& node.getExpr().getLast().toString().contains("Int") && !node.getExpr().getLast().toString().contains("Bool")) {
				types2.put(node, INT);
			}
			if(!node.getExpr().getLast().toString().contains("String")&& node.getExpr().getLast().toString().contains("Int") && !node.getExpr().getLast().toString().contains("Bool")) {
				types2.put(node, BOOL);
			}
			types2.put(node, INT);
				 
			 
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
		start.apply(new ScopePass());
		start.apply(new TypeChecker());
		
		start.apply(new OtherVisitor());
		
		start.apply(new InhVisitor());
		
		start.apply(new ExampleVisitor());
	
		
		start.apply(new Pass1());
		
		
		start.apply(new P2());
		if(ErrorManager.getInstance().getErrors().size() > 0){
			throw new SemanticException();
		}
		
		
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
		 		IO.name = "IO";
		 		IO.parent = OBJECT;

		 		
		 		INT = new Klass();
		 		INT.name = "Int";
		 		INT.parent = OBJECT;

		 		
		 		BOOL = new Klass();
		 		BOOL.name = "Bool";
		 		BOOL.parent = OBJECT;

				
		 		STR = new Klass();
				STR.name ="String";
				STR.parent = OBJECT;

				
				ERROR = new Klass();
				ERROR.name = "ERROR";
				ERROR.parent = OBJECT;
				
				
				VOID = new Klass();
				VOID.name = "VOID";
				VOID.parent = OBJECT;

				klasses.put("Object", OBJECT);
				klasses.put("IO", IO);
				klasses.put("Int", INT);
				klasses.put("Bool", BOOL);
			    klasses.put("String", STR);
				
		map.put("Object", ObjectClass);
		map.put("IO", IOClass);
		map.put("Int", IntClass);
		map.put("Bool", BoolClass);
		map.put("String", StringClass);
		
		return map;
		
	}

}
