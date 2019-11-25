package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Number;

public class MinusFactor extends CParseRule {
	// MinusFactor ::= MINUS unsignedFactor
	private CParseRule unsignedfactor,factor;
	private CToken minus;
	public MinusFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MINUS ;//最初が'-'か
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		minus = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(UnsignedFactor.isFirst(tk)) {
			unsignedfactor = new UnsignedFactor(pcx);
			unsignedfactor.parse(pcx);
			factor = unsignedfactor;			
		}else {
			pcx.fatalError(tk.toExplainString()+"'-'の後ろにはunsignedFactorが来ます");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			if(factor.getCType().getType()==CType.T_int) {
				setCType(factor.getCType());		// number の型をそのままコピー
				setConstant(factor.isConstant());	// number は常に定数
			}else {
				pcx.fatalError(minus.toExplainString() + "番地の前に-はつけられません");//
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; minusFactor starts");
		if (factor != null) { 
			factor.codeGen(pcx);
			//o.println("\tMOV\t#0, (R6)+\t; 符号反転ここから");
			o.println("\tMOV\t#0, R0\t\t; 符号反転ここから");  //符号の反転を行うコード
			o.println("\tSUB\t-(R6), R0\t;");
			o.println("\tMOV\tR0, (R6)+\t; 符号反転ここまで");
		}
		o.println(";;; minusFactor completes");
	}
}