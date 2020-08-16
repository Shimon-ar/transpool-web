package org.transpool.engine.ds;

import org.transpool.engine.TripDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class TranspoolTrip {
    private static int counter = 1;
    private final String name;
    private String mapName;
    private final int id;
    private final int ppk;
    private final int initCapacity;
    private List<String> route;
    private String from;
    private String to;
    private Map<Integer,Map<String,StopManager>> mapStopManager;
    private Scheduling scheduling;
    private Map<Integer,List<Integer>> mapRequestsID;
    private Time checkoutTime;
    private Time arrivalTime;
    private int cost;
    private int fuelCon;
    private double rank;
    private int countRanking;
    List<String> feedbackList;
    private List<AttachedPassenger> attachedPassengers;
    private boolean attached;



    public TranspoolTrip(String mapName,String name, int capacity, int ppk, List<String> route, Scheduling scheduling, MapDb map) {
        from = route.get(0);
        to = route.get(route.size() - 1);
        attached = false;
        attachedPassengers = new ArrayList<>();
        this.mapName = mapName;
        feedbackList = new ArrayList<>();
        rank = 0;
        countRanking = 0;
        this.name = name;
        this.id = counter;
        this.ppk = ppk;
        this.route = route;
        this.scheduling = scheduling;
        initCapacity = capacity;
        mapRequestsID = new HashMap<>();
       /* stopsManager = new LinkedHashMap<>();
        stopsManager = route.stream().collect(Collectors.toMap(x -> x, x -> new StopManager(capacity)));*/
        mapStopManager = new HashMap<>();
        counter++;
        checkoutTime = scheduling.getTime();
        arrivalTime = checkoutTime.clone();
        TripDetails.updateTime(map, route, arrivalTime, true);
        cost = TripDetails.cost(map, ppk, route);
        fuelCon = TripDetails.avgFuelCon(map, route);

    }

    /*public boolean isAvailableToAddTrip(List<String> route) {
        if (requestsID.isEmpty())
            return true;
        if (route.stream().allMatch(x -> stopsManager.containsKey(x)))
            return route.subList(0,route.size()-1).stream().allMatch(x -> stopsManager.get(x).getCapacity() > 0);
        return false;
    }*/

    public String getMapName() {
        return mapName;
    }

    public void addFeedback(String text){
        feedbackList.add(text);
    }

    public List<String> getFeedbackList() {
        return feedbackList;
    }

    public boolean addRequestTrip(int RequestID, String name, String from, String to, int day) {


        if(mapStopManager.get(day) == null)
           createStopManager(day);

        Map<String, StopManager> stopsManager = mapStopManager.get(day);
        if (!stopsManager.containsKey(to) || !stopsManager.containsKey(from) )
            return false;

        getPath(from,to).forEach(x->stopsManager.get(x).dec());
        stopsManager.get(from).addUpPassenger(name);
        stopsManager.get(to).addDownPassenger(name);
        stopsManager.get(to).inc();

        addRequestId(RequestID,day);

        if(!attached)
            attached = true;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        attachedPassengers.add(new AttachedPassenger(dtf.format(now),name,from,to));




        return true;
    }

    private void addRequestId(int requestId,int day){
        List<Integer> requestsIds = mapRequestsID.get(day);
        if(requestsIds != null)
            requestsIds.add(requestId);
        else{
            requestsIds = new ArrayList<>();
            requestsIds.add(requestId);
        }
        mapRequestsID.put(getCheckoutDay(day),requestsIds);

    }

    public Integer getCheckoutDay(int day){
        if(isGoingOutAtDay(day))
            return day;
        else if(isGoingOutAtDay(day-1))
            return day - 1;
        return null;
    }

    private boolean createStopManager(int day){
        Integer checkoutDay = getCheckoutDay(day);
        if(checkoutDay == null)
            return false;
        Map<String, StopManager> stopsManager = route.stream().collect(Collectors.toMap(x -> x, x -> new StopManager(initCapacity)));
        mapStopManager.put(checkoutDay,stopsManager);
        return true;
    }

    public List<String> getPath(String from, String to) {
        if (!route.contains(from) || !route.contains(to) || to.equals(from))
            return null;
        int inxTo, inxFrom;
        inxFrom = route.indexOf(from);
        inxTo = route.indexOf(to);
        if (inxFrom > inxTo)
            return null;
        return route.subList(inxFrom, inxTo + 1);
    }

    public Time whenArrivedToStop(String to, MapDb map,Integer day) {
        if(day == null)
            return null;
        Integer checkoutDay;
        checkoutDay = getCheckoutDay(day);
        if(checkoutDay == null)
           return null;
        Time time = checkoutTime.clone();
        time.setDay(checkoutDay);
        String from = route.get(0);
        if (from.equals(to))
            return time;
        List<String> path = getPath(from, to);
        if (path == null)
            return null;
        time.minToAdd(TripDetails.howLong(map, path));
        return time;
    }


    public List<Integer> getRequestsID(int day) {
        Integer checkoutDay = getCheckoutDay(day);
        if(checkoutDay != null)
        {
            if(mapRequestsID.get(checkoutDay) == null) {
                List<Integer> requestIds = new ArrayList<>();
                mapRequestsID.put(checkoutDay, requestIds);
            }

            return mapRequestsID.get(checkoutDay);

        }
        return null;
    }

    public boolean isGoingOutAtDay(int day){
        if(day < checkoutTime.getDay())
            return false;
        switch (scheduling.getRecurrences()){
            case Daily:return true;
            case Weekly:return (day - checkoutTime.getDay())%7 == 0;
            case BiDaily:return (day - checkoutTime.getDay())%2 == 0;
            case Monthly:return (day - checkoutTime.getDay())%30 == 0;
            case OneTime:return day == checkoutTime.getDay();
            default:return false;
        }
    }



    @Override
    public String toString() {
        return "Id:" + id + ", " +
                "Name:" + name +
                ", Price per kilometer:" + ppk +
                ", Trip cost:" + cost +
                ", Average fuel utilization:" + fuelCon +
                ", Checkout:" + checkoutTime + ", Arrival:" + arrivalTime;
    }

    public int getId() {
        return id;
    }

    public int getInitCapacity() {
        return initCapacity;
    }


    public int getPpk() {
        return ppk;
    }

    public List<String> getRoute() {
        return route;
    }

    public Scheduling getScheduling() {
        return scheduling;
    }

    public String getName() {
        return name;
    }

    public Integer getNextDay(int day){
        if(scheduling.getRecurrences().name().equals(Scheduling.Recurrences.OneTime.name()) && !isGoingOutAtDay(day))
            return null;
        while (!isGoingOutAtDay(day))
               day++;
        return day;
    }

    public Map<String, StopManager> getStopsManager(int day) {
        Integer checkoutDay = getCheckoutDay(day);
        if(checkoutDay == null)
            return null;
        if (mapStopManager.get(checkoutDay) == null)
                createStopManager(checkoutDay);
        return mapStopManager.get(checkoutDay);
    }

    public Time getCheckoutTime(int day) {
        int checkoutDay;
        if(!isGoingOutAtDay(day))
            checkoutDay = day;
        else if(!isGoingOutAtDay(day - 1))
            checkoutDay = day-1;
        else return null;
        Time time = checkoutTime.clone();
        time.setDay(checkoutDay);
        return time;
    }

    public Time getInitCheckout(){
        return checkoutTime;
    }

    public Time getInitArrival(){
        return arrivalTime;
    }

    public Time getArrivalTime(int day) {
        if(!isGoingOutAtDay(day) && !isGoingOutAtDay(day-1))
            return null;
        Time time = arrivalTime.clone();
        int daysToAdd = time.getDay() - checkoutTime.getDay();
        time.setDay(day + daysToAdd);
        return time;
    }

    public String whichStopILocated(Time time,MapDb map) {
        Time checkTime = this.checkoutTime.clone();
        if (isGoingOutAtDay(time.getDay()))
            checkTime.setDay(time.getDay());
        else if (isGoingOutAtDay(time.getDay() - 1))
            checkTime.setDay(time.getDay() - 1);
        else return null;

        if (time.equals(checkTime))
            return route.get(0);
        if (time.before(checkTime))
            return null;

        Time arriveTime = getArrivalTime(checkTime.getDay());
        if (arriveTime.equals(time))
            return route.get(route.size() - 1);

        if (!arriveTime.before(time)) {
            int count = 0;
            for (String stop : route) {
                Time arrivalTime = whenArrivedToStop(stop, map, time.getDay());
                if (arrivalTime.equals(time))
                    return stop;
                if (arrivalTime.before(time) && !whenArrivedToStop(route.get(count + 1), map, time.getDay()).before(time))
                    return stop;
                count++;
            }
        }

        return null;

    }

//    public void addRank(int rank){
//        this.rank *= countRanking ;
//        countRanking++;
//        this.rank = ((double)rank + this.rank)/(double)countRanking;
//    }

    public double getRank() {
        return rank;
    }

    public int getCost() {
        return cost;
    }

    public int getFuelCon() {
        return fuelCon;
    }

    public Time getCheckoutTime() {
        return checkoutTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public int getCountRanking() {
        return countRanking;
    }

    public List<AttachedPassenger> getAttachedPassengers() {
        return attachedPassengers;
    }

    public boolean getAttached() {
        return attached;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}


