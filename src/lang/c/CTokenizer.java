package lang.c;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule	rule;
	private int			lineNo, colNo;
	private char		backCh;
	private boolean		backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1; colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n')  { colNo = 1; ++lineNo; }
		//		System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}
	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') { --lineNo; }
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;
	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}
	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
				//System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}
	private CToken readToken() {
		CToken tk = null;
		char ch;
		int  startCol = colNo;
		StringBuffer text = new StringBuffer();

		int state = 0;
		boolean accept = false;
		while (!accept) {
			switch (state) {
			case 0:					// 初期状態
				text.setLength(0);
				ch = readChar();
				if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
				} else if (ch == (char) -1) {	// EOF
					startCol = colNo - 1;
					state = 1;
				} else if (ch >= '1' && ch <= '9') {//0以外の数を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 3;
				}else if (ch == '0') {//0を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 10;
				} else if (ch == '+') {//プラスを読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 4;
				} else if (ch == '-') {//マイナスを読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 5;
				} else if (ch == '/') {//コメント行かな
					startCol = colNo - 1;
					text.append(ch);
					state = 6;
				}else if (ch == '&') {//&を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 11;
				}else if (ch == '*') {//*を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 15;
				}else if (ch == '(') {//(を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 16;
				}else if (ch == ')') {//)を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 17;
				}else if (ch == '[') {//[を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 19;
				}else if (ch == ']') {//]を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 20;
				}else if ((ch >= 'A' && ch <= 'Z')||(ch >= 'a' && ch <= 'z')) {//英字を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 18;
				}else if(ch == '=') {
					startCol = colNo - 1;
					text.append(ch);
					state = 21;					
				}else if(ch == ';') {
					startCol = colNo - 1;
					text.append(ch);
					state = 22;				
				}else {			// ヘンな文字を読んだ
					startCol = colNo - 1;
					text.append(ch);
					state = 2;
				}
				break;
			case 1:					// EOFを読んだ
				tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
				accept = true;
				break;
			case 2:					// ヘンな文字を読んだ
				tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 3:					// 数（10進数）の開始
				//System.out.print("case3 : ");
				ch = readChar();
				if (Character.isDigit(ch)) {
					text.append(ch);
				} else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					if(Integer.decode(text.toString()) > 65535 || Integer.decode(text.toString()) < 0) {//16bit符号なし整数の最大値65535以上の時エラー
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, "範囲外の数["+text.toString()+"]");
					}
					accept = true;
				}
				break;
			case 4:					// +を読んだ
				tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
				accept = true;
				break;
			case 5:					// -を読んだ
				tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
				accept = true;
				break;
			case 6:					// /を読んだ:コメント行?
				//System.out.print("case6 : ");
				ch = readChar();
				if(ch == '*') {//複数コメント行
					state = 7;
				}else if(ch == '/') {//単行コメント
					state = 9;
				}else {
					text.append(ch);
					backChar(ch);
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				}
				break;
			case 7:					// /*と来た：複数コメント行
				//System.out.print("case7 : ");
				ch = readChar();
				//System.out.print("readChar"+ch+"\n");
				if(ch == '*') {
					state = 8;
				}else if(ch == (char) -1) {//コメント途中にEOF
					state = 2;
					break;
				}else {
					state = 7;
				}
				break;
			case 8:					// /*の後に*と来た:複数コメント行終わり?
				//System.out.print("case8 : ");
				ch = readChar();
				//System.out.print("readChar"+ch+"\n");
				if(ch == '*') {
					state = 8;
				}else if(ch == '/') {//複数コメント行終わり
					state = 0;
				}else if(ch == (char) - 1) {//コメント途中にEOF
					state = 2;
					break;
				}else {
					state = 7;
				}
				break;
			case 9:					// //と来た:単行コメント行
				ch = readChar();
				if(ch == '\n' || ch == '\r') {//改行が来たら
					state = 0;
				}else if(ch == (char) -1) {//コメント途中にEOF
					state = 1;
				}else {
					state = 9;
				}
				break;
			case 10:				// 0と来た：0or8進数or16進数?
				ch = readChar();
				if(ch == 'x') {// 16進数
					text.append(ch);
					state = 12;					
				}else if(ch >= '0' && ch <= '7') {//8進数だから0~7まで！
					text.append(ch);
					state = 14;					
				}else if(ch == (char) - 1){
					state = 0;
					backChar(ch);
					break;
				}else{
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					if(Integer.decode(text.toString()) > 65535 || Integer.decode(text.toString()) < 0) {//16bit符号なし整数の最大値65535以上の時エラー
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, "範囲外の数["+text.toString()+"]");
					}
					accept = true;					
				}				
				break;
			case 11:				// &と来た
				tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
				accept = true;
				break;
			case 12:				//16進数
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
					text.append(ch);
					state = 13;
				}else if(ch == (char) - 1){
					state = 0;
					backChar(ch);
				}else {				//数にならないのでエラー　ex)0x^
					state = 2;
				}
				break;
			case 13:				//16進数の数値
				ch = readChar();
				if((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
					text.append(ch);
					state = 13;
				}else if(ch == (char) - 1){
					state = 0;
					backChar(ch);
				}else {
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					if(Integer.decode(text.toString()) > 65535 || Integer.decode(text.toString()) < 0) {//16bit符号なし整数の最大値65535以上の時エラー
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, "範囲外の数["+text.toString()+"]");
					}
					accept = true;					
				}
				break;
			case 14:				//8進数
				ch = readChar();
				if(ch >= '0' && ch <= '7') {
					text.append(ch);
					state = 14;
				}else if(ch == (char) - 1){
					state = 0;
					backChar(ch);
				}else if(ch >= '8'){
					state = 2;
					backChar(ch);
				}else{
					// 数の終わり
					backChar(ch);	// 数を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
					if(Integer.decode(text.toString()) > 65535 || Integer.decode(text.toString()) < 0) {//16bit符号なし整数の最大値65535以上の時エラー
						tk = new CToken(CToken.TK_ILL, lineNo, startCol, "範囲外の数["+text.toString()+"]");
					}
					accept = true;					
				}
				break;
			case 15:				//*を読んだ
				tk = new CToken(CToken.TK_AST, lineNo, startCol, "*");
				accept = true;
				break;
			case 16:				//左の括弧'('
				tk = new CToken(CToken.TK_LPAR, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 17:				//右の括弧')'
				tk = new CToken(CToken.TK_RPAR, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 18:				//識別子の開始
				//System.out.print("case18 : ");
				ch = readChar();
				if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
					text.append(ch);
					state=18;
				}else if(ch == (char) - 1){
					state = 0;
					backChar(ch);
					break;
				}else{
					// 識別子の終わり
					backChar(ch);	// 識別子を表さない文字は戻す（読まなかったことにする）
					tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
					accept = true;
				}
				break;
			case 19:				//左の括弧'['
				tk = new CToken(CToken.TK_LBRA, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 20:				//右の括弧']'
				tk = new CToken(CToken.TK_RBRA, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 21:				//'='
				tk = new CToken(CToken.TK_ASSI, lineNo, startCol, text.toString());
				accept = true;
				break;
			case 22:				//';'
				tk = new CToken(CToken.TK_SEMI, lineNo, startCol, text.toString());
				accept = true;
				break;
			}
		}
		return tk;
	}
}
