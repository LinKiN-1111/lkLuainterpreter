package org.example.ch06.state;

public class Comparison {

    //这里大致先说明一下比较规则： nil 布尔 和 字符串类型比较都比较简单
    public static boolean eq(Object a,Object b){
        if (a == null) {
            return b == null;  //都为null就相等
        } else if (a instanceof Boolean || a instanceof String) {
            return a.equals(b);
        } else if (a instanceof Long) {
            return a.equals(b) ||
                    (b instanceof Double && b.equals(((Long) a).doubleValue()));
        } else if (a instanceof Double) {
            return a.equals(b) ||
                    (b instanceof Long && a.equals(((Long) b).doubleValue()));
        } else {
            return a == b;
        }
    }

    //只比较字符串和数字类型
    public static boolean lt(Object a,Object b){
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b) < 0;
        }
        if (a instanceof Long) {
            if (b instanceof Long) {
                return ((Long) a) < ((Long) b);
            } else if (b instanceof Double) {
                return ((Long) a).doubleValue() < ((Double) b);
            }
        }
        if (a instanceof Double) {
            if (b instanceof Double) {
                return ((Double) a) < ((Double) b);
            } else if (b instanceof Long) {
                return ((Double) a) < ((Long) b).doubleValue();
            }
        }
        throw new RuntimeException("comparison error!");
    }

    public static boolean le(Object a,Object b){
        if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b) <= 0;
        }
        if (a instanceof Long) {
            if (b instanceof Long) {
                return ((Long) a) <= ((Long) b);
            } else if (b instanceof Double) {
                return ((Long) a).doubleValue() <= ((Double) b);
            }
        }
        if (a instanceof Double) {
            if (b instanceof Double) {
                return ((Double) a) <= ((Double) b);
            } else if (b instanceof Long) {
                return ((Double) a) <= ((Long) b).doubleValue();
            }
        }
        throw new RuntimeException("comparison error!");
    }
}

/*   具体就是完成下述的比较规则
> "1" == 1
false
> "1" == "1"
true
> "1" == "2"
false
> true == false
false
> true == true
true
> 1.0 == 1.1
false
> 1.1 == 1.1
true
> 1 == 1.0
true
 */