package lang.c.parse;

import java.io.PrintStream;

import lang.FatalErrorException;
import lang.c.CParseContext;
import lang.c.CParseRule;
import lang.c.CToken;
import lang.c.CTokenizer;
import lang.c.CType;

public class PrimaryMult extends CParseRule {
	// primary ::= MULT variable
	private CParseRule variable;
	private CToken op;
	public PrimaryMult(CParseContext pcx) {
	}
	public static boolean isFirst(CToken tk) {
		return tk.getType()==CToken.TK_AST;
	}
	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		op = tk;
		tk = ct.getNextToken(pcx);
		if(Variable.isFirst(tk)) {
			variable = new Variable(pcx);
			variable.parse(pcx);
		}else {
			pcx.error(tk.toExplainString()+"*の次にvariableがありません");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (variable != null) {
			variable.semanticCheck(pcx);
			if(variable.getCType().getType() == CType.T_parray) {
				setCType(CType.getCType(CType.T_pint));
			}
			if(variable.getCType().getType() == CType.T_array) {
				pcx.fatalError("T_array に関するエラー : PrimaryMult semanticCheck");
			}
			if(variable.getCType().getType() == CType.T_parray){
				pcx.fatalError("T_parrayに関するエラー : Primarymult semanticCheck");
			}else {
				if(variable.getCType().getType() != CType.T_int) {
					setCType(CType.getCType(CType.T_int));
				}else {
					pcx.fatalError("T_intに関するエラー : PrimaryMult semanticCheck");
				}	
			}
			setConstant(variable.isConstant());
		}

	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; primaryMult starts");
		if (variable != null) { 
			variable.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<"
					+ op.toExplainString() + ">");
			o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
		}
		o.println(";;; primaryMult completes");
	}
}