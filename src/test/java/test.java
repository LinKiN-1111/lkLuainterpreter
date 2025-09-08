import org.example.ch04.api.LuaState;
import org.example.ch04.state.LuaStateImpl;

import java.util.ArrayList;
import java.util.Collections;

public class test {

    public static void main(String[] args) {
        LuaStateImpl luaState = new LuaStateImpl();
        luaState.pushInteger(1);
        luaState.pushInteger(2);
        luaState.pushInteger(3);
        luaState.pushBoolean(Boolean.TRUE);
        luaState.pushBoolean(Boolean.FALSE);
        luaState.pushString("Hello");
        luaState.pushString("World");

        System.out.println(luaState.isInteger(2));
        System.out.println(luaState.getTop());

//        luaState.rotate(3,1);
//        luaState.printStackState();
    }
}
