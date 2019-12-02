//実験5　テストケース1　SemanticTest.c
// 代入文に関する構文解析＆意味解析テスト
//
// ident.javaのsemanticCheckメソッドのところは、こうなっているはず
//
// public void semanticCheck(CParseContext pcx) throws FatalErrorException {
// 	this.setCType(CType.getCType(CType.XXXXX));					// 	this.setConstant(YYYYY);
// }

// (1) XXXXX を整数型 T_int に、YYYYYをfalseにして
//a=0;	// 正当（生成コードが正しいかどうかも確認）
//*a=1;	// 不当
//a[3]=1;	// 不当
//a=&1;	// 不当
//a=0	// 構文解析エラー（セミコロンなし）
//a 0;	// 構文解析エラー（＝なし）

// (2) XXXXX をポインタ型 T_pint に、YYYYYをfalseにして
//a=1;	// 不当
//a=&1;	// 正当（生成コードが正しいかどうかも確認）
//*a=1;	// 正当（生成コードが正しいかどうかも確認）
//*10=1;	// 構文解析エラー

//a=&1;*a=1;	// 正当（複数文正当なら、両方とも解析とコード生成できることを確認せよ）

// (3) XXXXX を配列型 T_array（人によってこの名前は異なる）に、YYYYYをfalseにして
//a=1;	// 不当
//a=a;	// 不当（正当だとすると、配列全体をごっそりコピーするのですか？）
//a[3]=1;	// 正当（生成コードが正しいかどうかも確認）
//a[3=1;	// 構文解析エラー（]が閉じてない）
//a 3]=1;	// 構文解析エラー（[が開いてない…「＝がない」というエラーになるはず）

// (4) XXXXX整数型 T_int に、YYYYYをtrueにして（定数には代入できないことの確認）
a=1;	// 不当

//実験5　テストケース2　TokenTest.c
//a/*comment*/=4;		// これは「a=4;」。= が消えてなくなっていないか？

