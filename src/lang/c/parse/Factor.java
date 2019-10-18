package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Number;

public class Factor extends CParseRule {
	// factor ::= factorAmp | number
	private CParseRule number,factoramp,factor;
	public Factor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || factorAmp.isFirst(tk);//factorAmpかnumberのisFirstを満たすか
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
			factor = number;
		}else if(factorAmp.isFirst(tk)) {
			factoramp = new factorAmp(pcx);
			factoramp.parse(pcx);
			factor = factoramp;
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType());		// number の型をそのままコピー
			setConstant(factor.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; factor completes");
	}
}