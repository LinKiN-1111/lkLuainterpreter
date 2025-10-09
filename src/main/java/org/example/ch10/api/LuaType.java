package org.example.ch10.api;

/* basic types */
public enum LuaType {

    LUA_TNIL          ,
    LUA_TBOOLEAN      ,
    LUA_TLIGHTUSERDATA,
    LUA_TNUMBER       ,
    LUA_TSTRING       ,
    LUA_TTABLE        ,
    LUA_TFUNCTION     ,
    LUA_TUSERDATA     ,
    LUA_TTHREAD       ,
    LUA_TNONE         , // -1  可能是拿这个来充当-1吧
    ;

}