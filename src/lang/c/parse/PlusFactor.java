package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class PlusFactor extends CParseRule {
	// MinusFactor ::= PLUS unsignedFactor
	private CParseRule unsignedfactor,factor;
	private CToken plus;
	public PlusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_PLUS ;//最初が'-'か
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		plus = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
			factor = unsignedfactor;
		}else {
			pcx.fatalError(tk.toExplainString()+"'+'の後ろにはunsignedFactorが来ます");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			if(factor.getCType().getType()== CType.T_int || factor.getCType().getType()== CType.T_pint) {
				setCType(factor.getCType());		// number の型をそのままコピー
				setConstant(factor.isConstant());	// number は常に定数
			}else {
				pcx.fatalError(plus.toExplainString() + "");//
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; plusFactor starts");
		if (factor != null) { factor.codeGen(pcx);}
		o.println(";;; plusFactor completes");
	}
}