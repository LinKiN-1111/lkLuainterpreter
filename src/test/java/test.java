import org.example.ch04.api.LuaState;
import org.example.ch04.state.LuaStateImpl;
import org.example.ch07.state.LuaTable;

import java.util.ArrayList;
import java.util.Collections;

public class test {

    public static void main(String[] args) {
        LuaTable t = new LuaTable(0, 0);
        t.put(1L, "a");
        t.put(2L, "b");
        t.put(3L, "c");
        t.put(4L, "d");
        t.put(5L, "e");
        t.put(6L, "f");
        t.put(7L, "g");
        t.put(2L, null);
        t.put(7L, null);
        System.out.println(t.length());  // 应该返回3
        System.out.println(t.get(1L));   // "a"
        System.out.println(t.get(2L));   // "c"
        System.out.println(t.get(3L));   // "d"
        System.out.println(t.get(4L));   // "e"

    }
}
