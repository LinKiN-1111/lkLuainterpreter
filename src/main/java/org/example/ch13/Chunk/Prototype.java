package org.example.ch13.Chunk;


//这个是实际读入的函数原型存储的东西,uint都被我改成long了,以防溢出
public class Prototype {
    //用于常量tag的区分
    public static final byte TAG_NIL       = 0x00;
    public static final byte TAG_BOOLEAN   = 0x01;
    public static final byte TAG_NUMBER    = 0x03;
    public static final byte TAG_INTEGER   = 0x13;
    public static final byte TAG_SHORT_STR = 0x04;
    public static final byte TAG_LONG_STR  = 0x14;


    private String source;            //源文件名
    private long    lineDefined;       //起止行号
    private long    lastLineDefined;
    private byte   numParams;         //固定参数个数
    private byte   isVararg;          //是否为Vararg函数
    private byte   maxStackSize;      //寄存器数量
    private long[]  code;              //指令表,这个是四字节为单位
    private Object[] constants;       //常量表        -->这里直接存就好了
    private Upvalue[] upvalues;       //Upvalue表,这部分和闭包有关?
    private Prototype[] protos;       //子函数原型表
    private long[] lineInfo;           //行号表
    private LocVar[] locVars;      // 局部变量表
    private String[] upvalueNames; // Upvalue名列表

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getLineDefined() {
        return lineDefined;
    }

    public void setLineDefined(long lineDefined) {
        this.lineDefined = lineDefined;
    }

    public long getLastLineDefined() {
        return lastLineDefined;
    }

    public void setLastLineDefined(long lastLineDefined) {
        this.lastLineDefined = lastLineDefined;
    }

    public byte getNumParams() {
        return numParams;
    }

    public void setNumParams(byte numParams) {
        this.numParams = numParams;
    }

    public byte getIsVararg() {
        return this.isVararg;
    }

    public void setIsVararg(byte isVararg) {
        this.isVararg = isVararg;
    }

    public byte getMaxStackSize() {
        return this.maxStackSize;
    }

    public void setMaxStackSize(byte maxStackSize) {
        this.maxStackSize = maxStackSize;
    }


    public long[] getCode() {
        return code;
    }

    public void setCode(long[] code) {
        this.code = code;
    }


    public Object[] getConstants() {
        return constants;
    }

    public void setConstants(Object[] constants) {
        this.constants = constants;
    }

    public Upvalue[] getUpvalues() {
        return upvalues;
    }

    public void setUpvalues(Upvalue[] upvalues) {
        this.upvalues = upvalues;
    }

    public Prototype[] getProtos() {
        return protos;
    }

    public void setProtos(Prototype[] protos) {
        this.protos = protos;
    }

    public long[] getLineInfo() {
        return lineInfo;
    }

    public void setLineInfo(long[] lineInfo) {
        this.lineInfo = lineInfo;
    }

    public LocVar[] getLocVars() {
        return locVars;
    }

    public void setLocVars(LocVar[] locVars) {
        this.locVars = locVars;
    }

    public String[] getUpvalueNames() {
        return upvalueNames;
    }

    public void setUpvalueNames(String[] upvalueNames) {
        this.upvalueNames = upvalueNames;
    }


}
