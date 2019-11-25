package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Variable extends CParseRule {
	// primary ::= primaryMult | variable
	private CParseRule ident,array;
	public Variable(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Ident.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Ident.isFirst(tk)) {
			ident = new Ident(pcx);
			ident.parse(pcx);
		}
		tk = ct.getCurrentToken(pcx);
		if(Array.isFirst(tk)) {
			array = new Array(pcx);
			array.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(ident != null) {
			ident.semanticCheck(pcx);
			if (array != null) {
				array.semanticCheck(pcx);
				if(ident.getCType().getType() == CType.T_parray) {
					setCType(CType.getCType(CType.T_pint));	
				}else if(ident.getCType().getType() == CType.T_array) {
					setCType(CType.getCType(CType.T_int));	
				}/*else if(ident.getCType().getType() == CType.T_int){
					setCType(CType.getCType(CType.T_int));
				}else if(ident.getCType().getType() == CType.T_pint) {
					setCType(CType.getCType(CType.T_pint));

				}*/else{
					pcx.fatalError("型に関するエラー : Variable semanticCheck");
				}
			}else {
				if(ident.getCType().getType() == CType.T_array) {
					pcx.fatalError("T_array に関するエラー : Variable semanticCheck");
				}
				if(ident.getCType().getType() == CType.T_parray){
					pcx.fatalError("T_parrayに関するエラー : Variable semanticCheck");
				}else {
					if(ident.getCType().getType() == CType.T_int) {
						setCType(CType.getCType(CType.T_int));
					}else if(ident.getCType().getType() == CType.T_pint) {
						setCType(CType.getCType(CType.T_pint));
					}else {
						pcx.fatalError("T_intに関するエラー : Variable semanticCheck");
					}	
				}
				setCType(ident.getCType());
			}
			setConstant(ident.isConstant());
		}


	}

	public CParseRule getCPR() {
		return array;
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; variable starts");
		if (ident != null) { ident.codeGen(pcx); }
		if (array != null) {
			array.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; Variable: 値を取り出し");
			o.println("\tADD\t-(R6), R0\t; Variable: 配列の先頭番地を取り出す，格納されている番地を計算する");
			o.println("\tMOV\tR0, (R6)+\t; Variable: 番地の値をスタックに戻す");
		}
		o.println(";;; variable completes");
	}
}