package org.example.ch03.vm;

//这部分需要具体而论的,后面用到再深入理解
public enum OpArgMask {
    OpArgN, // argument is not used
    OpArgU, // argument is used
    OpArgR, // argument is a register or a jump offset
    OpArgK, // argument is a constant or register/constant
    ;
}
