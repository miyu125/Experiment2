//実験3　テストケース1　CodeTest.c
// 生成されたコードの正当性は、次のテストでチェックせよ
//-4
//(1+2)*3										//(整数+整数)*整数		先に足し算
//1+2*3											//整数+整数*整数			後から足し算
//1/(2-3)										//整数/(整数―整数)		先に引き算
//1/2-3											//整数/整数-整数			後から引き算



//実験3　テストケース2　SemanticTest.c
// 意味解析エラーのチェック（すべてが不当）
//-&10											//-ポインタ
//(&1-2)*3										//ポインタ*整数
//(&1+2)/3										//ポインタ/整数
//1*(&2+3)										//整数*ポインタ
//1/(&2-3)										//整数/ポインタ


//実験3　テストケース2　TokenTest.c
// 次の２行にエラーはない
//(1+2)*3/-(4-5)
//+4--5++2
// 次のものはエラー（コメントが閉じていない）
//(1+2)/*3+4


//実験3　テストケース4 必要そうなテストケース
//+&2											//+ポインタ		上手くいく
//&1*&2											//ポインタ*ポインタ	エラー
//&1/&2											//ポインタ/ポインタ	エラー
//+&2+1											//+ポインタ+1
//1+2/(3+4*(5/(6+7))-(8/(9+10-11)))				//()がいっぱい　こうなるはず　1 2 3 4 5 6 7 + / * + 8 9 10 + 11 - / - / +