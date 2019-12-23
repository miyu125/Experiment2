package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;

public class StatementOutput extends CParseRule {
	// statementOutput ::= Output expression SEMI
	private CParseRule expression;
	public StatementOutput(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_OUTPUT; //PrimaryのisFirstを満たす
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
		}else {
			pcx.error(tk.toExplainString() + " expressionが足りません");
		}
		tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_SEMI) {
			ct.getNextToken(pcx);
		}else {
			pcx.error(tk.toExplainString() + " SEMIが足りません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(expression != null) { expression.semanticCheck(pcx); }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementOutput starts");
		if(expression != null) { expression.codeGen(pcx); }
		o.println("\tMOV\t#0xFFE0, R1\t; StatementOutput: 0xFFE0をR1に");
		o.println("\tMOV\t-(R6), (R1)\t; StatementOutput: 出力値を#0xFFE0を番地を参照して出力");
		o.println(";;; statementOutput completes");
	}
}