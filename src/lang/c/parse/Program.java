package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class Program extends CParseRule {
	// program ::= {declaration} {statement} EOF
	private CParseRule statement,declaration;
	private ArrayList<CParseRule> listState,listDecla;

	public Program(CParseContext pcx) {
		listState = new ArrayList<CParseRule>();
		listDecla = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk) || Declaration.isFirst(tk);
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while(Declaration.isFirst(tk)) {
			declaration = new Declaration(pcx);
			declaration.parse(pcx);
			listDecla.add(declaration);
			tk = ct.getCurrentToken(pcx);
		}
		while(Statement.isFirst(tk)) {
			statement = new Statement(pcx);
			statement.parse(pcx);
			listState.add(statement);
			tk = ct.getCurrentToken(pcx);
		}
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (declaration != null) { 
			for(CParseRule listdecla : listDecla ) {
				if(listdecla != null) {
					declaration.semanticCheck(pcx);	
				}
			}
		}		
		if (statement != null) { 
			for(CParseRule liststate : listState ) {
				if(liststate != null) {
					statement.semanticCheck(pcx);	
				}
			}
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// ここには将来、宣言に対するコード生成が必要
		if (declaration != null) {
			for(CParseRule listdecla : listDecla) {
				if(listdecla != null) {	
					listdecla.codeGen(pcx);
				}
			}
		}
		if (statement != null) {
			o.println("__START:");
			o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
			for(CParseRule liststate : listState) {
				if(liststate != null) {	
					liststate.codeGen(pcx);
				}
			}
			//o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		}
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}
