package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Term extends CParseRule {
	// term ::= factor {termMult | termDiv}
	private CParseRule factor,term;
	public Term(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Factor.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている		
		CParseRule list = null,left = null;
		factor = new Factor(pcx);
		factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		left = factor;
		while(termMult.isFirst(tk)||termDiv.isFirst(tk)) {		// "+"だけでなく"-"も認識するようにExpressionSub.isFirst()を追加
			list = null;
			if (termMult.isFirst(tk)) {
				list = new termMult(pcx,left);
			}else {
				list = new termDiv(pcx,left);
			}
			list.parse(pcx);
			left = list;
			tk = ct.getCurrentToken(pcx);
		}
		term = left;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (term != null) {
			term.semanticCheck(pcx);
			this.setCType(term.getCType());		// factor の型をそのままコピー
			this.setConstant(term.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (factor != null) { term.codeGen(pcx); }
		o.println(";;; term completes");
	}
}
