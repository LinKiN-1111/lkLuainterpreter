package org.example.ch04.Chunk;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

//这个是固定的头结构,读入之后通过这个来判断是否符合格式

public class BinaryChunk {
    private static byte[] LUA_SIGNATURE = {0x1b,'L','u','a'};
    private static byte LUA_VERSION = 0x53;
    private static byte LUA_FORMAT = 0x00;
    private static byte[] LUAC_DATA = {0x19, (byte) 0x93, '\r', '\n', 0x1a, '\n'};
    //每种类型占的字节数
    private static byte CINT_LEN = 0x04;
    private static byte SIZE_T_LEN = 0x08;
    private static byte LUA_INS_LEN = 0x04;
    private static byte LUA_INT = 0x08;
    private static byte LUA_FLOAT = 0x08;
    private static byte[] LUAC_INT = {0x78,0x56,0x00,0x00,0x00,0x00,0x00,0x00}; //整数
    private static byte[] LUAC_NUM = {0x00,0x00,0x00,0x00,0x00,0x28,0x77,0x40};  //浮点数

    //存储所有字节,还得模拟一个字节流,让其它函数能够调用
    private byte[] data;
    private int pos = 0;

    //长度不能乱设置,这部分都是基本数据操作
    public boolean setPos(int pos){
        if(pos < this.data.length){
            this.pos = pos;
            return true;
        }else {
            return false;
        }
    }

    //构造函数,存储一个data,包含了二进制文件中的所有数据
    public BinaryChunk(byte[] data) {
        this.data = data;
    }

    //从当前字节流中读一个字节
    public byte readByte(){
        return data[pos++];
    }

    //读取N个字节的字节数组
    public byte[] readNByte(int n){
        byte[] result = new byte[n];
        for(int i = 0; i < n; i++){
            result[i] = data[pos++];
        }
        return result;
    }

    //这里用long来存储,应该不会有bug
    public long readUint32() throws IOException {
        byte[] resultbytes = new byte[8];
        System.arraycopy(data, pos, resultbytes, 0, 4);
        pos += 4;
        return ByteBuffer.wrap(resultbytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    //这里可能会有bug的,因为java中模拟不出来uint64,最多用long来存储了
    public long readUint64() throws IOException {
        byte[] resultbytes = new byte[8];
        System.arraycopy(data, pos, resultbytes, 0, 8);
        pos += 8;
        return ByteBuffer.wrap(resultbytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    //该类型占8字节,映射到java的long中
    public long readLuaInteger() throws IOException {
        byte[] resultbytes = new byte[8];
        System.arraycopy(data, pos, resultbytes, 0, 8);
        pos += 8;
        return ByteBuffer.wrap(resultbytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    //这个是64位的float类型,这个需要映射到java的double类型吧
    public double readLuaNumber() throws IOException {
        byte[] resultbytes = new byte[8];
        System.arraycopy(data, pos, resultbytes, 0, 8);
        pos += 8;
        return ByteBuffer.wrap(resultbytes).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    //从当前位置读取一个string
    //需要注意str的存储格式,读取出来的数据好像回加上这个存储字节,所以读的时候需要-1
    public String readString() throws IOException {
        byte flag = readByte();
        String str = null;
        if(flag == 0){
            //NULL
            str = "";
        }else if(flag == 0xFF){
            //长字符串
            long len = readUint64();   //获取实际字符串长度
            byte[] stringBytes = readNByte((int)len-1);  //这个操作pos 已经加了,所以这后面就不用+了
            str = new String(stringBytes);
        }else if (flag > 0 && flag < 0xFF){
            //短字符串
            long len = flag ;
            byte[] stringBytes = readNByte((int)len-1);  //这个操作pos 已经加了,所以这后面就不用+了
            str = new String(stringBytes);
        }
        return str;
    }

    //读取头部，验证头部
    public void checkHeader(){
        setPos(0); //直接设置pos = 0
        if(!Arrays.equals(readNByte(4), LUA_SIGNATURE)){
            System.out.println("Invalid LUA_SIGNATURE");
        } else if (readByte() != LUA_VERSION) {
            System.out.println("Invalid LUA_VERSION");
        }else if (readByte() != LUA_FORMAT) {
            System.out.println("Invalid LUA_FORMAT");
        }else if (!Arrays.equals(readNByte(6),LUAC_DATA)){
            System.out.println("Invalid LUAC_DATA");
        }else if(readByte() != CINT_LEN ){
            System.out.println("Invalid CINT_LEN");
        }else if(readByte() != SIZE_T_LEN ){
            System.out.println("Invalid SIZE_T_LEN");
        }else if(readByte() != LUA_INS_LEN){
            System.out.println("Invalid LUA_INS_LEN");
        }else if(readByte() != LUA_INT){
            System.out.println("Invalid LUA_INT");
        }else if(readByte() != LUA_FLOAT){
            System.out.println("Invalid LUA_FLOAT");
        }else if(!Arrays.equals(readNByte(8),LUAC_INT)){
            System.out.println("Invalid LUAC_INT");
        }else if(!Arrays.equals(readNByte(8),LUAC_NUM)){
            System.out.println("Invalid LUAC_NUM");
        }
        System.out.println("verify success!");
    }


    //codenum : 要读入的指令个数
    private long[] readCode() throws IOException {
        long codeCount = readUint32();
        long[] result = new long[(int)codeCount];
        for(int i = 0; i < codeCount; i++){
            result[i] = readUint32();
        }
        return result;
    }

    //根据不同的标签读取常量
    private Object[] readConstants() throws IOException {
        long constantsCount = readUint32();
        Object[] result = new Object[(int)constantsCount];
        for(int i = 0; i < constantsCount; i++){
            byte tag = readByte();
            Object input = null;
            switch (tag){
                case Prototype.TAG_NIL: input = "";break;
                case Prototype.TAG_BOOLEAN: input = readByte();break;
                case Prototype.TAG_INTEGER: input = readLuaInteger();break;
                case Prototype.TAG_NUMBER: input = readLuaNumber();break;
                case Prototype.TAG_SHORT_STR: input = readString();break;
                case Prototype.TAG_LONG_STR: input = readString();break;
                default:
                    System.out.println("Invalid tag");
            }
            result[i] = input;
        }
        return result;
    }

    private Upvalue[] readUpvalues() throws IOException {
        long upValueCount = readUint32();
        Upvalue[] result = new Upvalue[(int)upValueCount];
        for(int i = 0; i < upValueCount; i++){
            result[i] = new Upvalue();
            result[i].setInstack(readByte());
            result[i].setIdx(readByte());
        }
        return result;
    }

    private Prototype[] readPrototypes(String source) throws IOException {
        long prototypesCount = readUint32();
        Prototype[] result = new Prototype[(int)prototypesCount];
        for(int i = 0; i < prototypesCount; i++){
            result[i] = readProto(source);
        }
        return result;
    }

    private long[] readLineInfos() throws IOException {
        long lineInfoCount = readUint32();
        long[] result = new long[(int)lineInfoCount];
        for(int i = 0; i < lineInfoCount; i++){
            result[i] = readUint32();
        }
        return result;
    }

    private LocVar[] readLocalVars() throws IOException {
        long localVarCount = readUint32();
        LocVar[] result = new LocVar[(int)localVarCount];
        for(int i = 0; i < localVarCount; i++){
            result[i] = new LocVar();
            result[i].setVarName(readString());
            result[i].setStartPC(readUint32());
            result[i].setEndPC(readUint32());
        }
        return result;
    }


    private String[] readUpvalueNames() throws IOException {
        long UpvalueNameCount = readUint32();
        String[] result = new String[(int)UpvalueNameCount];
        for(int i = 0; i < UpvalueNameCount; i++){
            result[i] = readString();
        }
        return result;
    }


    //读取函数原型，验证完头部之后，目前pos应该指向的就是函数原型
    public Prototype readProto(String sourcefile) throws IOException {
        Prototype prototype = new Prototype();
        String source = readString();
        if("".equals(source)){
            source = sourcefile;
        }
        prototype.setSource(source);   //编译文件名
        prototype.setLineDefined(readUint32());     //起始行号
        prototype.setLastLineDefined(readUint32());//结束行号
        prototype.setNumParams(readByte());        //固定参数个数
        prototype.setIsVararg(readByte());        //是否是Vararg函数
        prototype.setMaxStackSize(readByte());    //寄存器数量
        prototype.setCode(readCode()); //根据codeCount的个数读取一下Code
        prototype.setConstants(readConstants());  //获取所有常量
        prototype.setUpvalues(readUpvalues()); //获取所有upValue
        prototype.setProtos(readPrototypes(sourcefile));  //读取子原型
        prototype.setLineInfo(readLineInfos());  //读取每个指令对应的源文件行数
        prototype.setLocVars(readLocalVars());   //读取局部变量表
        prototype.setUpvalueNames(readUpvalueNames());


        return prototype;
    }


}


