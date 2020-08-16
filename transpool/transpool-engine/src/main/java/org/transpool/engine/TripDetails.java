package org.transpool.engine;

import org.transpool.engine.ds.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TripDetails {

    private static int lengthRoute(MapDb map, List<String> route) {
        int lengthRoute = 0, length = route.size();
        for (int i = 0; i < length - 1; i++) {
            Path path = findPath(map, route.get(i), route.get(i + 1));
            lengthRoute += path.getLength();
        }
        return lengthRoute;
    }

    public static int cost(MapDb map, int ppk, List<String> route) {
        return lengthRoute(map, route) * ppk;
    }


    public static Path findPath(MapDb map, String from, String to) {
        Node nodeFrom = map.getMap().get(from);
        Node nodeTo = map.getMap().get(to);
        if (nodeFrom == null || nodeTo == null)
            return null;
        List<Path> paths = nodeFrom.getPaths();
        for (Path path : paths) {
            if (path.getTo().getName().equals(to))
                return path;
        }
        return null;
    }

    public static boolean isValidRoute(MapDb map, List<String> route) {
        int length = route.size();
        if(length < 2)
            return false;
        List<String> stopsPast = new ArrayList<>();
        stopsPast.add(route.get(0));
        for (int i = 0; i < length - 1; i++){
            if (findPath(map, route.get(i), route.get(i + 1)) == null)
                return false;
            if(stopsPast.contains(route.get(i+1)))
                return false;
            stopsPast.add(route.get(i+1));
        }
        return true;
    }

    public static int howLong(MapDb map, List<String> route) {
        double timeH = 0;
        int length = route.size();

        for (int i = 0; i < length - 1; i++) {
            Path path = findPath(map, route.get(i), route.get(i + 1));
            timeH += (double)path.getLength() / (double)path.getSpeedLimit();
        }
        return (int)(timeH * 60.0);
    }

    public static int avgFuelCon(MapDb map, List<String> route) {
        int lengthRoute = lengthRoute(map, route);
        double lSum = 0;
        int length = route.size();
        for (int i = 0; i < length - 1; i++) {
            Path path = findPath(map, route.get(i), route.get(i + 1));
            lSum += ((double)(path.getLength())/path.getFuelConsumption());
        }
        return (int)(lengthRoute/lSum);
    }

    public static void updateTime(MapDb map, List<String> route, Time checkoutTime,boolean flag){
        int minutesToAdd = howLong(map,route);
        if(!flag)
            minutesToAdd = -1*minutesToAdd;
        checkoutTime.minToAdd(minutesToAdd);
    }

    public static void updateTripForEachStop(List<String> route, int tripID,MapDb map) {
        for (String stopName : route)
            map.getMap().get(stopName).getTrips().add(tripID);
    }

   public static List<String> appendRoutes(Match match){
       List<String> fullRoute = new ArrayList<>();
       List<List<String>> routes = match.getRoutes();
       int count = 0;
       for(List<String> route:routes){
           fullRoute = Stream.concat(fullRoute.stream(), route.stream()).collect(Collectors.toList());
           fullRoute.remove(fullRoute.size() - 1);

       }
       fullRoute.add(match.getLastStop());
       return fullRoute;
   }

}


