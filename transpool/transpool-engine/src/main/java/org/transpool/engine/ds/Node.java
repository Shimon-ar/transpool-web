package org.transpool.engine.ds;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final Stop stop;
    private List<Path> paths;
    private List<Integer> tripsID;

    public Node(Stop stop) {
        this.stop = stop;
        this.paths = new ArrayList<>();
        this.tripsID = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "stop=" + stop.getName() +
                ", paths=" + paths +
                ", trips=" + tripsID +
                '}';
    }

    public Stop getStop() {
        return stop;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public boolean addPath(Path path){
        if(paths.contains(path))
            return false;
        paths.add(path);
        return true;
    }

    public boolean addTrip(int idTrip){
        if(tripsID.contains(idTrip))
            return false;
        tripsID.add(idTrip);
        return true;
    }

    public List<Integer> getTrips() {
        return tripsID;
    }
}
