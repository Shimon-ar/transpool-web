package org.transpool.engine.ds;

import java.util.ArrayList;
import java.util.List;

public class TripsManager {
    private int numOfTrips;
    private List<TranspoolTrip> trips;

    public TripsManager() {
        trips = new ArrayList<>();
        numOfTrips = 0;
    }

    public int getNumOfTrips() {
        return numOfTrips;
    }

    public List<TranspoolTrip> getTrips() {
        return trips;
    }

    public void addTrip(TranspoolTrip transpoolTrip){
        numOfTrips++;
        trips.add(transpoolTrip);
    }

    public void removeTrip(TranspoolTrip transpoolTrip){
        numOfTrips--;
        trips.remove(transpoolTrip);
    }
}
