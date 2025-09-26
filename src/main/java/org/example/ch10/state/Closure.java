package org.example.ch09.state;

import org.example.ch09.Chunk.Prototype;
import org.example.ch09.api.JavaFunction;

//第八章只有Prototype，后续会添加
public class Closure {
    private Prototype proto;
    private JavaFunction  javaproto;
    Closure(Prototype proto) {
        this.proto = proto;
        this.javaproto = null;
    }

    Closure(JavaFunction javaproto) {
        this.proto = null;
        this.javaproto = javaproto;
    }

    public JavaFunction getJavaproto() {
        return javaproto;
    }

    public Prototype getProto() {
        return proto;
    }
}
