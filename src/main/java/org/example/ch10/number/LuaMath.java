package org.example.ch10.number;

import static java.lang.System.exit;

public class LuaMath {
    //====================不能直接模拟的算数运算指令 =======================//
    //注意,lua中的整除是往负无穷取整,而java和go中的整除是往0取整,所以需要特别区分一下...
    public static long IFloorDiv(long a, long b){
        if(b == 0){
            System.out.println("除数不能为0");
            exit(-1);
        }
        //可以整除或者结果大于0的时候
        if((a * b >0) || a % b == 0){
            return  a /b;
        }else{
            return a/b -1;
        }
    }
    //调用该函数就是往负无穷取整
    public static double FFloorDiv(double a, double b){
        return Math.floor(a/b);
    }

    //取模函数,数学上证明的,不用管
    public static long iMod(long a,long b){
        return a - IFloorDiv(a,b) * b;
    }

    public static double fMod(double a,double b){
        return a - FFloorDiv(a,b) * b;
    }

    //=====================不能直接模拟的位移运算指令==========================//
    //TODO 注意,这里的b需要转化成无符号数来运算的,但是java没有,可能有bug
    public static long shiftLeft(long a, long b){
        if(b >=0){
            return a << b;
        }
        return  shiftLeft(a,-b);
    }

    //TODO 注意,这里的b需要转化成无符号数来运算的,但是java没有,可能有bug
    public static long shiftRight(long a, long b){
        if(b >=0){
            return a >> b;
        }
        return shiftRight(a,b);
    }

    //TODO 以下的还没完成,等待完成
    //=====================不能直接模拟的比较运算符==========================//

    //=====================不能直接模拟的逻辑运算符==========================//

    //=====================不能直接模拟的长度运算符==========================//

    //==================不能直接模拟的字符串拼接运算符=========================//




}
