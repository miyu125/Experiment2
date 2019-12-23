package lang.c;

import java.util.HashMap;

public class CTokenRule extends HashMap<String, Object> {
	private static final long serialVersionUID = 1139476411716798082L;

	public CTokenRule() {
		put("int",		new Integer(CToken.TK_INT));
		put("const",new Integer (CToken.TK_CONST));
		put("true",new Integer(CToken.TK_TRUE));
		put("false",new Integer(CToken.TK_FALSE));
		put("if",		new Integer(CToken.TK_IF));
		put("else",new Integer (CToken.TK_ELSE));
		put("while",new Integer(CToken.TK_WHILE));
		put("input",new Integer(CToken.TK_INPUT));
		put("output",new Integer(CToken.TK_OUTPUT));
	}
}
