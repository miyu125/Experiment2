package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import lang.c.CSymbolTableEntry;
import lang.c.CSymbolTable;

public class StatementInput extends CParseRule {
	// statementInput ::= INPUT primary SEMI
	private CParseRule primary,factoramp;
	private CSymbolTable csymboltable;
	private CSymbolTableEntry search;
	public StatementInput(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INPUT;//PrimaryのisFirstを満たす
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		}
		else if(factorAmp.isFirst(tk)) {
			factoramp = new factorAmp(pcx);
			factoramp.parse(pcx);}
		else {
			pcx.error(tk.toExplainString() + "PrimaryまたはfactorAmpが足りません");
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);		
		}else {
			pcx.error(tk.toExplainString() + " SEMIが足りません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primary != null) { 
			primary.semanticCheck(pcx); 
			if(primary.isConstant() == true) {
				pcx.fatalError("StatementInput error : 定数には読み込めません");
			}
		}
		if(factoramp != null) {
			factoramp.semanticCheck(pcx);			 
			if(factoramp.isConstant() == true) {
				pcx.fatalError("StatementInput error : 定数には読み込めません");
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementInput starts");
		if(primary != null) { primary.codeGen(pcx); }
		//if(factoramp != null) { factoramp.codeGen(pcx); }
		o.println("\tMOV\t#0xFFE0, R1\t; StatementInput: 0xFFE0をR1に");
		o.println("\tMOV\t-(R6), R0\t; StatementInput:");
		o.println("\tMOV\t(R1), (R0)\t; StatementInput: 入力値を番地へ");
		o.println(";;; statementInput completes");
	}
}