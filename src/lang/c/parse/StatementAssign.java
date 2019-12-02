package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;

public class StatementAssign extends CParseRule {
	// statement ::= statementAssign
	private CParseRule primary,expression;
	public StatementAssign(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return Primary.isFirst(tk); //PrimaryのisFirstを満たす
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		primary = new Primary(pcx);
		primary.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(tk.getType() == CToken.TK_ASSI) {
			tk = ct.getNextToken(pcx);
			if(Expression.isFirst(tk)) {
				expression = new Expression(pcx);
				expression.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_SEMI) {
					ct.getNextToken(pcx);
				}else {
					pcx.fatalError("Expressionの後に;がありません");
				}
			}else {
				pcx.fatalError("=の後にはExpressionが来ます");
			}
		}else {
			pcx.fatalError("primaryの後に=がありません");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (primary != null && expression != null) {
			primary.semanticCheck(pcx);
			expression.semanticCheck(pcx);
			setCType(expression.getCType());		// number の型をそのままコピー
			setConstant(expression.isConstant());	// number は常に定数
			if(primary.getCType() != expression.getCType()) {
				pcx.fatalError("左辺の型["+primary.getCType()+"]と右辺の型["+expression.getCType()+"]は型が違います");
			}
			if(primary.isConstant()) {
				pcx.fatalError("定数には代入できません");
				
			}
		}else {
			pcx.fatalError("primaryまたはexpressionが空です");
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; StatementAssign starts");
		if (primary != null && expression != null) {
			primary.codeGen(pcx);
			expression.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; StatementAssign: スタックから右辺を取り出す");
			o.println("\tMOV\t-(R0), R1\t; StatementAssign: スタックから左辺を取り出す");
			o.println("\tMOV\tR0, (R1)\t; StatementAssign: 代入");
		}
		o.println(";;; StatementAssign completes");
	}
}