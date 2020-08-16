package org.transpool.engine.ds;

public class RequestTrip {
    private static int counter = 100;
    private int id;
    private final String name;
    private String mapName;
    private final String to;
    private final String from;
    private final RequestTime time;
    private boolean comfortable;
    private int flexHours;
    private boolean isMatch;
    private Match match;


    public RequestTrip(String mapName, String name, String to, String from, Time time, String whichTime, boolean comfortable, int flexHours) {
        this.mapName = mapName;
        this.name = name;
        this.to = to;
        this.from = from;
        id = counter;
        counter++;
        this.time = new RequestTime(time, whichTime);
        isMatch = false;
        this.comfortable = comfortable;
        this.flexHours = flexHours;
    }


    public String getMapName() {
        return mapName;
    }

    public RequestTime getTime() {
        return time;
    }

    public boolean isComfortable() {
        return comfortable;
    }

    public int getFlexHours() {
        return flexHours;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public boolean setMatch(Match match) {
        if (isMatch)
            return false;
        isMatch = true;
        this.match = match;
        return true;
    }

    public RequestTime getRequestTime() {
        return time;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public boolean getIsMatch(){
        return isMatch;
    }

    @Override
    public String toString() {
        return
                "Id:" + id +
                        ",Name:" + name +
                        ",Starting station:" + from +
                        ",Destination station:" + to +
                        "," + time;
    }


    public Match getMatch() {
        return match;
    }
}
