package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class IntDecl extends CParseRule {
	// intDecl ::= INT declItem { COMMA declItem } SEMI 
	private CParseRule declItem;
	private ArrayList<CParseRule> listdecl;
	public IntDecl(CParseContext pcx) {
		listdecl = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_INT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(DeclItem.isFirst(tk)) {
			declItem = new DeclItem(pcx);
			declItem.parse(pcx);
			listdecl.add(declItem);
			tk = ct.getCurrentToken(pcx);
			while(tk.getType() == CToken.TK_COMMA){
				tk = ct.getNextToken(pcx);
				if(DeclItem.isFirst(tk)) {
					declItem = new DeclItem(pcx);
					declItem.parse(pcx);
					listdecl.add(declItem);
					tk = ct.getCurrentToken(pcx);
				}else {
					pcx.fatalError("IntDecl error : COMMAの後にdeclItemがありません");
				}
			}
			if(tk.getType() != CToken.TK_SEMI) {
				pcx.fatalError("IntDecl error : declItemの後にSEMIがありません");
			}else {
				ct.getNextToken(pcx);
			}
		}else {
			pcx.fatalError("IntDecl error : declItemがありません");
		}

	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; IntDecl starts");
		if (declItem != null) {
			for(CParseRule list : listdecl) {
				list.codeGen(pcx);
			}
		}
		o.println(";;; IntDecl completes");
	}
}