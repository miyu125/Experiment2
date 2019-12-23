package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class StatementWhile extends CParseRule {
	// statementWhile ::= WHILE LPAR condition RPAR LCUR {statement} RCUR
	private CParseRule condition,statement;
	private ArrayList<CParseRule> list;
	private int seq;
	public StatementWhile(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_WHILE; //PrimaryのisFirstを満たす
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		list = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_LPAR) {
			tk = ct.getNextToken(pcx);
			if(Condition.isFirst(tk)) {
				condition = new Condition(pcx);
				condition.parse(pcx);
				tk = ct.getCurrentToken(pcx);
				if(tk.getType() == CToken.TK_RPAR) {
					tk = ct.getNextToken(pcx);
					if(tk.getType() == CToken.TK_LCUR) {
						tk = ct.getNextToken(pcx);
						while(Statement.isFirst(tk)) {
							statement = new Statement(pcx);
							statement.parse(pcx);
							list.add(statement);
							tk = ct.getCurrentToken(pcx);
						}
						tk = ct.getCurrentToken(pcx);
						if(tk.getType() == CToken.TK_RCUR) {
							ct.getNextToken(pcx);
						}else {
							pcx.error(tk.toExplainString() + "RCURが足りません");
						}
					}else {
						pcx.error(tk.toExplainString() + " LCURが足りません");
					}
				}else {
					pcx.error(tk.toExplainString() + " RPARが足りません");
				}
			}else {
				pcx.error(tk.toExplainString() + " conditionが足りません");
			}
		}else {
			pcx.error(tk.toExplainString() + " LPARが足りません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null) { condition.semanticCheck(pcx); }
		for(CParseRule item : list) {
			if(item != null) item.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		int tmpseq;
		o.println(";;; statementWhile starts");
		seq = pcx.getSeqId();
		tmpseq = seq;
		o.println("WHILE" + seq + ":\t\t\t\t; StatementWhile:");
		if(condition != null) { condition.codeGen(pcx); }
		o.println("\tMOV\t-(R6), R0\t; StatementWhile: フラグを取り出す");
		o.println("\tCMP\t#0x0000, R0\t; StatementWhile: 0と比較");
		seq = pcx.getSeqId();
		o.println("\tBRZ\tELSE" + seq + "\t\t; StatementWhile: falseならジャンプ");
		for(CParseRule item : list) {
			if(item != null) item.codeGen(pcx);
		}
		o.println("\tJMP\tWHILE"+ tmpseq +"\t\t;　StatementWhile: ");
		o.println("ElSE" + seq + ":\t\t\t\t; StatementWhile:");
		o.println(";;; statementWhile completes");
	
	}
}