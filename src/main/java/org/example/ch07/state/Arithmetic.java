package org.example.ch07.state;

import org.example.ch07.api.ArithOp;
import org.example.ch07.number.LuaMath;

import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;

public class Arithmetic {
    //将所有Lua运算都映射成java的运算
    private static final LongBinaryOperator[] integerOps = {
            (a, b) -> a + b,     // LUA_OPADD
            (a, b) -> a - b,     // LUA_OPSUB
            (a, b) -> a * b,     // LUA_OPMUL
            Math::floorMod,      // LUA_OPMOD
            null,                // LUA_OPPOW
            null,                // LUA_OPDIV
            Math::floorDiv,      // LUA_OPIDIV
            (a, b) -> a & b,     // LUA_OPBAND
            (a, b) -> a | b,     // LUA_OPBOR
            (a, b) -> a ^ b,     // LUA_OPBXOR
            LuaMath::shiftLeft,  // LUA_OPSHL
            LuaMath::shiftRight, // LUA_OPSHR
            (a, b) -> -a,        // LUA_OPUNM
            (a, b) -> ~a,        // LUA_OPBNOT
    };

    private static final DoubleBinaryOperator[] floatOps = {
            (a, b) -> a + b,   // LUA_OPADD
            (a, b) -> a - b,   // LUA_OPSUB
            (a, b) -> a * b,   // LUA_OPMUL
            LuaMath::fMod, // LUA_OPMOD
            Math::pow,         // LUA_OPPOW
            (a, b) -> a / b,   // LUA_OPDIV
            LuaMath::FFloorDiv, // LUA_OPIDIV
            null,              // LUA_OPBAND
            null,              // LUA_OPBOR
            null,              // LUA_OPBXOR
            null,              // LUA_OPSHL
            null,              // LUA_OPSHR
            (a, b) -> -a,      // LUA_OPUNM
            null,              // LUA_OPBNOT
    };


    //这个的算数就隐含了Lua的自动转化
    public static Object arith(Object a, Object b, ArithOp op) {
        LongBinaryOperator integerFunc = integerOps[op.ordinal()];
        DoubleBinaryOperator floatFunc = floatOps[op.ordinal()];

        if(floatFunc == null) { //说明肯定需要进行整数运算
            Long x = LuaValue.convertToInteger(a);
            if(x != null) {
                Long y  = LuaValue.convertToInteger(b);
                if(y != null) {
                    return integerFunc.applyAsLong(x, y);
                }
            }
        }else {
            //若都有的情况下,判断是否都是整数,若都是整数,进行进行整数运算
            if (integerFunc != null) { // add,sub,mul,mod,idiv,unm
                if (a instanceof Long && b instanceof Long) {
                    return integerFunc.applyAsLong((Long) a, (Long) b);
                }
            }
            //否则进行浮点数运算
            Double x = LuaValue.converToFloat(a);
            if (x != null) {
                Double y = LuaValue.converToFloat(b);
                if (y != null) {
                    return floatFunc.applyAsDouble(x, y);
                }
            }
        }

        return null;
    }


}
