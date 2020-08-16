package org.transpool.engine.ds;


import java.util.List;

public class Alert {
    private List<String> list;
    private boolean isSent;
    private Type type;


    public enum Type{
        rank,match
    }
    public Alert(List<String> list,Type type) {
        this.list = list;
        isSent = false;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public List<String> getList() {
        return list;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(){
        isSent = true;
    }
}
