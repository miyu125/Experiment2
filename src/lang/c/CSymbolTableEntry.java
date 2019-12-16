package lang.c;

import lang.*;

public class CSymbolTableEntry extends SymbolTableEntry {
	private CType type; // この識別子に対して宣言された型
	private int size; // メモリ上に確保すべきワード数
	private boolean constp; // 定数宣言か？
	private boolean isGlobal; // 大域変数か？
	private int address; // 割り当て番地
	public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGlobal, int addr) {
		this.type = type;
		this.size = size;
		this.constp = constp;
		this.isGlobal = isGlobal;
		this.address = addr;
	}
	
	public CType gettype() {
		return this.type;
	}
	public int getsize() {
		return this.size;
	}
	public boolean getconstp() {
		return this.constp;
	}
	public boolean getisGlobal() {
		return this.isGlobal;
	}
	public int getaddress() {
		return this.address;
	}	
	
	public String toExplainString() { // このエントリに関する情報を作り出す。記号表全体を出力するときに使う。
		return type.toString() + ", " + size + (constp ? "定数" : "変数");
	}
}