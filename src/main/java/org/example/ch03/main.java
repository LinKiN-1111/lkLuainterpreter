package org.example.ch03;

import org.example.ch03.Chunk.BinaryChunk;
import org.example.ch03.Chunk.LocVar;
import org.example.ch03.Chunk.Prototype;
import org.example.ch03.Chunk.Upvalue;
import org.example.ch03.vm.Instruction;
import org.example.ch03.vm.OpCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.example.ch03.vm.Instruction.*;
import static org.example.ch03.vm.OpArgMask.*;


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
//            System.out.printf("\t%d\t[%s]\t0x%08X\n", i+1, line, code[i]);
            System.out.printf("\t%d\t[%s]\t%-8s \t", i + 1, line, OpName(code[i]));
            printOperands(code[i]);
        }
    }

    //添加这个
    private static void printOperands(long i) {
        OpCode opCode = getOpCode(i);
        switch (opCode.getOpMode()) {
            case iABC: {
                int[] abc = Instruction.ABC(i);
                System.out.printf("%d", abc[0]); // A
                if (opCode.getArgBMode() != OpArgN) {
                    System.out.printf(" %d", abc[1] > 0xFF ? -1 - (abc[1] & 0xFF) : abc[1]); // C
                }
                if (opCode.getArgCMode() != OpArgN) {
                    System.out.printf(" %d", abc[2] > 0xFF ? -1 - (abc[2] & 0xFF) : abc[2]); // C
                }
                break;
            }
            case iABx: {
                int[] abx = Instruction.ABx(i);
                System.out.printf("%d", abx[0]); // A
                if (opCode.getArgBMode() == OpArgK) {
                    System.out.printf(" %d", -1 - abx[1]); // Bx as K
                } else if (opCode.getArgBMode() == OpArgU) {
                    System.out.printf(" %d", abx[1]); // Bx as U
                }
                break;
            }
            case iAsBx: {
                int[] asbx = Instruction.AsBx(i);
                System.out.printf("%d %d", asbx[0], asbx[1]); // A and sBx
                break;
            }
            case iAx: {
                int[] ax = Instruction.Ax(i);
                System.out.printf("%d", -1 - ax[0]); // Ax
                break;
            }
        }
        System.out.println(); //换行
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
