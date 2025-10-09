package org.example.ch10;

import org.example.ch10.api.LuaState;
import org.example.ch10.api.LuaType;
import org.example.ch10.state.LuaStateImpl;

import java.nio.file.Files;
import java.nio.file.Paths;


public class main {
    public static void main(String[] args) throws Exception {
        String test = "C:\\Users\\linkin\\OneDrive\\luainterpreter\\lua\\ch10\\luac.out";
        byte[] data =  Files.readAllBytes(Paths.get(test));
        LuaState ls = new LuaStateImpl();           //创建虚拟机
        ls.register("print", main::print);   //全局变量设置printf,那么就能调用大java函数了
        ls.load(data, "main", "b");
        ls.call(0, 0);
    }


    private static int print(LuaState ls) {
        int nArgs = ls.getTop();
        for (int i = 1; i <= nArgs; i++) {
            if (ls.isBoolean(i)) {
                System.out.print(ls.toBoolean(i));
            } else if (ls.isString(i)) {
                System.out.print(ls.toString(i));
            } else {
                System.out.print(ls.typeName(ls.type(i)));
            }
            if (i < nArgs) {
                System.out.print("\t");
            }
        }
        System.out.println();
        return 0;
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
