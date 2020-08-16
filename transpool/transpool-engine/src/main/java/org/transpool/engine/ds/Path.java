package org.transpool.engine.ds;

public class Path {
    private final Stop to;
    private final Stop from;
    private final boolean one_way;
    private final int length;
    private double fuelConsumption;
    private int speedLimit;

    public Path(Stop to, Stop from, boolean one_way, int length, int fuelConsumption, int speedLimit) {
        this.to = to;
        this.from = from;
        this.one_way = one_way;
        this.length = length;
        this.fuelConsumption = fuelConsumption;
        this.speedLimit = speedLimit;
    }

    @Override
    public String toString() {
        return "Path{" +
                "to=" + to.getName() +
                ", from=" + from.getName() +
                ", one_way=" + one_way +
                ", length=" + length +
                ", fuelConsumption=" + fuelConsumption +
                ", speedLimit=" + speedLimit +
                '}';
    }

    public Stop getTo() {
        return to;
    }

    public Stop getFrom() {
        return from;
    }

    public boolean isOne_way() {
        return one_way;
    }

    public int getLength() {
        return length;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }
}
