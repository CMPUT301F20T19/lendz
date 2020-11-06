package cmput301.team19.lendz;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the Location class.
 */
public class LocationTest {
    /**
     * Tests GeoPoint constructor.
     */
    @Test
    public void testFromGeoPoint() {
        double latitude = 21.8;
        double longitude = 103.5;

        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        Location location = new Location(geoPoint);

        assertEquals(latitude, location.getLat(), 0);
        assertEquals(longitude, location.getLon(), 0);
    }
}
