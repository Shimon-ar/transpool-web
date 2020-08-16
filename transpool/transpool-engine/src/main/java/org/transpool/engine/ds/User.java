package org.transpool.engine.ds;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class User {
    private String name;
    private Role role;
    private List<TranspoolTrip> transpoolTrips;
    private List<RequestTrip> requestTrips;
    private double rank;
    private List<String> rankList;
    private int countRanking;
    private Map<String, String> userToFeedback;
    private Account account;
    private List<Alert> alertList;


    public User(String name, Role role) {
        rank = countRanking = 0;
        this.name = name;
        this.role = role;
        transpoolTrips = new ArrayList<>();
        requestTrips = new ArrayList<>();
        userToFeedback = new HashMap<>();
        account = new Account();
        alertList = new ArrayList<>();
        rankList = new ArrayList<>();
    }

    public double getRank() {
        return rank;
    }

    public List<TranspoolTrip> getTranspoolTrips() {
        return transpoolTrips;
    }

    public List<RequestTrip> getRequestTrips() {
        return requestTrips;
    }

    public void addTrip(TranspoolTrip transpoolTrip) {
        transpoolTrips.add(transpoolTrip);
    }

    public void addTrip(RequestTrip requestTrip) {
        requestTrips.add(requestTrip);
    }

    public boolean isOffer() {
        return role == Role.offer;
    }

    public String getName() {
        return name;
    }

    public boolean addRank(String user,int rank) {
        if(rankList.contains(user))
            return false;
        rankList.add(user);
        this.rank *= countRanking;
        countRanking++;
        this.rank = ((double) rank + this.rank) / (double) countRanking;
        return true;
    }

    public boolean addFeedback(String user,String text) {
        if (Role.offer == role && text != null && !text.isEmpty() && !userToFeedback.containsKey(user)) {
            userToFeedback.put(user, text);
            return true;
        }
        return false;
    }

    public Map<String,String> getFeedbackList() {
        return userToFeedback;
    }

    public enum Role {
        offer, request;
    }

    public Account getAccount() {
        return account;
    }

    public void addAlert(Alert alert) {
        alertList.add(alert);
    }

    public Alert getAlert() {
        List<Alert> alerts = alertList.stream().filter(alert -> !alert.isSent()).collect(Collectors.toList());
        if (alerts.isEmpty())
            return null;
        alertList.remove(alerts.get(0));
        return alerts.get(0);
    }
}
