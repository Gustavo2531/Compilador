package coolc.compiler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.util.Map;
import java.util.Set;

import coolc.compiler.autogen.lexer.LexerException;
import coolc.compiler.autogen.node.AClassDecl;
import coolc.compiler.autogen.node.Node;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.autogen.parser.Parser;
import coolc.compiler.autogen.parser.ParserException;
import coolc.compiler.exceptions.SemanticException;
import coolc.compiler.util.Error;
import coolc.compiler.visitors.ASTPrinter;
import coolc.compiler.visitors.ASTPrinterTypes;

public class CompilerImpl implements Compiler {
	public static String file = "src/test/resources/semantic/input/compare.cool";
	//public static String file = "src/test/resources/codegen/input/while-val.cool";
	//public static String file = "src/test/resources/semantic/input/redefinedobject.cool";
	public static String outFile = "src/test/resources/test.s";
	
	
	
	private CoolcLexer lexer;
	private Parser parser;
	private CodegenFacade codegen;

	private SemanticFacade semantic;
	
	public CoolcLexer getLexer() {
		return lexer;
	}
	
	public static void main(String [] args) throws LexerException, IOException, ParserException {
		CompilerImpl compiler = new CompilerImpl();
		Start start = null;
		
		if (args.length > 0) {
			file = args[0];
		}

		compiler.setup(new CoolSemantic(), new ARMCodegen());		
		
		try {
			start = compiler.lexAndParse(new File(file), System.err);
		} catch (ParserException e) {
			System.err.format("\"%s\", Syntax error at or near [%s]\nLast good token was [%s]\n%s\n",
					file, e.getToken().getText(), compiler.getLexer().getLastToken().getText(),
					e.getMessage());
			System.err.format("\nCompilation halted due to parse errors.\n");			
			System.exit(-1);
		}
		
		//start.apply(new ASTPrinter(System.out));
		ErrorManager.getInstance().setOut(System.err);
		ErrorManager.getInstance().reset();
		
		try {
			compiler.semanticCheck(start, System.err);
		} catch (SemanticException e) {
			System.err.format("Compilation halted due to semantic errors.\n");		
			//System.exit(-1);
		} finally {
			ErrorManager.getInstance().reset();
			start.apply(new ASTPrinterTypes(compiler.getTypes(), System.out));
		}
		
		//start.apply(new ASTPrinterTypes(compiler.getTypes(), System.out));
		
		// When generating code, uncomment this:
		// PrintStream out = new PrintStream(new FileOutputStream(outFile));
		// compiler.genCode(start, out);
		// compiler.genCode(start, System.out);
	}
	
	@Override
	public Start lexAndParse(File file, PrintStream out) throws LexerException, IOException, ParserException {
		lexer = new CoolcLexer(new PushbackReader(new FileReader(file)), out);
		parser = new Parser(lexer);
		lexer.setParser(parser);
		
		Start start = parser.parse();
		return start;
	}
	
	public void genCode(Start start, PrintStream out) {
		// Here it is assumed codegen was set elsewhere
		codegen.setup(start, out);
		codegen.gen();
	}

	@Override
	public void setup(SemanticFacade s, CodegenFacade c) {
		semantic = s;
		codegen = c;		
	}

	@Override
	public void semanticCheck(Start node, PrintStream output) throws SemanticException {
		semantic.setup(node, output);
		semantic.check();
	}

	@Override
	public Set<Error> getErrors() {
		return semantic.getErrors();
	}

	@Override
	public Map<Node, CoolSemantic.Klass> getTypes() {
		return semantic.getTypes();
	}

}