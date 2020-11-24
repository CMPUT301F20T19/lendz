package cmput301.team19.lendz;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the Location class.
 */
public class LocationTest {
    /**
     * Tests GeoPoint constructor.
     */
    @Test
    public void testFromData() {
        double latitude = 21.8;
        double longitude = 103.5;
        Map<String, Object> data = new HashMap<>();
        data.put("geopoint", new GeoPoint(latitude, longitude));
        String address = "placeworld";
        data.put("address", address);

        Location location = new Location(data);

        assertEquals(latitude, location.getLat(), 0);
        assertEquals(longitude, location.getLon(), 0);
        assertEquals(address, location.getAddress());
    }
}
