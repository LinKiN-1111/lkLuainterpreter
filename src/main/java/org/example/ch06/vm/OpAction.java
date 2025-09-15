package org.example.ch06.vm;

import org.example.ch06.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}