package org.transpool.engine.ds;

import java.util.ArrayList;
import java.util.List;

public class StopManager implements Cloneable {
    private List<String> upCostumers;
    private List<String> downCostumers;
    private int capacity;


    public StopManager(int capacity) {
        upCostumers = new ArrayList<>();
        downCostumers = new ArrayList<>();
        this.capacity = capacity;
    }

    public void addUpPassenger(String name) {
        upCostumers.add(name);
    }


    public void addDownPassenger(String name) {
        downCostumers.add(name);
    }

    public void inc(){
        capacity++;
    }

    public void dec(){
        capacity--;
    }

    public List<String> getUpCostumers() {
        return upCostumers;
    }

    public List<String> getDownCostumers() {
        return downCostumers;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public StopManager clone() {
        StopManager stopManager = null;
        try {
            stopManager = (StopManager)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        stopManager.upCostumers = new ArrayList<>(this.upCostumers);
        stopManager.downCostumers = new ArrayList<>(this.downCostumers);
        return stopManager;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
