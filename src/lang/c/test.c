//実験6　テストケース1　TokenTest.c
//int a,b,c,d;
//const int *a=&45;



//実験6　テストケース2　ParseTest.c
// 変数宣言の構文解析テスト

// この２行は問題なし
//int a, *b, c[10], *d[10];
//const int e=10, *f=&30;

//int a, *b, c[10] *d[10];	// コンマが抜けてる
//int *d[10]			// セミコロンがない
//int 10;			// 識別子無し
//int c[10;			// ]が閉じてない
//const int e;			// 初期値がない
//const int e=3			// セミコロンがない
//const int e 3;		// ＝が
//int e=3;			// constがない（＝のところに,か;がないエラー…と出るはず）



//実験6　テストケース3　semanticTest.c
// 変数宣言の意味解析テスト

int a, *b, c[10], *d[10];
const int e=10, *f=&30;

// (1) 初期値の型違い
//const int g=&10;

// (2) 二重宣言
//const int a=10;
//int *c;
//int e[3];

// (3) 未宣言変数の使用
//z = 0;
//a = z+2;

// (4) 宣言と使い方の不一致
//*a=0;
//a=*c;
//b=a[4];
//c=c;	// 配列をごっそりコピーするの？

// (5) 許されない演算
//a=b*2;
//a=c*2;

// (6) 定数に代入
a=3;

