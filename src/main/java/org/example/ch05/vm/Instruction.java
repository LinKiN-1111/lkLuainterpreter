package org.example.ch05.vm;


public class Instruction {

    public static final int MAXARG_Bx = (1 << 18) - 1;   // 262143
    public static final int MAXARG_sBx = MAXARG_Bx >> 1; // 131071


    //提取操作码
    public static OpCode getOpCode(long i){
        return OpCode.values()[(int)i & 0x3f];
    }

    public static int[] ABC(long i){
        int[] result = new int[3];
        int A = ((int)i >> 6) & 0xFF;
        int C = ((int)i >> 14) & 0x1FF;
        int B = ((int)i >> 23) & 0x1FF;
        result[0] = A;
        result[1] = B;
        result[2] = C;
        return result;
    }

    //返回iABx模式的参数
    public static int[] ABx(long i){
        int[] result = new int[2];
        int A = ((int)i >> 6) & 0xFF;
        int B = ((int)i >> 14);
        result[0] = A;
        result[1] = B;
        return result;
    }

    public static int[] AsBx(long i){
        int[] result = new int[2];
        int A = ((int)i >> 6) & 0xFF;
        int sB = ((int)i >> 14) - MAXARG_sBx;   //书上这么定义的,我也不清楚具体为什么
        result[0] = A;
        result[1] = sB;
        return result;
    }

    public static int[] Ax(long i){
        int [] result = new int[1];
        int A = ((int)i >> 6);
        result[0] = A;
        return result;
    }

    public static String OpName(long i){
        return getOpCode(i).name();
    }

    public static OpMode OpMode(long i){
        return getOpCode(i).getOpMode();
    }

    public static OpArgMask bOpArgMask(long i){
        return  getOpCode(i).getArgBMode();
    }

    public static OpArgMask cOpArgMask(long i){
        return  getOpCode(i).getArgCMode();
    }

}
