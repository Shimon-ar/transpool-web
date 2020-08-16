package org.transpool.engine.ds;

public class AttachedPassenger {
    private String date;
    private String name;
    private String up;
    private String down;

    public AttachedPassenger(String date, String name, String up, String down) {
        this.date = date;
        this.name = name;
        this.up = up;
        this.down = down;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getUp() {
        return up;
    }

    public String getDown() {
        return down;
    }
}
