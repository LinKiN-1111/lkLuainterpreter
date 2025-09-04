package org.example.Utils;

public class TransUtils {
    //小端序存储
    public static byte[] intToBytes(int i){
        byte[] result = new byte[4];
        result[3] = (byte) (i >> 24 & 0xFF);
        result[2] = (byte) (i >> 16 & 0xFF);
        result[1] = (byte) (i >> 8 & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] longToBytes(long l){
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) ((l >> 8 * i) & 0xFF);
        }
        return result;
    }

}
