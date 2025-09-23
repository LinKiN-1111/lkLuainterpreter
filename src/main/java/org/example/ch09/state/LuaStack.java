package org.example.ch09.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuaStack {
    private final ArrayList<Object> slots;

    /* call info */
    Closure closure;         //看来代表一个函数的特点
    List<Object> varargs;    //应该用于记录传入的参数
    long pc;
    /* linked list */
    LuaStack prev;


    LuaStack(int size) {
        //默认就new一个slots了..

        slots = new ArrayList<>(size);
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
        return idx >= 0 ? idx : idx + slots.size()+ 1;
    }

    //判断这个索引是否合法
    public boolean isValid(int index){
        int absIdx = absIndex(index);
        return absIdx > 0 && absIdx <= slots.size() ;
    }

    //通过索引获取栈中元素，注意索引要-1，因为lua的数组是从1开始的
    public Object get(int index){
        int absIdx = absIndex(index);
        //isValia是对传入的进行判断的
        if(isValid(index)){
            return slots.get(absIdx-1);    //lua索引对应到stack中需要-1
        }

        throw new IndexOutOfBoundsException();
    }

    //set根据索引往栈里面写入
    public boolean set(int index,Object val){
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
        if(from > to){
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
