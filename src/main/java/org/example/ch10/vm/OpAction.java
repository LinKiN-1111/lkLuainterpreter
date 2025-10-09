package org.example.ch10.vm;

import org.example.ch10.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}