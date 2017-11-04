package coolc.compiler.visitors;

import java.io.PrintStream;
import java.util.Map;

import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.PExpr;


public class ASTPrinterTypes extends ASTPrinter {

	private Map<Node, AClassDecl> map;

	public ASTPrinterTypes(Map<Node, AClassDecl> map, PrintStream out) {
		super(out);
		this.map = map;
	}

	public void defaultOut(Node node) {
		// replace the current indent with the one from the stack
		indent = indent.substring(0, indent.length() - 3);
		indent = indent.substring(0, indent.length() - 1)
				+ (String) indentchar.pop();

		// prepend this line to the output.
		output = indent
				+ "- "
				+ node.getClass()
						.getName()
						.substring(
								node.getClass().getName().lastIndexOf('.') + 1)
				+ ((node instanceof PExpr)?":"+map.get(node).getName().getText():"")
				+ "\n" + output;

		// replace any ` with a |
		indent = indent.substring(0, indent.length() - 1) + '|';
	}

}
