package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTable;
import lang.c.CToken;
import lang.c.CTokenizer;
//import lang.c.CType;
import lang.c.CSymbolTableEntry;

public class Ident extends CParseRule {
	// primary ::= primaryMult | variable
	private CToken ident;
	private CSymbolTableEntry symTable;
	private CSymbolTable csymboltable;
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
		symTable = pcx.getCSymbolTable().getCSymbolTableGlobal().search(ident.getText());
		if(symTable == null) {
			pcx.error(ident.toExplainString() + "は宣言されていません");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null) {
			//setCType(CType.getCType(CType.T_int));	
			//setCType(CType.getCType(CType.T_pint));
			//setCType(CType.getCType(CType.T_array));	
			//setCType(CType.getCType(CType.T_parray));	
			//setConstant(false);
			//setConstant(true);
			setCType(symTable.gettype());
			setConstant(symTable.getconstp());
		}		
	}
	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Ident starts");
		if (ident != null) {
			o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
					+ ident.toExplainString() + ">");
		}
		o.println(";;; Ident completes");
	}
}