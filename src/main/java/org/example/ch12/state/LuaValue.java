package org.example.ch12.state;

import org.example.ch12.api.LuaType;
import org.example.ch12.number.LuaParser;

import static org.example.ch12.api.LuaType.*;

//这里的意思好像是说将java类型转化为lua类型
public class LuaValue {

    //判断一个java类型对应什么LuaValue类型
    public static LuaType typeOf(Object val) {
        if (val == null) {
            return LUA_TNIL;
        } else if (val instanceof Boolean) {
            return LUA_TBOOLEAN;
        } else if (val instanceof Long || val instanceof Double) {
            return LUA_TNUMBER;
        } else if (val instanceof String) {
            return LUA_TSTRING;
        } else if (val instanceof LuaTable) {
            return LUA_TTABLE;
        } else if (val instanceof Closure) {
            return LUA_TFUNCTION;
        } else {
            throw new RuntimeException("TODO");
        }
    }

    //将一个lua类型映射为布尔值
    public static boolean toBoolean(Object val) {
        if (val == null) {
            return false;
        } else if (val instanceof Boolean) {
            return (Boolean) val;
        } else {
            return true;
        }
    }

    //转化为浮点数
    public static Double converToFloat(Object val){
        if(val instanceof Double){
            return (Double) val;
        }else if(val instanceof Long){
            return (Double) ((Long) val).doubleValue();
        }else if(val instanceof String){
            return LuaParser.parseFloat(val.toString());
        }
        return null;
    }

    //转化为整数
    public static Long convertToInteger(Object val) {
        if (val instanceof Long) {
            return (Long) val;
        } else if (val instanceof Double) {
            double n = (Double) val;
            return LuaParser.isInteger(n) ? (long) n : null;
        } else if (val instanceof String) {
            return LuaParser.parseInteger((String) val);
        }
        return null;
    }


}
