package org.example.ch12.vm;

import org.example.ch12.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}