package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class StatementIfElse extends CParseRule {
	// statementIfElse ::= statementIF [ELSE (statementIfElse | LCUR {statment} RCUR]
	private CParseRule statementif,statement;
	private ArrayList<CParseRule> list;
	public StatementIfElse(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_ELSE; //StatementのisFirstを満たす
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		list = new ArrayList<CParseRule>();
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(StatementIf.isFirst(tk)) {
			statementif = new StatementIf(pcx);
			statementif.parse(pcx);
			tk = ct.getCurrentToken(pcx);
		}else if(tk.getType() == CToken.TK_LCUR){
			tk = ct.getNextToken(pcx);
			while(Statement.isFirst(tk)) {
				statement = new Statement(pcx);
				statement.parse(pcx);
				list.add(statement);
				tk = ct.getCurrentToken(pcx);
			}
			if(tk.getType() == CToken.TK_RCUR) {
				ct.getNextToken(pcx);
			}else {
				pcx.fatalError("statementIfElse error : RCURが足りません");
			}
		}else {
			pcx.fatalError("statementIfElse error : StatementIf または LCURが足りません");
		}
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if(statementif != null) { statementif.semanticCheck(pcx); }
		for(CParseRule item : list) {
			if(item != null) item.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; statementIfElse starts");
		if(statementif != null) { statementif.codeGen(pcx); }
		for(CParseRule item : list) {
			if(item != null)item.codeGen(pcx);
		}
		o.println(";;; statementIfElse completes");
	}
}