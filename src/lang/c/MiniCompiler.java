package lang.c;

import lang.*;
import lang.c.parse.*;

public class MiniCompiler {
	public static void main(String[] args) {
		String inFile = args[0]; // 適切なファイルを絶対パスで与えること
		IOContext ioCtx = new IOContext(inFile, System.out, System.err);
		CTokenizer tknz = new CTokenizer(new CTokenRule());
		CParseContext pcx = new CParseContext(ioCtx, tknz);
		try {
			CTokenizer ct = pcx.getTokenizer();
			CToken tk = ct.getNextToken(pcx);
			if (Program.isFirst(tk)) {
				CParseRule parseTree = new Program(pcx);
				parseTree.parse(pcx);									// 構文解析
				if (pcx.hasNoError()) parseTree.semanticCheck(pcx);		// 意味解析
				if (pcx.hasNoError()) parseTree.codeGen(pcx);			// コード生成
				pcx.errorReport();
			} else {
				pcx.fatalError(tk.toExplainString() + "プログラムの先頭にゴミがあります");
			}
		} catch (FatalErrorException e) {
			e.printStackTrace();
		}
	}
}
