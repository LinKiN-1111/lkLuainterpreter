package org.example.ch09;

import org.example.ch09.api.LuaState;
import org.example.ch09.api.LuaType;
import org.example.ch09.state.LuaStateImpl;

import java.nio.file.Files;
import java.nio.file.Paths;


public class main {
    public static void main(String[] args) throws Exception {
        String test = "C:\\Users\\linkin\\OneDrive\\luainterpreter\\lua\\ch08\\luac.out";
        byte[] data =  Files.readAllBytes(Paths.get(test));
        LuaState ls = new LuaStateImpl();
        ls.load(data, "main", "b");
        ls.call(0, 0);
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
