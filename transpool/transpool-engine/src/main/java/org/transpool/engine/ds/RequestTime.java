package org.transpool.engine.ds;

public class RequestTime implements Cloneable {
    private Time arrivalTime;
    private Time checkoutTime;
    WhichTime whichTime;

    public enum WhichTime {
        arrival, checkout
    }

    public RequestTime(Time time, String whichTime) {
        if (whichTime.equals(WhichTime.arrival.name()))
            this.whichTime = WhichTime.arrival;
        else this.whichTime = WhichTime.checkout;
        arrivalTime = time;
        checkoutTime = time.clone();
    }
    public Time getTime(){
        if(WhichTime.arrival == whichTime)
            return arrivalTime;
        else
            return checkoutTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public Time getCheckoutTime() {
        return checkoutTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return  whichTime + ":"+ getTime();
    }

    public WhichTime getWhichTime() {
        return whichTime;
    }

    @Override
    protected RequestTime clone() throws CloneNotSupportedException {
        RequestTime requestTime = (RequestTime)super.clone();
        requestTime.checkoutTime = this.checkoutTime.clone();
        requestTime.arrivalTime = this.arrivalTime.clone();
        return requestTime;
    }
}
