package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class factorAmp extends CParseRule {
	// factorAmp ::= AMP NUM
	private CParseRule number,primary;
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
		}else if(Primary.isFirst(tk)) {
			primary = new Primary(pcx);
			primary.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + "&の後にnumberまたはprimaryがありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(number != null) {
			number.semanticCheck(pcx);
			setCType(CType.getCType(CType.T_pint));//ポインタ型
			setConstant(number.isConstant());			
		}else if(primary != null) {
			primary.semanticCheck(pcx);
			if(((Primary)(primary)).getCPR() instanceof PrimaryMult) {
				pcx.error("factorAmp の子節点にprimary がつながっているとき、その下にはprimaryMult クラスのオブジェクトが来てはいけません");
			}
			//System.out.print("((Primary)(primary)).getCPR().getCType().getType()"+((Primary)(primary)).getCPR().getCType().getType()+" \n");
			if(((Primary)(primary)).getCPR().getCType().getType() == CType.T_pint) {
				pcx.error("T_pintに関するエラー : factorAmp semanticCheck");
			}else if (((Primary)(primary)).getCPR().getCType().getType() == CType.T_int) {
				setCType(CType.getCType(CType.T_int));		
				setConstant(primary.isConstant());					
			}else {
				setCType(CType.getCType(CType.T_parray));		
				setConstant(primary.isConstant());
			}
			
			/*if(((Primary)(primary)).getCPR().getCType().getType() == CType.T_parray) {
				setCType(CType.getCType(CType.T_parray));		
				setConstant(primary.isConstant());	
			}else {
				pcx.error("T_parrayに関するエラー : factorAmp semanticCheck");
			}*/	
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factorAmp starts");
		if (number != null) {number.codeGen(pcx);}
		else if(primary != null) {
			primary.codeGen(pcx);
		}
		o.println(";;; factorAmp completes");
	}
}
