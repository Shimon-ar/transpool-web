package org.transpool.engine.ds;

import org.transpool.engine.Matcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FullMap {
    private String mapName;
    private String userUploaded;
    private MapDb map;
    private Map<Integer, TranspoolTrip> transPoolTrips;
    private Map<Integer, RequestTrip> requestTrips;
    private Matcher matcher;

    public FullMap(String mapName, MapDb map, String userUploaded) {
        this.mapName = mapName;
        this.map = map;
        transPoolTrips = new HashMap<>();
        requestTrips = new HashMap<>();
        this.userUploaded = userUploaded;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public String getUserUploaded() {
        return userUploaded;
    }

    public MapDb getMap() {
        return map;
    }

    public List<Match> getMatches(int requestId, int limit) {
        matcher = new Matcher(map, transPoolTrips, requestTrips.get(requestId));

        try {
            return matcher.getOptionalMatches(limit);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getStops() {
        return new ArrayList<>(map.getStops());
    }

    public void setMatch(int matchId) {
        Match _match = matcher.getOptionalMatches().stream().filter(match -> match.getId() == matchId).collect(Collectors.toList()).get(0);
        matcher.setMatch(_match);
    }

    public void addTrip(TranspoolTrip transpoolTrip) {
        transPoolTrips.put(transpoolTrip.getId(), transpoolTrip);
    }

    public void addTrip(RequestTrip requestTrip) {
        requestTrips.put(requestTrip.getId(), requestTrip);
    }

    public List<TranspoolTrip> getTransPoolTrips() {
        return new ArrayList<>(transPoolTrips.values());
    }

    public List<RequestTrip> getRequestTrips() {
        return new ArrayList<>(requestTrips.values());
    }

    public List<RequestTrip> getUnMatchRequested() {
        return getRequestTrips().stream().filter(r -> !r.isMatch()).collect(Collectors.toList());
    }

    public List<RequestTrip> getMatchRequested() {
        return getRequestTrips().stream().filter(RequestTrip::isMatch).collect(Collectors.toList());
    }

    public String getMapName() {
        return mapName;
    }


}
