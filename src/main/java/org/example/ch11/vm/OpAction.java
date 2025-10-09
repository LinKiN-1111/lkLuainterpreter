package org.example.ch11.vm;

import org.example.ch11.api.LuaVM;

@FunctionalInterface
public interface OpAction {

    void execute(int i, LuaVM vm);

}