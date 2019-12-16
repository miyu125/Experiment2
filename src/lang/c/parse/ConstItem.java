package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CSymbolTableEntry;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;
import lang.c.CSymbolTable;

public class ConstItem extends CParseRule {
	// constItem :: = [ MULT ] IDENT ASSIGN [ AMP ] NUM 

	//private CToken mult, assign, amp, num;
	private CToken mult, num;
	private String ident;
	private int ctype;
	private CSymbolTable csymboltable;
	private CSymbolTableEntry search;
	public ConstItem(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AST || tk.getType() == CToken.TK_IDENT;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		//tk = ct.getNextToken(pcx);
		ctype = CType.T_int;
		if(tk.getType() == CToken.TK_AST) {
			mult = tk;
			ctype = CType.T_pint;
			tk = ct.getNextToken(pcx);
		}			
		if(tk.getType() == CToken.TK_IDENT) {
			ident = tk.getText();
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_ASSI) {
				//assign = tk;
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_AMP) {
					//amp = tk;
					if(mult == null) {
						pcx.fatalError(tk.toExplainString() + "整数型定数にアドレス値は代入できません");
					}
					tk = ct.getNextToken(pcx);
				}else {
					if(mult != null){
						pcx.fatalError(tk.toExplainString() + "ポインタ型定数に整数値は代入できません");
					}
				}
				if(tk.getType() == CToken.TK_NUM) {
					num = tk;	
					tk = ct.getNextToken(pcx);	
				}else {
					pcx.fatalError("ConstItem error : IDENTの後にASSIGNがありません");
				}
			}else {
				pcx.fatalError("ConstItem error : IDENTの後にASSIGNがありません");
			}
		}else {
			pcx.fatalError("ConstItem error : IDENTがありません");
		}
		search = pcx.getCSymbolTable().getCSymbolTableGlobal().search(ident);
		if(search == null) {
			CSymbolTableEntry symTable= new CSymbolTableEntry(CType.getCType(ctype), 1, true, true, 0);
			pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident, symTable);
		}else {
			pcx.error(tk.toExplainString()+"ConstItem error : 多重定義があります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; ConstItem starts");
		o.println(ident+":\t.WORD\t"+ num.getIntValue() +"\t; constItem:");
		o.println(";;; ConstItem completes");
	}
}