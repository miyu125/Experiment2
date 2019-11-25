package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;


import lang.*;
import lang.c.*;
import lang.c.parse.Number;

public class UnsignedFactor extends CParseRule {
	// unsignedFactor ::= factorAmp | number | LPAR expression RPAR | addressToValue
	private CParseRule number,factoramp,factor,expression,addressToValue;
	public UnsignedFactor(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || factorAmp.isFirst(tk) || tk.getType() == CToken.TK_LPAR || AddressToValue.isFirst(tk); //factorAmpかnumberかAddressToValueのisFirstを満たす
																							//もしくは最初がLPAR
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Number.isFirst(tk)) {																	//number
			number = new Number(pcx);
			number.parse(pcx);
			factor = number;
		}else if(factorAmp.isFirst(tk)) {															//factorAmp
			factoramp = new factorAmp(pcx);
			factoramp.parse(pcx);
			factor = factoramp;
		}else if(tk.getType() == CToken.TK_LPAR) {												//LPAR expression RPAR
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
				factor = expression;
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_RPAR) {
					tk = ct.getNextToken(pcx);
					//System.out.print("gettype = "+tk.getType()+"gettext = "+tk.getText()+"\n");	
				} else {
                    pcx.fatalError(tk.toExplainString() + "expressionの後ろには)が来ます");
                }
            } else {
                pcx.fatalError(tk.toExplainString() + "(の後ろにはexpressionが来ます");
            }
		}else if(AddressToValue.isFirst(tk)) {													//addressToValue
			addressToValue = new AddressToValue(pcx);
			addressToValue.parse(pcx);
			factor = addressToValue;
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
		o.println(";;; unsignedfactor starts");
		if (factor != null) { factor.codeGen(pcx); }
		o.println(";;; unsignedfactor completes");
	}
}