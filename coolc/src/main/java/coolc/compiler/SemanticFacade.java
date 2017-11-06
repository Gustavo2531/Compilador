package coolc.compiler;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.exceptions.SemanticException;
import coolc.compiler.util.Error;

public interface SemanticFacade {
	public void setup(Start start, PrintStream out);
	public void check() throws SemanticException;
	public Set<Error> getErrors();
	public Map<Node, AClassDecl> getTypes();
}
