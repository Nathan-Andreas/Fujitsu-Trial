package ee.fujitsu.deliverytask.fee.models;

public enum City {
    TALLINN("Tallinn-Harku"),
    TARTU("Tartu-Tõravere"),
    PARNU("Pärnu");

    private final String stationName;

    City(String station) {
        this.stationName = station;
    }
    public String getStationName() {
        return stationName;
    }
}
