package org.transpool.engine;


import org.transpool.engine.ds.*;

import javax.jws.soap.SOAPBinding;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Engine {

    private Map<String, FullMap> allMaps;
    private List<User> users;
    String errorDes;

    public Engine() {
        allMaps = new HashMap<>();
        users = new ArrayList<>();

    }

    public List<FullMap> getMaps() {
        return new ArrayList<>(allMaps.values());
    }

    public String getErrorDes() {
        return errorDes;
    }

    public boolean loadMap(InputStream inputStream, String mapName, String userUploaded) throws JAXBException, IOException {
        MapBuilder mapBuilder = new MapBuilder();
        if (!mapBuilder.build(inputStream)) {
            errorDes = mapBuilder.getDescription();
            return false;
        }

        FullMap fullMap = new FullMap(mapName, mapBuilder.getMapDb(), userUploaded);
        allMaps.put(userUploaded, fullMap);
        return true;
    }

    public boolean isUserExist(String userName) {
        return users.stream().anyMatch(user -> user.getName().equals(userName));
    }

    public void addUser(String userName, User.Role role) {
        if (isUserExist(userName))
            return;
        users.add(new User(userName, role));
    }

    public User getUser(String userName) {
        List<User> users1 = users.stream().filter(user -> user.getName().equals(userName)).limit(1).collect(Collectors.toList());
        if (users1.isEmpty())
            return null;
        return users1.get(0);
    }

    public FullMap getFullMapByUser(String userUploaded) {
        return allMaps.get(userUploaded);
    }

    public FullMap getFullMapByName(String mapName) {
        List<FullMap> fullMaps = allMaps.values().stream().filter(fullMap -> fullMap.getMapName().equals(mapName)).collect(Collectors.toList());
        if (fullMaps.isEmpty())
            return null;

        return fullMaps.get(0);
    }

    public List<RequestTrip> getRequestsOfUser(String userName) {
        List<List<RequestTrip>> requestLists = allMaps.values().stream().map(fullMap -> fullMap.getRequestTrips().stream()
                .filter(requestTrip -> requestTrip.getName().equals(userName))
                .collect(Collectors.toList())).collect(Collectors.toList());
        List<RequestTrip> requestTrips = new ArrayList<>();
        for (List<RequestTrip> requestTrips1 : requestLists)
            requestTrips.addAll(requestTrips1);
        return requestTrips;
    }

    public RequestTrip inRequest(String mapName, String name, String from, String to, Time time, String whichTime, boolean comfortable, int hourFlex) {
        RequestTrip requestTrip = new RequestTrip(mapName, name, to, from, time, whichTime, comfortable, hourFlex);
        getFullMapByName(mapName).addTrip(requestTrip);
        getUser(name).addTrip(requestTrip);
        return requestTrip;
    }


    public TranspoolTrip addTransPoolTrip(String mapName, String name, List<String> route, Time time, String recurrences, int ppk, int capacity) {
        FullMap fullMap = getFullMapByName(mapName);
        Scheduling scheduling = new Scheduling(recurrences, time);
        TranspoolTrip transpoolTrip = new TranspoolTrip(mapName, name, capacity, ppk, route, scheduling, fullMap.getMap());
        fullMap.addTrip(transpoolTrip);
        getUser(name).addTrip(transpoolTrip);
        route.stream().forEach(s -> {
            fullMap.getMap().getMap().get(s).addTrip(transpoolTrip.getId());
        });
        return transpoolTrip;
    }


    public boolean isValidRoute(List<String> route, String mapName) {
        return TripDetails.isValidRoute(getFullMapByName(mapName).getMap(), route);
    }

    public List<Match> getMatches(int requestId, String mapName, int limit) {
        return getFullMapByName(mapName).getMatches(requestId, limit);
    }


    public boolean setMatch(int matchId, String mapName, String userName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timeString = dtf.format(now);
        FullMap fullMap = getFullMapByName(mapName);
        fullMap.setMatch(matchId);
        User user = getUser(userName);
        Match match = fullMap.getMatcher().getMatch(matchId);
        user.getAccount().addAction(ActionAccount.Action.Payment.name(), match.getCost(), timeString);
        match.getOffersNames().forEach(offerName -> {
            getUser(offerName).getAccount().addAction(ActionAccount.Action.Receive.name(), match.getPayment(offerName), timeString);
            User offerUser = getUser(offerName);
            List<String> alertContent = new ArrayList<>();
            alertContent.add(mapName);
            alertContent.add(Integer.toString(match.getIdOffer(offerName)));
            alertContent.add(Integer.toString(match.getPayment(offerName)));
            offerUser.addAlert(new Alert(alertContent, Alert.Type.match));
        });

        return true;

    }

    public void makeDeposit(String userName, int amount, String time) {
        getUser(userName).getAccount().addAction(ActionAccount.Action.Deposit.name(), amount, time);
    }



    public List<String> getAllMapNames() {
        return allMaps.values().stream().map(FullMap::getMapName).collect(Collectors.toList());
    }


}


