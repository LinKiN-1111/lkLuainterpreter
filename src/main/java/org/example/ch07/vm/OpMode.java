package org.example.ch07.vm;

//只有sBx的操作数会被解释为有符号整数,其它都是无符号
public enum OpMode {
           // 31                             0
    iABC , // [  B:9  ][  C:9  ][ A:8  ][OP:6]
    iABx , // [      Bx:18     ][ A:8  ][OP:6]
    iAsBx, // [     sBx:18     ][ A:8  ][OP:6]
    iAx  , // [           Ax:26        ][OP:6]
    ;
}
