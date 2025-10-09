package org.example.ch12.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.example.ch12.api.LuaState.LUA_REGISTRYINDEX;

public class LuaStack {
    private final ArrayList<Object> slots;

    /* call info */
    public LuaStateImpl state;
    public Closure closure;         //看来代表一个函数的特点
    public List<Object> varargs;    //应该用于记录传入的参数
    public long pc;
    /* linked list */
    public LuaStack prev;

    Map<Integer, UpvalueHolder> openuvs;


    public LuaStack(int size) {
        //默认就new一个slots了..

        slots = new ArrayList<>(size);
    }

    public LuaStack() {
        //默认就new一个slots了..
        slots = new ArrayList<>();
    }

    //chack (这个是控制容量的,由于java直接使用array实现了,这里就不用控制了)

    //获取这个栈的大小
    int top() {
        return slots.size();
    }

    //在末尾增加一个元素
    public void push(Object val) {
        if (slots.size() > 10000) {
            throw new StackOverflowError();
        }
        this.slots.add(val);
    }

    //推入n个元素不够的推入null补充
    void pushN(List<Object> vals, int n) {
        int nVals = vals == null ? 0 : vals.size();
        if (n < 0) {
            n = nVals;
        }
        for (int i = 0; i < n; i++) {
            push(i < nVals ? vals.get(i) : null);
        }
    }

    //在末尾弹出一个元素
    public Object pop() {
        if(this.slots.size() == 0){
            throw new RuntimeException("Stack is empty");
        }
        return this.slots.remove(this.slots.size() - 1);
    }

    //弹出N个元素
    List<Object> popN(int n) {
        List<Object> vals = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vals.add(pop());
        }
        Collections.reverse(vals);   //恢复原来的顺序
        return vals;
    }


    //把索引改为绝对索引,这个改出来的还是lua的索引,并不是slots的索引
    public int absIndex(int idx) {
        if(idx <= LUA_REGISTRYINDEX){
            return idx;
        }
        return idx >= 0 ? idx : idx + slots.size()+ 1;
    }

    //判断这个索引是否合法
    public boolean isValid(int index){
        if(index < LUA_REGISTRYINDEX){   //小于的情况就是获取upvalue
            int uvIdx = LUA_REGISTRYINDEX - index- 1;
            return this.closure != null && uvIdx < closure.upvals.length;
        }
        if(index == LUA_REGISTRYINDEX){   //获取注册表
            return true;
        }
        int absIdx = absIndex(index);
        return absIdx > 0 && absIdx <= slots.size() ;
    }

    //通过索引获取栈中元素，注意索引要-1，因为lua的数组是从1开始的
    public Object get(int index){
        if(index < LUA_REGISTRYINDEX){
            int uvIdx = LUA_REGISTRYINDEX - index - 1;
            if(this.closure != null && uvIdx < closure.upvals.length && closure.upvals[uvIdx] != null){
                return closure.upvals[uvIdx].get();
            }
            return null;
        }
        if(index == LUA_REGISTRYINDEX){
            return this.state.registry;
        }
        int absIdx = absIndex(index);
        //isValia是对传入的进行判断的
        if(isValid(index)){
            return slots.get(absIdx-1);    //lua索引对应到stack中需要-1
        }

        throw new IndexOutOfBoundsException();
    }

    //set根据索引往栈里面写入
    public boolean set(int index,Object val){
        if(index < LUA_REGISTRYINDEX){
            int uvIdx = LUA_REGISTRYINDEX - index- 1;
            if (closure != null
                    && closure.upvals.length > uvIdx
                    && closure.upvals[uvIdx] != null) {
                closure.upvals[uvIdx].set(val);
            }
            return true;
        }
        if(index == LUA_REGISTRYINDEX){
            this.state.registry = (LuaTable) val;
            return true;
        }
        int absIdx = absIndex(index);
        if(isValid(index)){
            slots.set(absIdx-1, val);
            return true;
        }
        System.out.println("not Valia index stack set error!");
        return false;
    }

    //传入的都是lua索引,操作slot的时候需要转化为虚拟机索引
    public void reverse(int from, int to) {

        if(from-1 > to){
            throw new RuntimeException("Stack is empty");
        }

        if(!isValid(from) || !isValid(to)){
            throw new RuntimeException("Stack is empty");
        }
        //这个翻转范围应该是开区间
        Collections.reverse(slots.subList(from-1, to));   //翻转范围,由于array是开区间的,[)的,所以这么操作
    }


    //======== temp debug output =======
    public void printState(){
        System.out.println(slots.toString());
    }

}
