package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class factorAmp extends CParseRule {
	// factorAmp ::= AMP NUM
	private CParseRule number;
	public factorAmp(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Number.isFirst(tk)) {
			number = new Number(pcx);
			number.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(number != null) {
			number.semanticCheck(pcx);
			this.setCType(CType.getCType(CType.T_pint));//ポインタ型
			this.setConstant(true);			
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) {number.codeGen(pcx);}
		o.println(";;; factorAmp completes");
	}
}
