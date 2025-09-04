package org.example.ch02;

import jdk.jshell.execution.Util;
import org.example.Utils.TransUtils;
import org.example.ch02.Chunk.BinaryChunk;
import org.example.ch02.Chunk.LocVar;
import org.example.ch02.Chunk.Prototype;
import org.example.ch02.Chunk.Upvalue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class main {
    public static void main(String[] args) throws IOException {
        Undump();
    }

    //解析二进制chunk
    public static void Undump() throws IOException {
        FileInputStream is = new FileInputStream(new File("C:\\Users\\linkin\\OneDrive\\luainterpreter\\lua\\ch02\\luac.out"));
        byte[] input = is.readAllBytes();
        BinaryChunk binaryChunk = new BinaryChunk(input);
        binaryChunk.checkHeader();  //校验头部
        binaryChunk.readByte();     //跳过Upvalue数量
        Prototype prototype = binaryChunk.readProto("");
//        System.out.println(prototype);
        list(prototype);
        is.close();
    }

    private static void list(Prototype f) {
        printHeader(f);
        printCode(f);
        printDetail(f);
        for (Prototype p : f.getProtos()) {
            list(p);
        }
    }
    private static void printHeader(Prototype f) {
        String funcType = f.getLineDefined() > 0 ? "function" : "main";
        String varargFlag = f.getIsVararg() > 0 ? "+" : "";

        System.out.printf("\n%s <%s:%d,%d> (%d instructions)\n",
                funcType, f.getSource(), f.getLineDefined(), f.getLastLineDefined(),
                f.getCode().length);

        System.out.printf("%d%s params, %d slots, %d upvalues, ",
                f.getNumParams(), varargFlag, f.getMaxStackSize(), f.getUpvalues().length);

        System.out.printf("%d locals, %d constants, %d functions\n",
                f.getLocVars().length, f.getConstants().length, f.getProtos().length);
    }

    private static void printCode(Prototype f) {
        long[] code = f.getCode();
        long[] lineInfo = f.getLineInfo();
        for (int i = 0; i < code.length; i++) {
            String line = lineInfo.length > 0 ? String.valueOf(lineInfo[i]) : "-";
            System.out.printf("\t%d\t[%s]\t0x%08X\n", i+1, line, code[i]);
        }
    }

    private static void printDetail(Prototype f) {
        System.out.printf("constants (%d):\n", f.getConstants().length);
        int i = 1;
        for (Object k : f.getConstants()) {
            System.out.printf("\t%d\t%s\n", i++, constantToString(k));
        }

        i = 0;
        System.out.printf("locals (%d):\n", f.getLocVars().length);
        for (LocVar locVar : f.getLocVars()) {
            System.out.printf("\t%d\t%s\t%d\t%d\n", i++,
                    locVar.getVarName(), locVar.getStartPC() + 1, locVar.getEndPC() + 1);
        }

        i = 0;
        System.out.printf("upvalues (%d):\n", f.getUpvalues().length);
        for (Upvalue upval : f.getUpvalues()) {
            String name = f.getUpvalueNames().length > 0 ? f.getUpvalueNames()[i] : "-";
            System.out.printf("\t%d\t%s\t%d\t%d\n", i++,
                    name, upval.getInstack(), upval.getIdx());
        }
    }

    private static String constantToString(Object k) {
        if (k == null) {
            return "nil";
        } else if (k instanceof String) {
            return "\"" + k + "\"";
        } else {
            return k.toString();
        }
    }
}
