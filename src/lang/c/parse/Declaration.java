package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;

public class Declaration extends CParseRule {
	// declaration ::= intDecl | constDecl
	private CParseRule intDecl,constDecl;
	public Declaration(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(IntDecl.isFirst(tk)) {
			intDecl = new IntDecl(pcx);
			intDecl.parse(pcx);
		}else if(ConstDecl.isFirst(tk)){
			constDecl = new ConstDecl(pcx);
			constDecl.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "Declaration error : intDeclもしくはconstDeclがありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Declaration starts");
		if (intDecl != null) { 
			intDecl.codeGen(pcx); 
		}else if(constDecl != null) {
			constDecl.codeGen(pcx);
		}
		o.println(";;; Declaration completes");
	}
}