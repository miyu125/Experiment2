package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;

public class Primary extends CParseRule {
	// primary ::= primaryMult | variable
	private CParseRule primaryMult;
	private CParseRule variable;
	public Primary(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(PrimaryMult.isFirst(tk)) {
			primaryMult = new PrimaryMult(pcx);
			primaryMult.parse(pcx);
		}else if(Variable.isFirst(tk)){
			variable = new Variable(pcx);
			variable.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "Primary error : isFirst not satisfied");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(primaryMult != null) {
			primaryMult.semanticCheck(pcx);
			setCType(primaryMult.getCType());		
			setConstant(primaryMult.isConstant());
		}
		if (variable != null) {
			variable.semanticCheck(pcx);
			setCType(variable.getCType());		
			setConstant(variable.isConstant());
		}
		
	}
	
	public CParseRule getCPR() {
		if(primaryMult != null) {
			return primaryMult;
		}else if(variable != null) {
			return variable;
		}else {
			return null;
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (primaryMult != null) { primaryMult.codeGen(pcx); }
		if (variable != null) { variable.codeGen(pcx); }
		o.println(";;; primary completes");
	}
}