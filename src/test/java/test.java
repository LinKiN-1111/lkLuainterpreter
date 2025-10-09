import org.example.ch04.api.LuaState;
import org.example.ch04.state.LuaStateImpl;
import org.example.ch07.state.LuaTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class test {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("1", "2", "3");
        List<String> list2 = new ArrayList<>();
        list2.addAll(list);
        list.set(2,"test");
        System.out.println(list);
        System.out.println(list2);

    }
}
