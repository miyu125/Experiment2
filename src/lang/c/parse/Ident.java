package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Ident extends CParseRule {
	// primary ::= primaryMult | variable
	private CToken ident;
	public Ident(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null) {
			//setCType(CType.getCType(CType.T_int));	
			setCType(CType.getCType(CType.T_pint));
			//setCType(CType.getCType(CType.T_array));	
			//setCType(CType.getCType(CType.T_parray));	
			setConstant(false);
		}		
	}
	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
					+ ident.toExplainString() + ">");
		}
		o.println(";;; primary completes");
	}
}