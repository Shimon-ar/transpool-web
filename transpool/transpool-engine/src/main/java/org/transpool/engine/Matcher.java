package org.transpool.engine;

import org.transpool.engine.ds.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Matcher {


    private List<Match> optionalMatches;
    private MapDb map;
    private Map<Integer, TranspoolTrip> transPoolTrips;
    private final RequestTrip requestTrip;

    public Matcher(MapDb mapDb, Map<Integer, TranspoolTrip> transPoolTrips, RequestTrip requestTrip) {
        this.map = mapDb;
        optionalMatches = new ArrayList<>();
        this.transPoolTrips = transPoolTrips;
        this.requestTrip = requestTrip;

    }

    private void makeOptionalMatches(int day) throws CloneNotSupportedException {

        String initStop = requestTrip.getFrom();
        String destStop = requestTrip.getTo();

        List<Integer> trips = map.getMap().get(initStop).getTrips();
        Queue<Match> queue = new LinkedList<>();
        for (int offerId : trips) {
            TranspoolTrip transpoolTrip = transPoolTrips.get(offerId);
            if (transpoolTrip.isGoingOutAtDay(day) && transpoolTrip.getStopsManager(day).get(initStop).getCapacity() > 0 ) {
                if(transpoolTrip.whenArrivedToStop(initStop, map, day) != null)
                    queue.add(new Match(offerId, initStop, transpoolTrip.whenArrivedToStop(initStop, map, day)));
            }

        }

        while (!queue.isEmpty()) {
            Match match = queue.remove();
            TranspoolTrip nextTrip = transPoolTrips.get(match.getNextOfferId());
            int index = nextTrip.getRoute().indexOf(match.getLastStop());
            Integer nextTripDay = nextTrip.getNextDay(match.getFinishTime().getDay());
            Map<String, StopManager> stopManagerMap = null;
            if(nextTripDay != null)
                stopManagerMap = nextTrip.getStopsManager(nextTripDay);

            if (nextTrip.getRoute().size() > (++index) && stopManagerMap!=null  &&
                    stopManagerMap.get(match.getLastStop()).getCapacity() > 0) {
                String nextStop = nextTrip.getRoute().get(index);
                Time timeArrived = nextTrip.whenArrivedToStop(nextStop, map,nextTripDay);
                Time timeCheckout = nextTrip.whenArrivedToStop(match.getLastStop(), map,nextTripDay);

                if (nextStop.equals(destStop)) {
                    if(match.isPossibleToAddOffer(nextStop, -1, timeCheckout)) {
                        match = match.addOffer(nextStop, -1, timeArrived, timeCheckout);
                        if (!optionalMatches.contains(match))
                            optionalMatches.add(match);
                    }
                }
                else {
                    List<Integer> offersToAdd = map.getMap().get(nextStop).getTrips();
                    for (int offerId : offersToAdd) {
                        if (match.isPossibleToAddOffer(nextStop, offerId, timeCheckout))
                            queue.add(match.addOffer(nextStop, offerId, timeArrived, timeCheckout));
                    }
                }
            }
        }
    }


    private void updateCostAvgEachMatch() {
        optionalMatches.forEach(match -> {
            List<String> fullRoute = new ArrayList<>();
            List<List<String>> routes = match.getRoutes();
            List<Integer> offersId = match.getOfferIDs();
            List<String> offersNames = match.getOffersNames();
            int count = 0;
            for (List<String> route : routes) {
                fullRoute = Stream.concat(fullRoute.stream(), route.stream()).collect(Collectors.toList());
                fullRoute.remove(fullRoute.size() - 1);
                int payment = TripDetails.cost(map, transPoolTrips.get(offersId.get(count)).getPpk(), route);
                match.addPayment(payment,offersNames.get(count));
                match.setCost(match.getCost() + payment);
                count++;
            }
            fullRoute.add(match.getLastStop());
            match.setAvgFoul(TripDetails.avgFuelCon(map, fullRoute));
        });
    }

    public List<Match> getOptionalMatches(int limit) throws CloneNotSupportedException {

        makeOptionalMatches(requestTrip.getRequestTime().getTime().getDay());

        for (Match match : optionalMatches)
            match.updateOffersNames(transPoolTrips);
        updateCostAvgEachMatch();

        for(Match match:optionalMatches)
            match.setRoadStory(match.createMassage(match));


        if (!requestTrip.isComfortable())
            setSnobMatches(limit);
        boolean checkout = false;

        if (requestTrip.getRequestTime().getWhichTime().name().equals(RequestTime.WhichTime.checkout.name()))
            checkout = true;
        setMatchesByTime(checkout, requestTrip.getFlexHours(), limit, requestTrip.getRequestTime().getTime());
        return optionalMatches;
    }

    private void setMatchesByTime(boolean checkout, int flexHours, int limit, Time requestTime) {
        optionalMatches = optionalMatches.stream().filter(match -> {
            if (checkout) {
                Time startTime = match.getStartTime();
                if (Math.abs(requestTime.getDay() - startTime.getDay()) > 1)
                    return false;
                if (startTime.getDay() < requestTime.getDay()) {
                    int hour = startTime.getHours() + flexHours;
                    if (hour < 24)
                        return false;
                    int hourCom = (hour) % 24;
                    if (hourCom < requestTime.getHours())
                        return false;
                    else if (hourCom == requestTime.getHours())
                        if (startTime.getMinutes() < requestTime.getMinutes())
                            return false;
                }
                if (requestTime.getDay() < startTime.getDay()) {
                    int hour = requestTime.getHours() + flexHours;
                    if (hour < 24)
                        return false;
                    int hourCom = (hour) % 24;
                    if (hourCom < startTime.getHours())
                        return false;
                    else if (hourCom == startTime.getHours())
                        if (requestTime.getMinutes() < startTime.getMinutes())
                            return false;
                }
                if (requestTime.getDay() == startTime.getDay()) {
                    int distance = Math.abs(startTime.getHours() - requestTime.getHours());
                    if (distance > flexHours)
                        return false;
                    if (distance == flexHours) {
                        if (startTime.getHours() < requestTime.getHours()) {
                            if (startTime.getMinutes() < requestTime.getMinutes())
                                return false;
                        }
                        if (requestTime.getHours() < startTime.getHours()) {
                            if (requestTime.getMinutes() < startTime.getMinutes())
                                return false;
                        }
                        if (requestTime.getHours() == startTime.getHours())
                            if (requestTime.getMinutes() != startTime.getMinutes())
                                return false;

                    }
                }
            } else {
                Time finishTime = match.getFinishTime();
                if (Math.abs(requestTime.getDay() - finishTime.getDay()) > 1)
                    return false;
                if (finishTime.getDay() < requestTime.getDay()) {
                    int hour = finishTime.getHours() + flexHours;
                    if (hour < 24)
                        return false;
                    int hourCom = (hour) % 24;
                    if (hourCom < requestTime.getHours())
                        return false;
                    else if (hourCom == requestTime.getHours())
                        if (finishTime.getMinutes() < requestTime.getMinutes())
                            return false;
                }
                if (requestTime.getDay() < finishTime.getDay()) {
                    int hour = requestTime.getHours() + flexHours;
                    if (hour < 24)
                        return false;
                    int hourCom = hour % 24;
                    if (hourCom < finishTime.getHours())
                        return false;
                    else if (hourCom == finishTime.getHours())
                        if (requestTime.getMinutes() < finishTime.getMinutes())
                            return false;
                }
                if (requestTime.getDay() == finishTime.getDay()) {
                    int distance = Math.abs(finishTime.getHours() - requestTime.getHours());
                    if (distance > flexHours)
                        return false;
                    if (distance == flexHours) {
                        if (finishTime.getHours() < requestTime.getHours()) {
                            if (finishTime.getMinutes() < requestTime.getMinutes())
                                return false;
                        }
                        if (requestTime.getHours() < finishTime.getHours()) {
                            if (requestTime.getMinutes() < finishTime.getMinutes())
                                return false;
                        }
                        if (requestTime.getHours() == finishTime.getHours())
                            if (requestTime.getMinutes() != finishTime.getMinutes())
                                return false;
                    }
                }
            }
            return true;
        }).sorted(mySort()).limit(limit).collect(Collectors.toList());

    }

    private Comparator<Match> mySort() {
        return (m1, m2) -> {
            if (m1.getOfferIDs().size() == 1)
                return -1;
            if (m2.getOfferIDs().size() == 1)
                return 1;
            if (m1.getFinishTime().before(m2.getFinishTime()))
                return -1;
            if (m2.getFinishTime().before(m1.getFinishTime()))
                return 1;
            if (TripDetails.howLong(map, TripDetails.appendRoutes(m1)) > TripDetails.howLong(map, TripDetails.appendRoutes(m2)))
                return -1;
            return 1;

        };
    }

    private void setSnobMatches(int limit) {
        optionalMatches = optionalMatches.stream().filter(match -> {
            if (match.getOfferIDs().size() > 1)
                return false;
            return true;
        }).limit(limit).collect(Collectors.toList());
    }


    public boolean setMatch(Match match) {
        if (!optionalMatches.contains(match))
            return false;
        int count = 0;
        List<List<String>> routes = match.getRoutes();
        for (List<String> route : routes) {
            transPoolTrips.get(match.getOfferIDs().get(count)).
                    addRequestTrip(requestTrip.getId(), requestTrip.getName(), route.get(0), route.get(route.size() - 1),
                            match.getTimeForEachRoute().get(count).getCheckoutTime().getDay());
            count++;
        }
        requestTrip.setMatch(match);
        return true;
    }

    public List<Match> getOptionalMatches() {
        return optionalMatches;
    }

    public Match getMatch(int id){
         return optionalMatches.stream().filter(match -> match.getId() == id).collect(Collectors.toList()).get(0);
    }
}
