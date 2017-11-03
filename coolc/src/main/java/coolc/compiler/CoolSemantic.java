package coolc.compiler;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import coolc.compiler.autogen.analysis.DepthFirstAdapter;
import coolc.compiler.autogen.node.AAttributeFeature;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.exceptions.SemanticException;
import coolc.compiler.util.Error;

public class CoolSemantic implements SemanticFacade {
	private Map<Node, AClassDecl> tipos;
	private Start start;
	private PrintStream out;
	
	class ErrorsVisitor extends DepthFirstAdapter {
		@Override
		public void outAAttributeFeature(AAttributeFeature node) {
			if (node.getObjectId().getText().equals("self")) {
				ErrorManager.getInstance().getErrors().add(Error.SELF_ATTR);
				ErrorManager.getInstance().semanticError("Coolc.semant.selfAttr");
			}
		}
		
		@Override
		public void outAClassDecl(AClassDecl node) {
			if (node.getName().getText().equals("Object")) {
				ErrorManager.getInstance().getErrors().add(Error.REDEF_BASIC);
				ErrorManager.getInstance().semanticError("Coolc.semant.redefBasic", node.getName().getText());						
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
		this.start.apply(new ErrorsVisitor());
		
		if (ErrorManager.getInstance().getErrors().size() > 0) {
			throw new SemanticException();
		}
	}

	@Override
	public Set<Error> getErrors() {
		return ErrorManager.getInstance().getErrors();
	}

	@Override
	public Map<Node, AClassDecl> getTypes() {
		return this.tipos;
	}

}
