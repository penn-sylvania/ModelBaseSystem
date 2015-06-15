package controller;

public class IllegalVarNameException extends Exception {

    private String var;
    private int index;

    public IllegalVarNameException(String var, int index) {
        this.var = var;
        this.index = index;
    }

    public String getVar() {
        return var;
    }

    public int getIndex() {
        return index;
    }

}
