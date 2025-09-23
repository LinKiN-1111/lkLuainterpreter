package org.example.ch08.vm;

import org.example.ch08.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}