package coolc.compiler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAtExpr;
import coolc.compiler.autogen.node.ABoolExpr;
import coolc.compiler.autogen.node.ACallExpr;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.ADivExpr;
import coolc.compiler.autogen.node.AEqExpr;
import coolc.compiler.autogen.node.AIfExpr;
import coolc.compiler.autogen.node.AIntExpr;
import coolc.compiler.autogen.node.AIsvoidExpr;
import coolc.compiler.autogen.node.ALeExpr;
import coolc.compiler.autogen.node.ALetDecl;
import coolc.compiler.autogen.node.ALetExpr;
import coolc.compiler.autogen.node.AListExpr;
import coolc.compiler.autogen.node.ALtExpr;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.AMinusExpr;
import coolc.compiler.autogen.node.AMultExpr;
import coolc.compiler.autogen.node.ANegExpr;
import coolc.compiler.autogen.node.APlusExpr;
import coolc.compiler.autogen.node.AStrExpr;
import coolc.compiler.autogen.node.AWhileExpr;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PExpr;
import coolc.compiler.autogen.node.PFeature;
import coolc.compiler.autogen.node.PLetDecl;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.autogen.node.TObjectId;
import coolc.compiler.util.Util;
import coolc.compiler.visitors.DeclarationVisitor;

public class ARMCodegen implements CodegenFacade {
	private Map<Node, Integer> literalIdx;
	private Integer lidx;
	
	class SweepConstants extends DepthFirstAdapter {
		
		
		@Override
		public void inAStrExpr(AStrExpr node) {
			String s = node.getStrConst().getText();
			stringTemplate.addAggr("ints.{idx,value}", lidx++, s.length()+1);
			int int32size = ( (s.length() + 1 + 3) & ~0x03 )/4 ;
			stringTemplate.addAggr("strings.{idx,size,sizeIdx,value}", lidx, int32size+3, lidx-1, Util.escapeString(s));
			literalIdx.put(node, lidx++);
		}	
		
		
		
		@Override
		public void inAIntExpr(AIntExpr node) {
			stringTemplate.addAggr("ints.{idx,value}", lidx, node.getIntConst().getText());
			literalIdx.put(node, lidx++);
		}
	}

	class MethodVisitor extends DepthFirstAdapter {
		String lastResult;
		int labelCounter = 0;
		int counterLets=0;
		int framePointer =0;
		int lr=0;
		int counterParameters=0;
		int offs=0;
		HashMap<Integer, Boolean> letCountHash = new HashMap<>();
		@Override
		public void inAIntExpr(AIntExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("intExpr");
			st.add("e", "int_const" + literalIdx.get(node));
			
			lastResult = st.render();
		}

		@Override
		public void inAStrExpr(AStrExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("strExpr");
			st.add("e", "str_const" + literalIdx.get(node));
			
			lastResult = st.render();
		}

		@Override
		public void inABoolExpr(ABoolExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("boolExpr");
			if(node.getBoolConst().getText().toLowerCase().equals("true")) {
				st.add("e", "bool_const1");
			}else {
				st.add("e", "bool_const0");

			}
			lastResult = st.render();
		}
		
		private AClassDecl klass;
		private HashMap<Integer, Boolean> extracted;
		@Override
		public void caseAClassDecl(AClassDecl node) {
			klass = node;	
			
			for (PFeature f : node.getFeature()) {
				if (f instanceof AMethodFeature) {
					f.apply(this);
				}
			}
		}		

		public void outAMethodFeature(AMethodFeature node) {	
			ST st;
			st = templateGroup.getInstanceOf("methodDeclarations");
			int nParameters = node.getFormal().size();
			int totalStackSize = getCurrentOffset(nParameters);
			stringTemplate.addAggr("methodsText.{klass, name, code, counterLV, outFramePointer, inFramePointer, lr}", klass.getName().getText(), node.getObjectId().getText(), lastResult, totalStackSize, totalStackSize-framePointer, totalStackSize-framePointer-4, totalStackSize-lr);
		}

		private HashMap<Integer, Boolean> extracted() {
			return letCountHash;
		}
		
		@Override
		public void caseAEqExpr(AEqExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("eqExpr");
			
			node.getL().apply(this);
			st.add("left", lastResult);
			
			node.getR().apply(this);
			st.add("right", lastResult);
			
			lastResult = st.render();
		}
		
		//---------------------------------------------------------
		@Override
		public void caseALtExpr(ALtExpr node) {
			

			ST st;
			st = templateGroup.getInstanceOf("ltExpr");
			
			st.add("x", getLabel("x"));

			node.getL().apply(this);
			st.add("left", lastResult);
			
			node.getR().apply(this);
			st.add("right", lastResult);
						
			lastResult = st.render();
		}
		//Para regresar la posición del self en el stack
		//Actualmente regresa 0
		@Override
		public void caseTObjectId(TObjectId node) {
			ST st;
			st = templateGroup.getInstanceOf("");
			
			//Despues se implementará bien con el offset del self por el momento regresa 0
			//st.add("e", getCurrentOffset());
			st.add("e", 0);	
		}
		//------------------------------------------------

		@Override
		public void caseALeExpr(ALeExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("leExpr");
			
			st.add("x", getLabel("x"));

			node.getL().apply(this);
			st.add("left", lastResult);
			
			node.getR().apply(this);
			st.add("right", lastResult);
			
			
			lastResult = st.render();
		}

		@Override
		public void outAAtExpr(AAtExpr node) {
            ST st;
			st = templateGroup.getInstanceOf("atExpr");			
			
            lastResult=st.render();

        }
		
        @Override
        public void outACallExpr(ACallExpr node){
            ST st;
			st = templateGroup.getInstanceOf("callExpr");			
			
            lastResult=st.render();

        }

		@Override
		public void outALetDecl(ALetDecl node) {
			// TODO Auto-generated method stub
			//super.outALetDecl(node);
			
			ST st;
			st = templateGroup.getInstanceOf("letDecl");
		
			
			if(node.getExpr() != null){
				
	            if(!node.getTypeId().getText().equals("SELF_TYPE")){
	            		node.getExpr().apply(this);
	            		//System.out.println("Para"+ node.getObjectId().toString());
	                st.add("loadedExpr", lastResult);
	                lastResult=st.render();
	                if(extracted().get(node.hashCode()) == null || extracted().get(node.hashCode()) == false) {
						counterLets++;
						extracted().put(node.hashCode(), true);
					}
	            	}
			}
		}
		
		
		public void getCountLet(int num) {
			if(counterLets!=num) {
				counterLets+=num;
			}
		}
		
		public void getParameters(int numP) {
			if(counterParameters!=numP) {
				counterParameters+=numP;
			}
		}
	
		@Override
		public void outALetExpr(ALetExpr node) {
			//super.outALetExpr(node);
			String r = "";
			
			
			for (PLetDecl p : node.getLetDecl()) {
				p.apply(this);
				r += lastResult;
			}
			
			
			ST st;
			st = templateGroup.getInstanceOf("letExpr");
			node.getExpr().apply(this);
			st.add("resultExpr", lastResult);
			r += st.render();
			
			
			lastResult = r;
		}

		 public void inALetExpr(ALetExpr node){
		     
		   }
		String getLabel(String s){
			labelCounter ++;
			return s + labelCounter;
		}
		
		
		
		
public int getCurrentOffset(int nParameters) {
			int positionSelf;
			int positionReturnAddress;
			int positionParameters;
			int positionFramePointer=0;
			int positionLocalVariables=0;
			int positionStackPointer=0;
			int stackSize=0;
			
			positionLocalVariables=4*counterLets;
			positionStackPointer=positionLocalVariables;
			positionFramePointer = positionLocalVariables+4;
			framePointer=positionLocalVariables;
			positionStackPointer=positionFramePointer;
			positionParameters=positionFramePointer+nParameters*4;
			positionStackPointer=positionParameters;
			positionReturnAddress=positionParameters+4;
			lr = positionReturnAddress;
			positionStackPointer=positionReturnAddress;
			positionSelf= positionReturnAddress+4;
			positionStackPointer=positionSelf;
			stackSize=positionSelf;
			
			return stackSize;			
		}
		
		@Override
		public void caseAWhileExpr(AWhileExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("whileExpr");
			
			st.add("loopLabel", getLabel("loopLabel"));
			node.getTest().apply(this);
			st.add("testExpression", lastResult);
			
			node.getLoop().apply(this);
			st.add("loopExpression", lastResult);
			st.add("exitLabel", getLabel("exitLabel"));
			
			lastResult = st.render();
		}
		
		@Override
		public void caseAListExpr(AListExpr node) {
			String r = "";
			for(PExpr p: node.getExpr()){
				p.apply(this); //gonna update the last result
				r += lastResult; 
			}
			lastResult = r;
		}
		
		@Override
		public void caseAPlusExpr(APlusExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("addOperation");
			
			node.getL().apply(this);
			st.add("n1", lastResult);
			
			node.getR().apply(this);
			st.add("n2", lastResult);
			
			lastResult = st.render();
		}
		
		@Override
		public void caseAMultExpr(AMultExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("mulOperation");
			
			node.getL().apply(this);
			st.add("n1", lastResult);
			
			node.getR().apply(this);
			st.add("n2", lastResult);
			
			lastResult = st.render();
		}
		
		@Override
		public void caseAMinusExpr(AMinusExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("minusOperation");
			
			node.getL().apply(this);
			st.add("n1", lastResult);
			
			node.getR().apply(this);
			st.add("n2", lastResult);
			
			lastResult = st.render();
		}

		@Override
		public void caseADivExpr(ADivExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("divOperation");
			
			node.getL().apply(this);
			st.add("n1", lastResult);
			
			node.getR().apply(this);
			st.add("n2", lastResult);
			
			lastResult = st.render();
		}
		
		@Override
		public void caseANegExpr(ANegExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("negExpr");
			
			node.getExpr().apply(this);
			st.add("n1", lastResult);
			
			node.setExpr(node);
			st.add("n1", lastResult);
			lastResult = st.render();
		}

		@Override
		public void caseAIfExpr(AIfExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("ifOperation");
			
			node.getTest().apply(this);
			st.add("testExpression", lastResult);
			
			node.getTrue().apply(this);
			st.add("trueExpression", lastResult);
			
			node.getFalse().apply(this);
			st.add("falseExpression", lastResult);
			
			lastResult = st.render();
		}
		
		@Override
		public void caseAIsvoidExpr(AIsvoidExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("ifOperation");
			
			node.getExpr().apply(this);
			st.add("testExpression", lastResult);
			
			node.setExpr(node);
			st.add("trueExpression", lastResult);
			
			lastResult = st.render();
		}
	

	}

	private PrintStream out;
	private Start start;
	STGroup templateGroup;
	ST stringTemplate;
	
	public ARMCodegen () {
		literalIdx = new HashMap<Node, Integer>();
		lidx = 0;
	}

	@Override
	public void setup(Start start, PrintStream out) {
		this.start = start;
		this.out = out;
		
	}

	@Override
	public void gen() {
		templateGroup = new STGroupFile("src/main/resources/cool/compiler/arm.stg");
		stringTemplate = templateGroup.getInstanceOf("base");
		
		dataSegment();
		textSegment();
		
		String result = stringTemplate.render();
		out.print(result);
	}
	
	private void dataSegment() {

//		*** Global Declarations, example:
//		.globl    class_nameTab
//		.globl    Main_protObj
//		.globl    Int_protObj
//		.globl    String_protObj
//		.globl    bool_const0
//		.globl    bool_const1
//		.globl    _int_tag
//		.globl    _bool_tag
//		.globl    _string_tag
		
		// TODO: Replace by global declarations
		
		//stringTemplate.addAggr("globalsData.{name}", "name_of_my_lovely_to_be_global_symbol");
		stringTemplate.addAggr("globalsData.{name}", "class_nameTab");
		stringTemplate.addAggr("globalsData.{name}", "Main_protObj");
		stringTemplate.addAggr("globalsData.{name}", "Int_protObj");
		stringTemplate.addAggr("globalsData.{name}", "String_protObj");
		stringTemplate.addAggr("globalsData.{name}", "bool_const0");
		stringTemplate.addAggr("globalsData.{name}", "bool_const1");
		stringTemplate.addAggr("globalsData.{name}", "_int_tag");
		stringTemplate.addAggr("globalsData.{name}", "_bool_tag");
		stringTemplate.addAggr("globalsData.{name}", "_string_tag");
		
		//Por aqui van los tags
		stringTemplate.addAggr("tags.{name,value}", "int", 2);
		stringTemplate.addAggr("tags.{name,value}", "bool", 3);
		stringTemplate.addAggr("tags.{name,value}", "string", 4);

		
//		*** Constants
//	    1. String literals
//        1.1 Get list of literals (each must have a unique index)
//        1.2 Get list of integer constants (of string sizes')
//        1.3 Replace in the template:
//            - tag 
//            - object size: [tag, size, ptr to dispTab, ptr to int, (len(contenido)+1)%4] = ?
//                (+1 because the 0 at the end of strings)
//            - index of the ptr of int (with the size)
//            - value (in the string)
//    2. Integer literals
//        2.1 Literals neccessary for the strings' sizes
//        2.2 + constants in source code
//        2.3 Replace in the template:
//            - tag 
//            - object size: [tag, size, ptr to dispTab and content] = 4 words
//            - value

		// TODO: Replace by global constants
		// Note this is like instance some type of inner class to hold the values
//		stringTemplate.addAggr("strings.{idx,tag,size,sizeIdx,value}", 1, 5, 8, 0, "String 1");
//		st.addAggr("strings.{idx,tag,size,sizeIdx,value}", 2, 5, 11, 0, "Hello World");
//		st.addAggr("strings.{idx,tag,size,sizeIdx,value}", 3, 5, 16, 0, "Cool compiler");
		
//		st.addAggr("ints.{idx,tag,value}", 1, 3, 15);
//		st.addAggr("ints.{idx,tag,value}", 1, 3, 28);
		
		 start.apply(new SweepConstants());
		
//		** Tables
//	    1. class_nameTab: table for the name of the classes in string
//        1.1 The objects were already declared above
//        1.2 The tag of each class is used for the offset from class_nameTab		
		// TODO: Table of names of classes
		 /* int counter = 0;
		 for (Klass k : set) { //the set is missing declaration lol
			 stringTemplate.addAggr("classNames.{id}", counter++); //the counter serves as an idx increment
		 }*/
		 for (int x : new int[] {3,4,5,6,7,8}) { //Before: {1,2,3,4,5,6,7,8,9}
			 stringTemplate.addAggr("classNames.{id}", x);
		 }
	
		
//      2. class_objTab: prototypes and constructors for each object
//        2.1 Indexed by tag: 2*tag -> protObj, 2*tag+1 -> init
		// TODO: Table of objects and constructors
		
		 for (String s : new String[] {"Object", "IO", "Int", "Bool", "String","Main"}) {
			 stringTemplate.addAggr("baseObjects.{id}", s);
		 }
		
//      3. dispTab for each class
//        3.1 Listing of the methods for each class considering inheritance
		// TODO: Dispatch tables
		
		String [] s = new String[] {"a", "b"};
		stringTemplate.addAggr("methodsData.{name, methods}", "className", s);
		

//		*** protObjs
//		Attributes, also consider inherited ones.
		// TODO: Base prototype (base object) of each class 
	}
	
	private void textSegment() {
		start.apply(new MethodVisitor());

//		*** Global declarations of the text segment, for example:
//		.global    Main_init
//		.global    Int_init
//		.global    String_init
//		.global    Bool_init
//		.global    Main.main
		// TODO: Global names of TEXT segment
		stringTemplate.addAggr("globalsText.{name}", " Main_init");
		stringTemplate.addAggr("globalsText.{name}", " Int_init");
		stringTemplate.addAggr("globalsText.{name}", " String_init");
		stringTemplate.addAggr("globalsText.{name}", " Bool_init");
		stringTemplate.addAggr("globalsText.{name}", " Main.main");
		
//		*** Constructors (init) for each class
		// Rembember to use the next to save and restore:
//		Object_init:
//		    push {r0, lr}
//		    pop {r0, pc}
//		    bx lr
		// TODO: Constructors, remember to generate code for optional initialization
		
//		*** Methods
		// TODO: code for methods *mainly all expressions*
	}
	class Klass{
		String name, parent;
		int tag;
		int int32size = ((name.length() + 1 + 3) & ~0x03) / 4;
	}

	

}
