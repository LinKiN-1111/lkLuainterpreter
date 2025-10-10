package org.example.ch13.Chunk;

public class Upvalue {
    private byte Instack;  // 本函数的upvalue，是否指向外层函数的栈(不是则指向外层函数的某个upvalue值)
    private byte Idx;     // upvalue在外层函数中的位置(栈的位置或upval列表中的位置，根据in_stack确定)

    public byte getInstack() {
        return Instack;
    }

    public void setInstack(byte instack) {
        Instack = instack;
    }

    public byte getIdx() {
        return Idx;
    }

    public void setIdx(byte idx) {
        Idx = idx;
    }
}
