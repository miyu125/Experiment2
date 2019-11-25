package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class termDiv extends CParseRule {
	// factor ::= Div factor
	private CToken op;
    private CParseRule left, right;
	public termDiv(CParseContext pcx,CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;//最初に'*'があるか
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError("*の後ろはfactorが来ます");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// 割り算の型計算規則
		final int s[][] = {
		//		T_err			T_int			T_pint
			{	CType.T_err,	CType.T_err, 	CType.T_err},	// T_err
			{	CType.T_err,	CType.T_int,	CType.T_err},	// T_int
			{	CType.T_err,	CType.T_err,	CType.T_err}	//　T_pint
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType();		// *の左辺の型
			int rt = right.getCType().getType();	// *の右辺の型
			int nt = s[lt][rt];						// 規則による型計算
			if (nt == CType.T_err) {
				pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型[" + right.getCType().toString() + "]は割れません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant());	// /の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; termDiv starts");
		if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            o.println("\tJSR\tDIV\t\t; termDiv:サブルーチン呼び出し");
            o.println("\tSUB\t#2, R6\t\t; termDiv:積んだスタックを捨てる");
            o.println("\tMOV\tR0, (R6)+\t; termDiv:結果をスタックに積む");
        }
		o.println(";;; termDiv completes");
	}
}