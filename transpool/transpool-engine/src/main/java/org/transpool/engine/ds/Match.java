package org.transpool.engine.ds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match implements Cloneable {
    private static int counter = 0;
    private int id;
    private int lastOfferId;
    private int nextOfferId;
    private String lastStop;
    private List<Integer> offerIDs;
    private List<String> offersNames;
    private List<List<String>> routes;
    private List<RequestTime> timeForEachRoute;
    private Time startTime;
    private Time finishTime;
    private Map<String, Integer> payments;
    private int cost;
    private int avgFoul;
    private String roadStory = "";
    private Map<String,Integer> mapOfferNameToId;


    public Match(Integer offerId, String initStop, Time initTime) {
        id = counter;
        counter++;
        offersNames = new ArrayList<>();
        offerIDs = new ArrayList<>();
        routes = new ArrayList<>();
        List<String> route = new ArrayList<>();
        route.add(initStop);
        routes.add(route);
        offerIDs.add(offerId);
        lastOfferId = offerId;
        nextOfferId = offerId;
        lastStop = initStop;
        timeForEachRoute = new ArrayList<>();
        timeForEachRoute.add(new RequestTime(initTime, "checkout"));
        startTime = initTime;
        finishTime = initTime.clone();
        cost = 0;
        avgFoul = 0;
        payments = new HashMap<>();
        mapOfferNameToId = new HashMap<>();

    }

    public int getId() {
        return id;
    }

    public void addPayment(int amount, String name) {
        payments.put(name, amount);
    }

    public int getPayment(String name) {
        return payments.get(name);
    }

    public int getCost() {
        return cost;
    }

    public int getAvgFoul() {
        return avgFoul;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setAvgFoul(int avgFoul) {
        this.avgFoul = avgFoul;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getFinishTime() {
        return finishTime;
    }

    public int getNextOfferId() {
        return nextOfferId;
    }

    public void setNextOfferId(int nextOfferId) {
        this.nextOfferId = nextOfferId;
    }

    public int getLastOfferId() {
        return lastOfferId;
    }

    public List<RequestTime> getTimeForEachRoute() {
        return timeForEachRoute;
    }

    public List<Integer> getOfferIDs() {
        return offerIDs;
    }

    public List<List<String>> getRoutes() {
        return routes;
    }


    public boolean isPossibleToAddOffer(String stop, int nextOfferId, Time timeCheckout) {

        if (timeCheckout == null)
            return false;


        for (List<String> route : routes)
            if (route.contains(stop))
                return false;

        if (offerIDs.contains(nextOfferId) && this.nextOfferId != nextOfferId)
            return false;

        if (timeCheckout.equals(finishTime))
            return true;
        if (timeCheckout.before(finishTime))
            return false;

        /*if (timeCheckout.getDay() < finishTime.getDay())
            return false;*/

        /*if ((timeCheckout.getDay() - finishTime.getDay()) > 5)
            return false;*/

      /*  if ((timeCheckout.getDay() - finishTime.getDay()) == 1) {
            if ((finishTime.getHours() != 23 || timeCheckout.getHours() != 0))
                return false;
            else return true;
        }*/

        /*int hoursCheck = timeCheckout.getHours() - finishTime.getHours();
        if (timeCheckout.getDay() == finishTime.getDay() && hoursCheck < 0)
            return false;
        *//*if (hoursCheck > 1)
            return false;*//*
        if (timeCheckout.getDay() == finishTime.getDay() && hoursCheck == 0 && timeCheckout.getMinutes() < finishTime.getMinutes())
            return false;*/


        return true;
    }

    @Override
    protected Match clone() throws CloneNotSupportedException {
        Match match = (Match) super.clone();
        match.offerIDs = new ArrayList<>(this.offerIDs);
        match.offersNames = new ArrayList<>(this.offersNames);
        match.timeForEachRoute = new ArrayList<>();
        for (RequestTime requestTime : timeForEachRoute)
            match.timeForEachRoute.add(requestTime.clone());
        //match.timeForEachRoute = new ArrayList<>(this.timeForEachRoute);
        match.startTime = this.startTime.clone();
        match.finishTime = this.finishTime.clone();
        List<List<String>> matchRoutes = new ArrayList<>();
        for (List<String> route : routes)
            matchRoutes.add(new ArrayList<>(route));
        match.routes = matchRoutes;
        match.id = counter;
        counter++;
        return match;

    }

    @Override
    public boolean equals(Object match1) {
        Match match = (Match) match1;
        return this.offerIDs.equals(match.offerIDs);
    }

    public Match addOffer(String stop, int nextOfferId, Time timeArrived, Time timeCheckout) throws CloneNotSupportedException {
        Match match = this.clone();
        RequestTime lastTime = match.timeForEachRoute.get(match.timeForEachRoute.size() - 1);
        List<String> lastRoute = match.routes.get(match.routes.size() - 1);

        if (match.lastOfferId == match.nextOfferId) {
            lastTime.setArrivalTime(timeArrived);
            lastRoute.add(stop);
        } else {
            RequestTime newRequestTime = new RequestTime(timeCheckout, "checkout");
            newRequestTime.setArrivalTime(timeArrived);
            match.timeForEachRoute.add(newRequestTime);
            List<String> route = new ArrayList<>();
            route.add(lastStop);
            route.add(stop);
            match.routes.add(route);
            match.offerIDs.add(match.nextOfferId);

        }

        match.lastOfferId = match.nextOfferId;
        match.nextOfferId = nextOfferId;
        match.lastStop = stop;
        match.finishTime = timeArrived;

        return match;
    }

    public List<String> getOffersNames() {
        return offersNames;
    }

    public void updateOffersNames(Map<Integer, TranspoolTrip> map) {
        for (int id : offerIDs) {
            offersNames.add(map.get(id).getName());
            mapOfferNameToId.put(map.get(id).getName(), id);
        }



    }

    public int getIdOffer(String offerName){
        return mapOfferNameToId.get(offerName);
    }

    public Map<String, Integer> getPayments() {
        return payments;
    }

    public String getRoadStory() {
        return roadStory;
    }

    public String getLastStop() {
        return lastStop;
    }

    public String createMassage(Match match) {
        List<String> offers = new ArrayList<>();
        List<List<String>> route = match.getRoutes();
        List<RequestTime> times = match.getTimeForEachRoute();
        List<String> names = match.getOffersNames();
        for (int i = 0; i < route.size(); i++) {
            String source = route.get(i).get(0);
            String destination = route.get(i).get(route.get(i).size() - 1);
            if (offers.isEmpty())
                offers.add("Leaving with " + names.get(0) + ", from " + source + " at " + times.get(0).getCheckoutTime() +

                        ", and arriving to " + destination + " at " + times.get(0).getArrivalTime()
                );
            else offers.add("then leaving with " + names.get(i) + " at " + times.get(i).getCheckoutTime() +

                    " and arriving to " + destination + " at " + times.get(i).getArrivalTime()
            );

        }

        return String.join(",", offers) + " , price: " + match.getCost() + " , avg fuel: " + match.getAvgFoul();

    }

    public void setRoadStory(String roadStory) {
        this.roadStory = roadStory;
    }
}
