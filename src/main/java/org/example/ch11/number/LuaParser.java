package org.example.ch11.number;

//负责处理字符串解析的工作
public class LuaParser {
    //将字符串解析为整数
    public static long parseInteger(String input){
        return Long.parseLong(input);
    }
    //将字符串解析为浮点数
    public static double parseFloat(String input){
        return Double.parseDouble(input);
    }

    //判断一个double值是不是整数
    public static boolean isInteger(double f) {
        return f == (long) f;
    }
}
