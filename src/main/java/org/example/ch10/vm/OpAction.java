package org.example.ch09.vm;

import org.example.ch09.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}