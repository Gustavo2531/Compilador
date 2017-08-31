package coolc.compiler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import coolc.compiler.autogen.lexer.LexerException;
import coolc.compiler.autogen.node.Start;
import coolc.compiler.autogen.parser.ParserException;

public interface Compiler {
	public Start lexAndParse(File file, PrintStream output) throws LexerException, IOException, ParserException;
	public void genCode(Start node, PrintStream output);
	public void setup(CodegenFacade c);
}
