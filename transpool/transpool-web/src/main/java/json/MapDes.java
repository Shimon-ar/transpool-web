package json;

public class MapDes {
    private String mapName;
    private String userUploaded;
    private int totalRoads;
    private int totalStations;
    private int totalTripOffers;
    private int totalTripRequests;
    private int totalMatchRequests;

    public MapDes(String mapName, String userUploaded, int totalRoads, int totalStations, int totalTripOffers, int totalTripRequests, int totalMatchRequests) {
        this.mapName = mapName;
        this.userUploaded = userUploaded;
        this.totalRoads = totalRoads;
        this.totalStations = totalStations;
        this.totalTripOffers = totalTripOffers;
        this.totalTripRequests = totalTripRequests;
        this.totalMatchRequests = totalMatchRequests;
    }

    public String getMapName() {
        return mapName;
    }

    public String getUserUploaded() {
        return userUploaded;
    }

    public int getTotalRoads() {
        return totalRoads;
    }

    public int getTotalStations() {
        return totalStations;
    }

    public int getTotalTripOffers() {
        return totalTripOffers;
    }

    public int getTotalTripRequests() {
        return totalTripRequests;
    }

    public int getTotalMatchRequests() {
        return totalMatchRequests;
    }
}
