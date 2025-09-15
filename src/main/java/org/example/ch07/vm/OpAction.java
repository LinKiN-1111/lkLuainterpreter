package org.example.ch07.vm;

import org.example.ch07.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}