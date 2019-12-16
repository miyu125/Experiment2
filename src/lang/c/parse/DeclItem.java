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


public class DeclItem extends CParseRule {
	// declItem :: = [ MULT ] IDENT [ LBRA NUM RBRA ] 

	//private CToken mult,ident,assign,num;
	private CToken num,id;
	private String ident;
	private int ctype;
	private CSymbolTable csymboltable;
	private CSymbolTableEntry search;
	public DeclItem(CParseContext pcx) {
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
			ctype = CType.T_pint;
			tk = ct.getNextToken(pcx);
		}
		if(tk.getType() == CToken.TK_IDENT) {
			ident =tk.getText();
			id = tk;
			tk = ct.getNextToken(pcx);
			if(tk.getType() == CToken.TK_LBRA) {
				ctype = (ctype == CType.T_int) ? CType.T_array : CType.T_parray;
				tk = ct.getNextToken(pcx);
				if(tk.getType() == CToken.TK_NUM) {
					num = tk;
					tk = ct.getNextToken(pcx);
					if(tk.getType() == CToken.TK_RBRA) {
						tk = ct.getNextToken(pcx);				
					}else {
						pcx.fatalError("DeclItem error : NUMの後にRBRAがありません");
					}
				}else {
					pcx.fatalError("DeclItem error : IDENTの後にASSIGNがありません");
				}
			}
		}else {
			pcx.fatalError("DeclItem error : IDENTがありません");
		}
		search = pcx.getCSymbolTable().getCSymbolTableGlobal().search(ident);
		if(search == null) {
			CSymbolTableEntry symTable= new CSymbolTableEntry(CType.getCType(ctype),((num != null) ? num.getIntValue() : 1), false, true, 0);
			pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident, symTable);
		}else {
			pcx.error(tk.toExplainString()+"DeclItem error : 多重定義があります");
		}

		//System.out.print("ident = "+pcx.getCSymbolTable().getCSymbolTableGlobal()+"***aaa\n");
		//if(pcx.getCSymbolTable().getCSymbolTableGlobal().register(ident.getText(), symTable) != null) {}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; DeclItem starts");
		//o.println(ident + ":\t; declItem:");
		if(ctype == CType.T_int || ctype == CType.T_pint) {
			o.println(ident + ":\t.WORD\t0\t; declItem:");
		}else {
			o.println(ident + ":\t.BLKW\t" + num.getIntValue() +"\t; declItem:");
		}
		o.println(";;; DeclItem completes");
	}
}