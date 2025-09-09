package org.example.ch05.state;

import org.example.ch05.api.ArithOp;
import org.example.ch05.api.CmpOp;
import org.example.ch05.api.LuaState;
import org.example.ch05.api.LuaType;

import static java.lang.System.exit;
import static org.example.ch05.state.LuaValue.converToFloat;


//这里面的操作基本上都没判断溢出的情况，因为这个情况在栈中判断了
public class LuaStateImpl implements LuaState {
    //注意，这里的操作就和LUA的是一样的了，需要以1作为基地址
    LuaStack stack;

    public LuaStateImpl() {
        stack = new LuaStack(20);
    }

    //返回栈顶的索引
    @Override
    public int getTop() {
        return stack.top();
    }

    //将索引转化为绝对索引
    @Override
    public int absIndex(int idx) {
        return stack.absIndex(idx);
    }

    //由于LUA的栈容量不会自动增长，所以需要调用这个来操作->感觉是因为C++实现的问题
    //由于我们是使用Java的arraylist来进行的模拟，所以直接返回true先即可
    @Override
    public boolean checkStack(int n) {
        return true;
    }

    //从栈中弹出n个值--->这个是settop的一个特例
    @Override
    public void pop(int n) {
        for (int i = 0; i < n; i++) {
            stack.pop();
        }
    }

    //将指定位置的元素复制到目标位置
    @Override
    public void copy(int fromIdx, int toIdx) {
        stack.set(toIdx, stack.get(fromIdx));
    }

    //获取某个位置的值，然后压入
    @Override
    public void pushValue(int idx) {
        stack.push(stack.get(idx));
    }

    //弹出一个元素，将指定位置元素设置为弹出的元素
    @Override
    public void replace(int idx) {
        Object pop = stack.pop();
        stack.set(idx, pop);
    }

    //将栈顶元素弹出，再插入到指定位置
    @Override
    public void insert(int idx) {
        int index = stack.absIndex(idx);
        stack.reverse(index,stack.top());
        stack.reverse(index+1,stack.top());
    }

    @Override
    public void remove(int idx) {
        int index = stack.absIndex(idx);
        stack.reverse(index,stack.top());
        stack.pop();
        stack.reverse(index,stack.top());
    }

    //具体而言，就是索引区间[index,top]这个区间进行旋转,n表示旋转n个位置
    //n为正数时往栈顶方向旋转,n为负数时往栈底方向旋转...
    //三次反转法实现:注意,lua都是闭区间
    @Override
    public void rotate(int idx, int n) {
        //这个不能交由stack来判断了
        if(idx+n > stack.top()) {
            System.out.println("rotate error!");
            exit(0);
        }
        if(n >= 0){
            stack.reverse(idx,stack.top());
            stack.reverse(idx,idx+n-1);
            stack.reverse(idx+n,stack.top());
        }else{
            stack.reverse(idx,stack.top());
            stack.reverse(idx,stack.top()+n-1);
            stack.reverse(stack.top()+n,stack.top());
        }
    }

    //settop方法将栈顶索引设置为指定值,也就是说多退少补
    @Override
    public void setTop(int idx) {
        int i = stack.absIndex(idx);
        if(i<=0){
            System.out.println("setTop error! index is : " + i);
            exit(0);
        }
        //不做处理
        if(i==stack.top()){
            return;
        }
        //添加nil
        while(i > stack.top()) {
            pushNil();
        }

        //删除多余的项
        while(i < stack.top()) {
            stack.pop();
        }
    }

    //将给定的Lua类型转换换成对应的字符串表示
    @Override
    public String typeName(LuaType tp) {
        switch (tp){
            case LUA_TNONE:
                return "no value";
            case LUA_TNIL:
                return "nil";
            case LUA_TBOOLEAN:
                return "boolean";
            case LUA_TNUMBER:
                return "number";
            case LUA_TSTRING:
                return "string";
            case LUA_TTABLE:
                return "table";
            case LUA_TFUNCTION:
                return "function";
            case LUA_TTHREAD:
                return "thread";
            default:
                return "userdata";
        }
    }

    //
    @Override
    public LuaType type(int idx) {
        if(stack.isValid(idx)){
            Object o = stack.get(idx);
            return LuaValue.typeOf(o);
        }
        return LuaType.LUA_TNONE;
    }

    @Override
    public boolean isNone(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TNONE;
    }

    @Override
    public boolean isNil(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TNIL;
    }

    @Override
    public boolean isNoneOrNil(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TNONE ||  type == LuaType.LUA_TNIL;
    }

    @Override
    public boolean isBoolean(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TBOOLEAN;
    }

    @Override
    public boolean isInteger(int idx) {
        return stack.get(idx) instanceof Long;
    }

    @Override
    public boolean isNumber(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TNUMBER;
    }

    @Override
    public boolean isString(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TSTRING || type == LuaType.LUA_TNUMBER;   //String的范围扩大了
    }

    @Override
    public boolean isTable(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TTABLE;
    }

    @Override
    public boolean isThread(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TTHREAD;
    }

    @Override
    public boolean isFunction(int idx) {
        LuaType type = type(idx);
        return type == LuaType.LUA_TFUNCTION;
    }

    //有点难理解,但是是从索引处取出一个布尔值???
    //注意,lua中只有false和nil表示假,所以选择这样设置
    @Override
    public boolean toBoolean(int idx) {
        LuaType type = type(idx);
        if(type == LuaType.LUA_TBOOLEAN){
            return (boolean) stack.get(idx);
        }else if (type == LuaType.LUA_TNIL){
            return false;
        }
        return true;

    }

    @Override
    public long toInteger(int idx) {
        Long integerX = toIntegerX(idx);
        return integerX == null ? 0 : integerX;
    }

    @Override
    public Long toIntegerX(int idx) {
        Object val = stack.get(idx);
        return LuaValue.convertToInteger(val);
    }

    @Override
    public double toNumber(int idx) {
        Double numberX = toNumberX(idx);
        return numberX == null ? 0.0 : numberX;
    }

    @Override
    public Double toNumberX(int idx) {
        Object val = stack.get(idx);
        return converToFloat(val);
    }

    @Override
    public String toString(int idx) {
        Object val = stack.get(idx);
        if (val instanceof String) {
            return (String) val;
        } else if (val instanceof Long || val instanceof Double) {
            stack.set(idx,val.toString()); //注意,这里需要修改栈
            return val.toString();
        } else {
            return null;
        }

    }


    @Override
    public void pushNil() {
        stack.push(null);
    }

    @Override
    public void pushBoolean(boolean b) {
        stack.push(b);
    }

    @Override
    public void pushInteger(long n) {
        stack.push(n);
    }

    @Override
    public void pushNumber(double n) {
        stack.push(n);
    }

    @Override
    public void pushString(String s) {
        stack.push(s);
    }


    //
    @Override
    public void arith(ArithOp op) {
        Object y = stack.pop();
        Object x = null;
        if (op != ArithOp.LUA_OPUNM && op != ArithOp.LUA_OPBNOT){
             x = stack.pop();
        }else {
             x = y;
        }

        Object result = Arithmetic.arith(x, y, op);
        if (result != null) {
            stack.push(result);
        } else {
            throw new RuntimeException("arithmetic error!");
        }

    }

    @Override
    public boolean compare(int idx1, int idx2, CmpOp op) {
        if (!stack.isValid(idx1) || !stack.isValid(idx2)) {
            return false;
        }

        Object a = stack.get(idx1);
        Object b = stack.get(idx2);
        switch (op) {
            case LUA_OPEQ: return Comparison.eq(a, b);
            case LUA_OPLT: return Comparison.lt(a, b);
            case LUA_OPLE: return Comparison.le(a, b);
            default: throw new RuntimeException("invalid compare op!");
        }
    }

    /* miscellaneous functions */
    @Override
    public void len(int idx) {
        //暂时只考虑字符串长度
        Object val = stack.get(idx);
        if (val instanceof String) {
            pushInteger(((String) val).length());
        } else {
            throw new RuntimeException("length error!");
        }
    }


    //不知道这里为什么只能转换string,但是lua理论上可以拼接字符串和数字的....
    @Override
    public void concat(int n) {
        if (n == 0) {
            stack.push("");
        } else if (n >= 2) {
            for (int i = 1; i < n; i++) {
                if (isString(-1) && isString(-2)) {
                    String s2 = toString(-1);
                    String s1 = toString(-2);
                    pop(2);
                    pushString(s1 + s2);
                    continue;
                }
                throw new RuntimeException("concatenation error!");
            }
        }
        // n == 1, do nothing
    }


    //======== temp ====
    public void printStackState(){
        stack.printState();
    }
}
