package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class StatementIf extends CParseRule {
	// statementIf ::= IF LPAR condition RPAR LCUR {statement} RCUR
	private CParseRule statement, condition,statementifelse;
	private ArrayList<CParseRule> list;
	private int seq;
	public StatementIf(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IF;
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
						if(tk.getType() == CToken.TK_RCUR) {
							tk = ct.getNextToken(pcx);
							if(StatementIfElse.isFirst(tk)) {
								statementifelse = new StatementIfElse(pcx);
								statementifelse.parse(pcx);
							}
						}else {
							pcx.error(tk.toExplainString() + " '}'が足りません");
						}
					}else {
						pcx.error(tk.toExplainString() + " '{'が足りません");
					}
				}else {
					pcx.error(tk.toExplainString() + " ')'が足りません");
				}
			}else {
				pcx.error(tk.toExplainString() + " Conditionが足りません");
			}
		}else {
			pcx.error(tk.toExplainString() + " '('が足りません");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(condition != null) { condition.semanticCheck(pcx); }
		for(CParseRule item : list) {
			if(item != null) item.semanticCheck(pcx);
		}
		if(statementifelse != null) { statementifelse.semanticCheck(pcx); }
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIf starts");
		if(condition != null) { condition.codeGen(pcx); }
		seq = pcx.getSeqId();
		int tmpseq = seq;
		o.println("\tMOV\t-(R6), R0\t; StatementIf: フラグをスタックから取り出す");
		o.println("\tCMP\t#0x0000, R0\t; StatementIf: 0とフラグを比較する");
		o.println("\tBRZ\tELSE" + seq + "\t\t; StatementIf: falseのときELSE●●にジャンプ");
		for(CParseRule item : list) {
			item.codeGen(pcx);
		}
		seq = pcx.getSeqId();
		o.println("\tJMP\tEND" + seq + "\t\t; StatementIf: 分岐終わりまでジャンプ");
		o.println("ELSE" + tmpseq + ":\t\t\t\t; StatementIf: falseの時");
		if(statementifelse != null) { statementifelse.codeGen(pcx); }
		o.println("END" + seq + ":\t\t\t\t; StatementIf:　分岐終わり");
		o.println(";;; statementIf completes");
	}
}