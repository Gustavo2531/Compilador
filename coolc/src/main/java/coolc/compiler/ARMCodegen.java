package coolc.compiler;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.AIntExpr;
import coolc.compiler.autogen.node.AMethodFeature;
import coolc.compiler.autogen.node.APlusExpr;
import coolc.compiler.autogen.node.AStrExpr;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PFeature;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.util.Util;

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
		
		@Override
		public void inAIntExpr(AIntExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("intExpr");
			// TODO: Here you need to put some way that the constant know
			// its name in assembly, for example maybe "34" is int_const3
			// st.add("e", node.getIntConst().codeRef());
			// or
			// st.add("e", node.getIntConst().codeRef());
			// NEWS!!!! I made your homework
			// whatever, for the example I will let it fixed
			
			st.add("e", "int_const" + literalIdx.get(node));
			
			lastResult = st.render();
		}
		
		private AClassDecl klass;
		@Override
		public void caseAClassDecl(AClassDecl node) {
			klass = node;
			
			for (PFeature f : node.getFeature()) {
				if (f instanceof AMethodFeature) {
					f.apply(this);
				}
			}
		}		
		
		@Override
		public void outAMethodFeature(AMethodFeature node) {
			stringTemplate.addAggr("methodsText.{klass, name, code}", klass.getName().getText(), node.getObjectId().getText(), lastResult);
		}
		
		@Override
		public void caseAPlusExpr(APlusExpr node) {
			ST st;
			st = templateGroup.getInstanceOf("addExpr");
			
			node.getL().apply(this);
			st.add("left", lastResult);
			
			node.getR().apply(this);
			st.add("right", lastResult);
			
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
		//stringTemplate.addAggr("globalsData.{name}", "class_nameTab");

		
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
//		st.addAggr("strings.{idx,tag,size,sizeIdx,value}", 1, 5, 8, 0, "String 1");
//		st.addAggr("strings.{idx,tag,size,sizeIdx,value}", 2, 5, 11, 0, "Hello World");
//		st.addAggr("strings.{idx,tag,size,sizeIdx,value}", 3, 5, 16, 0, "Cool compiler");
		
//		st.addAggr("ints.{idx,tag,value}", 1, 3, 15);
//		st.addAggr("ints.{idx,tag,value}", 1, 3, 28);
		
//		 start.apply(new SweepConstants());
		
//		** Tables
//	    1. class_nameTab: table for the name of the classes in string
//        1.1 The objects were already declared above
//        1.2 The tag of each class is used for the offset from class_nameTab		
		// TODO: Table of names of classes
		
//      2. class_objTab: prototypes and constructors for each object
//        2.1 Indexed by tag: 2*tag -> protObj, 2*tag+1 -> init
		// TODO: Table of objects and constructors
		
//      3. dispTab fo reach class
//        3.1 Listing of the methods for each class considering inheritance
		// TODO: Dispatch tables
		/*
		String [] s = new String[] {"a", "b"};
		stringTemplate.addAggr("methodsData.{name, methods}", "className", s);
		*/

//		*** protObjs
//		Attributes, also consider inherited ones.
		// TODO: Base prototype (base object) of each class 
	}
	
	private void textSegment() {
//		start.apply(new MethodVisitor());

//		*** Global declarations of the text segment, for example:
//		.global    Main_init
//		.global    Int_init
//		.global    String_init
//		.global    Bool_init
//		.global    Main.main
		// TODO: Global names of TEXT segment
		
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

	

}
