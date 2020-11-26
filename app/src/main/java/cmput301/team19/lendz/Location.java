package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores information about a location by latitude, longitude, and optional address.
 */
public class Location {
    private static final String ADDRESS_KEY = "address";
    private static final String GEOPOINT_KEY = "geopoint";

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
     * Create a new Location for an address and Firebase GeoPoint.
     * @param address address to use
     * @param geoPoint GeoPoint containing latitude and longitude to use
     */
    public Location(String address, GeoPoint geoPoint) {
        this(address, geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    /**
     * Create a Location from a data map.
     * @param data map of data to use
     */
    public Location(@NonNull Map<String, Object> data) {
        this((String) data.get(ADDRESS_KEY), (GeoPoint) data.get(GEOPOINT_KEY));
    }

    /**
     * Create a Firebase GeoPoint from this Location.
     * @return new GeoPoint object
     */
    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put(ADDRESS_KEY, address);
        data.put(GEOPOINT_KEY, new GeoPoint(lat, lon));
        return data;
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
