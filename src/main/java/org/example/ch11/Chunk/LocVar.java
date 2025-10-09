package org.example.ch11.Chunk;

//注意这里可能会有bug,因为pc是uint32类型,由于java的问题被我改了....这里用long存,因为不考虑长度了
public class LocVar {
    private String  VarName;
    private long    StartPC;
    private long    EndPC;

    public String getVarName() {
        return VarName;
    }

    public void setVarName(String varName) {
        VarName = varName;
    }

    public long getStartPC() {
        return StartPC;
    }

    public void setStartPC(long startPC) {
        StartPC = startPC;
    }

    public long getEndPC() {
        return EndPC;
    }

    public void setEndPC(long endPC) {
        EndPC = endPC;
    }
}
