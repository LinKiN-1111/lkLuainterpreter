package org.example.ch09.state;

import org.example.ch09.Chunk.BinaryChunk;
import org.example.ch09.Chunk.Prototype;
import org.example.ch09.api.*;
import org.example.ch09.vm.Instruction;
import org.example.ch09.vm.OpCode;

import java.util.Collections;
import java.util.List;

import static java.lang.System.exit;
import static org.example.ch08.api.ThreadStatus.LUA_OK;
import static org.example.ch08.state.LuaValue.converToFloat;
import static org.example.ch08.state.LuaValue.typeOf;


//这里面的操作基本上都没判断溢出的情况，因为这个情况在栈中判断了
public class LuaStateImpl implements LuaState, LuaVM {
    //注意，这里的操作就和LUA的是一样的了，需要以1作为基地址
    private LuaStack stack;

    //在ch08中将proto交由stack实现之后，整个解释器就直接新建一个LuaStack即可
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

    //去除某个位置的
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
        int newTop = stack.absIndex(idx);
        if(newTop<=0){
            throw new RuntimeException("stack underflow!");
        }
        //不做处理
        if(newTop==stack.top()){
            return;
        }
        //添加nil
        while(newTop > stack.top()) {
            pushNil();
        }

        //删除多余的项
        while(newTop < stack.top()) {
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
        Object o = stack.get(idx);
        return LuaValue.typeOf(o);
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


    //基本操作
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
            pushInteger((long) ((String) val).length());
        }else if (val instanceof LuaTable) {
            pushInteger((long) ((LuaTable) val).length());
        }
        else {
            throw new RuntimeException("length error!");
        }
    }


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

    //======== 实现LuaVM接口 ========
    @Override
    public long getPC() {
        return this.stack.pc;
    }

    @Override
    public void addPC(int n) {
        this.stack.pc += n;
    }

    //这里有些和书上不一样,本质原因是因为我很多字段用的是long来存储,书中用的是int来存储,所以在写代码的时候着重观察这两个情况
    @Override
    public long fetch() {
        long[] codes = this.stack.closure.getProto().getCode();
        long code = codes[(int) this.stack.pc];
        this.stack.pc++;     //可以回看结构解析中的code,code是以一个数组的形式存储的,而且其是定长的,所以只要往后指一个,就能获取到该code的long形式
        return code;
    }


    //从常量表中获取一个值,然后推入栈顶
    @Override
    public void getConst(int idx) {
        Object[] constants = this.stack.closure.getProto().getConstants();
        Object constant = constants[idx];
        stack.push(constant);
    }

    //根据情况调用GetConst方法,把某个常量推入栈顶,或者调用pushValue方法把某个索引处的栈值推入栈顶
    //传递给该方法的参数实际上是iABC模式指令里的OpArgK类型参数,由第三者可知,这种类型的参数一共占9bit
    //如果最高位是1,这参数中存放的就是常量表的索引
    @Override
    public void getRK(int rk) {
        if (rk > 0xFF) { // constant
            getConst(rk & 0xFF);
        } else { // register
            pushValue(rk + 1); //注意,LuaAPI里的索引是从1开始的,当我们转化为java的stack索引的时候,要对寄存器+1
        }
    }


    /* Table function*/
    @Override
    public void newTable() {
        LuaTable luaTable = new LuaTable(0,0);
        stack.push(luaTable);
    }

    @Override
    public void createTable(int nArr, int nRec) {
        LuaTable luaTable = new LuaTable(nArr, nRec);
        stack.push(luaTable);
    }

    //根据键(从栈顶弹出)从表(索引由参数指定)里取值,然后将值推入栈顶,并获取返回值的类型
    @Override
    public LuaType getTable(int idx) {
        Object t = stack.get(idx);
        Object k = stack.pop();
        return getTable(t, k);
    }
    //从表里取值的方法做一层抽象,以便复用
    private LuaType getTable(Object table, Object index) {
        if(table instanceof LuaTable) {
            Object v = ((LuaTable) table).get(index);
            stack.push(v);
            return typeOf(v);
        }
        throw new RuntimeException("getTable error!");
    }


    @Override
    public LuaType getField(int idx, String k) {
        Object t = stack.get(idx);
        return getTable(t,k);
    }

    @Override
    public LuaType getI(int idx, long i) {
        Object t = stack.get(idx);
        return getTable(t,i);
    }

    //将键值对写入表,其中键和值从栈里面弹出,表则位于指定索引处
    @Override
    public void setTable(int idx) {
        Object t = stack.get(idx);
        Object value = stack.pop();
        Object key = stack.pop();
        setTable(t,key,value);
    }

    private void setTable(Object table, Object key, Object value) {
        if(table instanceof LuaTable) {
            ((LuaTable) table).put(key, value);
            return;
        }
        throw new RuntimeException("setTable error!");
    }

    //这里基本和上面的一样，就是键不是从栈顶弹出的任意值，而是由参数传入的字符串
    @Override
    public void setField(int idx, String k) {
        Object t  = stack.get(idx);
        Object value = stack.pop();
        setTable(t,k,value);
    }

    //那这里估计就和上面一样了
    @Override
    public void setI(int idx, long i) {
        Object t  = stack.get(idx);
        Object value = stack.pop();
        setTable(t,i,value);
    }

    //只是实现将载入的chunk放入到栈中
    @Override
    public ThreadStatus load(byte[] chunk, String chunkName, String mode) {
        try {
            Prototype prototype = BinaryChunk.unDump(chunk);
            Closure closure = new Closure(prototype);
            this.stack.push(closure);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return LUA_OK;
    }

    //完成对Lua函数进行调用,在执行call方法之前,必须将被调用函数推入栈顶,然后把参数值依次推入栈顶
    //结束后,推入栈顶的是返回值,第一个参数是传递给被调用函数的参数刷量,第二个参数指定需要的返回值数量(多退少补)
    @Override
    public void call(int nArgs, int nResults) {
        if(stack.isValid(-(nArgs+1))){
            Object o = stack.get(-(nArgs + 1));
            if(o instanceof Closure){
                Closure closure = (Closure) o;
                System.out.printf("call %s<%d,%d>\n", closure.getProto().getSource(),
                        closure.getProto().getLineDefined(),closure.getProto().getLastLineDefined());
                callLuaClosure(nArgs, nResults, closure);
            }else {
                throw new RuntimeException("call error!");
            }
        }else {
            throw new RuntimeException("call error!");
        }

    }

    private void callLuaClosure(int nArgs, int nResults, Closure closure) {
        //这部分的操作目的是获取执行函数需要的寄存器数量，定义函数时声明的固定参数数量以及是否是vararg函数
        //然后根据寄存器数量创建栈空间,并把闭包和调用帧联系起来
        byte nRegs = closure.getProto().getMaxStackSize();  //获取寄存器数量
        byte nParams = closure.getProto().getNumParams();  //获取该函数的参数数量
        byte isVarargByte = closure.getProto().getIsVararg(); //判断是否是可变参数函数
        boolean isVararg = (isVarargByte == 1);

        LuaStack newLuaStack = new LuaStack(nRegs + 20);   //新建一个新的stack
        newLuaStack.closure =  closure;                 //指向当前解析出来的闭包

        List<Object> funcAndArgs = this.stack.popN(nArgs + 1);
        newLuaStack.pushN(funcAndArgs.subList(1, funcAndArgs.size()), nParams);
        if(nArgs > nParams && isVararg) {
            newLuaStack.varargs = funcAndArgs.subList(1 + nParams, funcAndArgs.size());
        }

        this.pushLuaStack(newLuaStack);
        //这里操作的是新的栈
        this.setTop(nRegs);
        this.runLuaClosure();
        this.popLuaStack();

        // return results
        if (nResults != 0) {
            List<Object> results = newLuaStack.popN(newLuaStack.top() - nRegs);
            //stack.check(results.size())
            stack.pushN(results, nResults);
        }

    }

    private void runLuaClosure() {
        for(;;){
            long ins = fetch();
            OpCode opCode = Instruction.getOpCode(ins);
//            if (opCode != org.example.ch08.vm.OpCode.RETURN) {
//                System.out.printf("[%02d] %-8s \n", this.stack.pc, opCode.name());
//            }
            opCode.getAction().execute((int)ins,this);
//            printStack();
            if (opCode == OpCode.RETURN) {
                break;
            }
        }
    }

    @Override
    public int registerCount() {
        return (int)this.stack.closure.getProto().getMaxStackSize();
    }

    @Override
    public void loadVararg(int n) {

        List<Object> varargs = stack.varargs != null
                ? stack.varargs : Collections.emptyList();
        if(n < 0){
            n = this.stack.varargs.size();
        }

        this.stack.pushN(varargs,n);

    }


    @Override
    public void loadProto(int idx) {
        Prototype proto = this.stack.closure.getProto().getProtos()[idx];
        Closure closure = new Closure(proto);
        this.stack.push(closure);
    }

    //简单理解一下，每个函数对应一个栈，当前虚拟机接收了这个函数之后，生成了一个新的stack
    //这个新的stack的前一个stack就是当前的stack，然后虚拟机转为执行传入的stack
    public void pushLuaStack(LuaStack stack) {
        stack.prev = this.stack;
        this.stack = stack;
    }

    //去除顶部的stack
    private void popLuaStack() {
        LuaStack top = this.stack;
        this.stack = top.prev;
        top.prev = null;
    }

    //   tmp
    public void printStack() {
        int top = this.getTop();

        for (int i = 1; i <= top; i++) {
            LuaType t = this.type(i);
            switch (t) {
                case LUA_TBOOLEAN:
                    System.out.printf("[%b]", this.toBoolean(i));
                    break;
                case LUA_TNUMBER:
                    if (this.isInteger(i)) {
                        System.out.printf("[%d]", this.toInteger(i));
                    } else {
                        System.out.printf("[%f]", this.toNumber(i));
                    }
                    break;
                case LUA_TSTRING:
                    System.out.printf("[\"%s\"]", this.toString(i));
                    break;
                default: // other values
                    System.out.printf("[%s]", this.typeName(t));
                    break;
            }
        }
        System.out.println();
    }
}
