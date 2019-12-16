package lang.c.parse;

import java.io.PrintStream;
import lang.*;
import lang.c.*;
import java.util.ArrayList;

public class ConstDecl extends CParseRule {
	// constDecl ::= CONST INT constItem { COMMA constItem } SEMI 
	private CParseRule constItem;
	private ArrayList<CParseRule> listconst;
	public ConstDecl(CParseContext pcx) {
		listconst = new ArrayList<CParseRule>();
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		tk = ct.getNextToken(pcx);
		if(tk.getType() == CToken.TK_INT) {
			tk = ct.getNextToken(pcx);
			if(ConstItem.isFirst(tk)) {
				constItem = new ConstItem(pcx);
				constItem.parse(pcx);
				listconst.add(constItem);
				tk = ct.getCurrentToken(pcx);
				while(tk.getType() == CToken.TK_COMMA){
					tk = ct.getNextToken(pcx);
					if(ConstItem.isFirst(tk)) {
						constItem = new ConstItem(pcx);
						constItem.parse(pcx);
						listconst.add(constItem);
						tk = ct.getCurrentToken(pcx);
					}else {
						pcx.fatalError("constDecl error : COMMAの後にconstItemがありません");
					}
				}
				if(tk.getType() != CToken.TK_SEMI) {
					pcx.fatalError("constDecl error : constItemの後にSEMIがありません");
				}else {
					ct.getNextToken(pcx);
				}
			}else {
				pcx.fatalError("ConstDecl error : constItemがありません");
			}
		}else {
			pcx.fatalError("ConstDecl error : CONSTの後にINTがありません");
		}

	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ConstDecl starts");
		if (constItem != null) {
			for(CParseRule list : listconst) {
				list.codeGen(pcx);
			}
		}
		o.println(";;; ConstDecl completes");
	}
}