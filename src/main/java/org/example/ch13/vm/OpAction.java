package org.example.ch13.vm;

import org.example.ch13.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}