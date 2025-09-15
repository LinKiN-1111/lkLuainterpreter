package org.example.ch06.api;

//Java中的接口只负责定义方法,而不负责定义存在的属性
public interface LuaVM extends LuaState {
    long getPC();                //返回当前PC
    void addPC(int n);          //修改当前的PC
    long fetch();                //取出当前的指令,同时递增PC指向下一条指令
    void getConst(int idx);     //从常量表中取出指定常量并推入栈顶
    void getRK(int rk);         //从常量表里提取常量或者从栈里提取值,然后推入栈顶...这个方法不是必须的,仅在测试中使用
}
