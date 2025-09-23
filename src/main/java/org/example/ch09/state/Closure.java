package org.example.ch09.state;

import org.example.ch09.Chunk.Prototype;

//第八章只有Prototype，后续会添加
public class Closure {
    private Prototype proto;

    Closure(Prototype proto) {
        this.proto = proto;
    }

    public Prototype getProto() {
        return proto;
    }

}
