package org.example.ch03.Chunk;

public class Upvalue {
    private byte Instack;
    private byte Idx;

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
