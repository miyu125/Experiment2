package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class Array extends CParseRule {
	// primary ::= primaryMult | variable
	private CParseRule expression;
	public Array(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LBRA;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(tk.getType() != CToken.TK_RBRA) {
				pcx.error(tk.toExplainString()+"expressionの後に]が足りません\n");
			}
		}else{
			pcx.error(tk.toExplainString()+"[の後にexpressionが足りません\n");
		}
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(expression != null) {
			expression.semanticCheck(pcx);
			if(expression.getCType().getType() == CType.T_int) {
				setCType(CType.getCType(CType.T_int));
				setConstant(expression.isConstant());
			}else {
				pcx.fatalError("配列の中はint型です");
			}
		}
	}
	
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primary starts");
		if (expression != null) { expression.codeGen(pcx); }
		o.println(";;; primary completes");
	}
}