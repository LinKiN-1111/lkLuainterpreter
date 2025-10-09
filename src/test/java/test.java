import org.example.ch04.api.LuaState;
import org.example.ch04.state.LuaStateImpl;
import org.example.ch07.state.LuaTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class test {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        System.out.println("原列表: " + list);

// 反转索引 3 到 6（即元素 3,4,5,6）
        Collections.reverse(list.subList(7, 3));  // 7 是 exclusive
        System.out.println("反转后: " + list);

    }
}
