package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

public class Location {

    private String address;
    private double lat;
    private double lon;

    public Location(
            String address,
            double lat,
            double lon) {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Create a Location from a Firebase GeoPoint.
     */
    public Location(@NonNull GeoPoint geoPoint) {
        this(null, geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
