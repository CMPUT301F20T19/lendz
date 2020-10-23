package cmput301.team19.lendz;

public class Location {

    private String address;
    private Double lat;
    private Double lon;

    public Location(
            String address,
            Double lat,
            Double lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
