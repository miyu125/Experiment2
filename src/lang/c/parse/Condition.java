package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;


public class Condition extends CParseRule{

	private CParseRule expression;
	private CParseRule condition;
	private CToken bool;
	public Condition(CParseContext pcx) {

	}	
	public static boolean isFirst(CToken tk) {
		return Expression.isFirst(tk) || tk.getType() == CToken.TK_TRUE || tk.getType() == CToken.TK_FALSE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if(Expression.isFirst(tk)) {
			expression = new Expression(pcx);
			expression.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			if(ConditionLT.isFirst(tk)){
				condition = new ConditionLT(pcx,expression);
				condition.parse(pcx);
			} else 	if(ConditionLE.isFirst(tk)){
				condition = new ConditionLE(pcx,expression);
				condition.parse(pcx);
			} else 	if(ConditionGT.isFirst(tk)){
				condition = new ConditionGT(pcx,expression);
				condition.parse(pcx);
			} else 	if(ConditionGE.isFirst(tk)){
				condition = new ConditionGE(pcx,expression);
				condition.parse(pcx);
			} else 	if(ConditionEQ.isFirst(tk)){
				condition = new ConditionEQ(pcx,expression);
				condition.parse(pcx);
			} else 	if(ConditionNE.isFirst(tk)){
				condition = new ConditionNE(pcx,expression);
				condition.parse(pcx);
			} else{ 	
				pcx.error(tk.toExplainString() + "expressionの後ろにはcondition●●が来ます");
			}			
		}else if(tk.getType() == CToken.TK_TRUE) {
			bool = tk;
			ct.getNextToken(pcx);			
		}else if(tk.getType() == CToken.TK_FALSE) {
			bool = tk;
			ct.getNextToken(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (condition != null) {
			condition.semanticCheck(pcx);
			setCType(condition.getCType());		// expression の型をそのままコピー
			setConstant(condition.isConstant());
		}else {
			setCType(CType.getCType(CType.T_bool));		// expression の型をそのままコピー
			setConstant(true);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; Condition starts");
		if (condition != null) { condition.codeGen(pcx); }
		if (bool != null) {
			o.println("\tMOV\t#"+ (bool.getType() == CToken.TK_TRUE ? 1 : 0) +", (R6)+\t; Condition:");
		}
		o.println(";;; Condition completes");
	}

}


class ConditionLT extends CParseRule {
	// Condition LT ::= LT expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionLT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "LTの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionLT error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}
	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition < (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLT: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionLT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLT: set true");
			o.println("\tCMP\tR0, R1\t\t; ConditionLT: R1<R0 = R1-R0<0");
			o.println("\tBRN\tLT" + seq + " \t\t; ConditionLT: CMPで比較→N=1のときLT●へ分岐");
			o.println("\tCLR\tR2\t\t; ConditionLT: set false");
			o.println("LT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLT: 分岐先))set trueで入れておいたtrueをスタックへ");
		}
		o.println(";;;condition < (compare) completes");
	}
}



class ConditionLE extends CParseRule {
	// Condition LE ::= LE expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionLE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_LE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "LEの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionLE error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition <= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionLE: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionLE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionLE: set true");
			o.println("\tCMP\tR0, R1\t\t; ConditionLE: R1<=R0 = R1-R0<=0");
			o.println("\tBRN\tLE" + seq + " \t\t; ConditionLE: R1-R0<0");
			o.println("\tBRZ\tLE" + seq + " \t\t; ConditionLE: R1-R0=0");
			o.println("\tCLR\tR2\t\t; ConditionLE: set false");
			o.println("LE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionLE:");
		}
		o.println(";;;condition <= (compare) completes");
	}
}


class ConditionGT extends CParseRule {
	// Condition GT ::= LT expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionGT(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "LTの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionGT error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}


	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition > (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionGT: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionGT:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGT: set true");
			o.println("\tCMP\tR1, R0\t\t; ConditionGT: R1>R0 = R0-R1<0");
			o.println("\tBRN\tGT" + seq + " \t\t; ConditionGT:　CMPで比較→N=1のとき分岐");
			o.println("\tCLR\tR2\t\t; ConditionGT: set false");
			o.println("GT" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGT: 分岐先))set trueで入れておいたtrueをスタックへ");
		}
		o.println(";;;condition < (compare) completes");
	}
}


class ConditionGE extends CParseRule {
	// Condition GE ::= GE expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionGE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_GE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "GEの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionGE error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition >= (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionGE: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionGE:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionGE: set true");
			o.println("\tCMP\tR1, R0\t\t; ConditionGE: R1>=R0 = R0-R1>=0");
			o.println("\tBRN\tGE" + seq + " \t\t; ConditionGE: R1-R0<0");
			o.println("\tBRZ\tGE" + seq + " \t\t; ConditionGE: R1-R0=0");
			o.println("\tCLR\tR2\t\t; ConditionGE: set false");
			o.println("GE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionGE:");
		}
		o.println(";;;condition >= (compare) completes");
	}
}



class ConditionEQ extends CParseRule {
	// Condition EQ ::= EQ expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionEQ(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_EQ;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "EQの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionEQ error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition == (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionEQ: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionEQ:");
			o.println("\tMOV\t#0x0001, R2\t; ConditionEQ: set true");
			o.println("\tCMP\tR0, R1\t\t; ConditionEQ: R1==R0 = R1-R0=0");
			o.println("\tBRZ\tEQ" + seq + " \t\t; ConditionEQ: R1-R0=0");
			o.println("\tCLR\tR2\t\t; ConditionEQ: set false");
			o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; ConditionEQ:");
		}
		o.println(";;;condition == (compare) completes");
	}
}

class ConditionNE extends CParseRule {
	// Condition NE ::= NE expression
	private CParseRule left, right;
	//private CToken op;
	public ConditionNE(CParseContext pcx, CParseRule left) {
		this.left = left;
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_NE;
	}
	public void parse(CParseContext pcx) throws FatalErrorException{
		CTokenizer ct = pcx.getTokenizer();
		//op = ct.getCurrentToken(pcx);
		CToken tk = ct.getNextToken(pcx);
		if(Expression.isFirst(tk)) {
			right = new Expression(pcx);
			right.parse(pcx);
		}else {
			pcx.fatalError(tk.toExplainString() + "NEの後ろはExpressionです");
		}
	}
	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			if (!left.getCType().equals(right.getCType())) {
				pcx.fatalError("ConditionNE error : 左辺の型[" + left.getCType().toString() + "] と右辺の型["
						+ right.getCType().toString() + "] が一致しないので比較できません");
			} else {
				this.setCType(CType.getCType(CType.T_bool));
				this.setConstant(true);
			}
		}
	}


	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; condition != (compare) starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			int seq = pcx.getSeqId();
			o.println("\tMOV\t-(R6), R0\t; ConditionNE: ２数を取り出して、比べる");
			o.println("\tMOV\t-(R6), R1\t; ConditionNE:");
			o.println("\tCLR\tR2\t\t; ConditionNE: set false");
			o.println("\tCMP\tR0, R1\t\t; ConditionNE: R1!=R0 = R1-R0!=0");
			o.println("\tBRZ\tNE" + seq + " \t\t; ConditionNE: R1-R0=0");
			o.println("\tMOV\t#0x0001, R2\t; ConditionNE: set true");
			o.println("NE" + seq + ":\tMOV\tR2, (R6)+\t; ConditionNE:");
		}
		o.println(";;;condition != (compare) completes");
	}
}
