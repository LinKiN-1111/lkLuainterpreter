package org.example.ch12.state;

import org.example.ch12.Chunk.Prototype;
import org.example.ch12.api.JavaFunction;

//第八章只有Prototype，后续会添加
public class Closure {
    private Prototype proto;
    private JavaFunction javaproto;
    final UpvalueHolder[] upvals;

    Closure(Prototype proto) {
        this.proto = proto;
        this.javaproto = null;
        this.upvals = new UpvalueHolder[proto.getUpvalues().length];
    }

    Closure(JavaFunction javaproto, int nUpvals) {
        this.proto = null;
        this.javaproto = javaproto;
        this.upvals = new UpvalueHolder[nUpvals];
    }

    public JavaFunction getJavaproto() {
        return javaproto;
    }

    public Prototype getProto() {
        return proto;
    }
}
