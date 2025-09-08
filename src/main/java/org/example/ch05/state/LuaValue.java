package org.example.ch05.state;

import org.example.ch05.api.LuaType;

import static org.example.ch04.api.LuaType.*;

//这里的意思好像是说将java类型转化为lua类型
public class LuaValue {

    static LuaType typeOf(Object val) {
        if (val == null) {
            return LUA_TNIL;
        } else if (val instanceof Boolean) {
            return LUA_TBOOLEAN;
        } else if (val instanceof Long || val instanceof Double) {
            return LUA_TNUMBER;
        } else if (val instanceof String) {
            return LUA_TSTRING;
        } else {
            throw new RuntimeException("TODO");
        }
    }

    static boolean toBoolean(Object val) {
        if (val == null) {
            return false;
        } else if (val instanceof Boolean) {
            return (Boolean) val;
        } else {
            return true;
        }
    }
}
