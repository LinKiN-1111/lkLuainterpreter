package org.example.ch07;

import org.example.ch07.Chunk.BinaryChunk;
import org.example.ch07.Chunk.Prototype;
import org.example.ch07.api.LuaState;
import org.example.ch07.api.LuaType;
import org.example.ch07.api.LuaVM;
import org.example.ch07.state.LuaStateImpl;
import org.example.ch07.vm.Instruction;
import org.example.ch07.vm.OpCode;

import java.nio.file.Files;
import java.nio.file.Paths;


public class main {
    public static void main(String[] args) throws Exception {
        String test = "C:\\Users\\linkin\\OneDrive\\luainterpreter\\lua\\ch07\\luac.out";
        byte[] data = Files.readAllBytes(Paths.get(test));
        Prototype proto = BinaryChunk.unDump(data);
        luaMain(proto);
    }

    //通过这个来分派指令
    private static void luaMain(Prototype proto) {
        LuaVM vm = new LuaStateImpl(proto);
        vm.setTop(proto.getMaxStackSize());
        for (;;) {
            long pc = vm.getPC();
            long i = vm.fetch();
            OpCode opCode = Instruction.getOpCode(i);
            if (opCode != OpCode.RETURN) {
                opCode.getAction().execute((int)i, vm);
                System.out.printf("[%02d] %-8s ", pc+1, opCode.name());
                printStack(vm);
            } else {
                break;
            }
        }
    }

    private static void printStack(LuaState ls) {
        int top = ls.getTop();
        for (int i = 1; i <= top; i++) {
            LuaType t = ls.type(i);
            switch (t) {
                case LUA_TBOOLEAN:
                    System.out.printf("[%b]", ls.toBoolean(i));
                    break;
                case LUA_TNUMBER:
                    if (ls.isInteger(i)) {
                        System.out.printf("[%d]", ls.toInteger(i));
                    } else {
                        System.out.printf("[%f]", ls.toNumber(i));
                    }
                    break;
                case LUA_TSTRING:
                    System.out.printf("[\"%s\"]", ls.toString(i));
                    break;
                default: // other values
                    System.out.printf("[%s]", ls.typeName(t));
                    break;
            }
        }
        System.out.println();
    }
}
