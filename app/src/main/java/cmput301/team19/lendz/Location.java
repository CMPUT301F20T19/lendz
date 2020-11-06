package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

/**
 * Stores information about a location by latitude, longitude, and optional address.
 */
public class Location {
    private String address;
    private double lat;
    private double lon;

    /**
     * Create a Location with an address, latitude, and longitude.
     */
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

    /**
     * @return the address of this Location. may be null
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the longitude
     */
    public double getLon() {
        return lon;
    }

    /**
     * Set the address of this location.
     * @param address address to use. may be null
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Set the latitude of this location.
     * @param lat latitude to use
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Set the longitude of this location.
     * @param lon longitude to use
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
}
