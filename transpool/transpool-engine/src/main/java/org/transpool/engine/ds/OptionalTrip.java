package org.transpool.engine.ds;

public class OptionalTrip {
    private int transPoolID;
    private String name;
    private int cost;
    private int fuelCon;
    private Time arrivalTime;
    private Time checkoutTime;

    public OptionalTrip(int transPoolID, String name, int cost, int fuelCon, Time arrivalTime, Time checkoutTime) {
        this.transPoolID = transPoolID;
        this.name = name;
        this.cost = cost;
        this.fuelCon = fuelCon;
        this.arrivalTime = arrivalTime;
        this.checkoutTime = checkoutTime;
    }

    public int getTransPoolID() {
        return transPoolID;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public int getFuelCon() {
        return fuelCon;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public Time getCheckoutTime() {
        return checkoutTime;
    }
}
