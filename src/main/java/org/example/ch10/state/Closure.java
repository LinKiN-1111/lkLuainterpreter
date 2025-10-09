package org.example.ch10.state;

import org.example.ch10.Chunk.Prototype;
import org.example.ch10.Chunk.Upvalue;
import org.example.ch10.api.JavaFunction;

import java.util.ArrayList;

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
