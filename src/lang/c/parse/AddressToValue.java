package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;


import lang.*;
import lang.c.*;
import lang.c.parse.Number;

public class AddressToValue extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue
	private CParseRule primary;
	public AddressToValue(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk); //factorAmpかnumberのisFirstを満たす
																							//もしくは最初がLPAR
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている													//number
			primary = new Primary(pcx);
			primary.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null) {
			primary.semanticCheck(pcx);
			setCType(primary.getCType());		// number の型をそのままコピー
			setConstant(primary.isConstant());	// number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; addressToValue starts");
		if (primary != null) { primary.codeGen(pcx); }
		o.println("\tMOV\t-(R6), R0\t; addressToValue: スタックから番地を取り出す");
		o.println("\tMOV\t(R0), (R6)+\t; addressToValue: 番地から値を取り出しスタックへ");
		o.println(";;; addressToValue completes");
	}
}