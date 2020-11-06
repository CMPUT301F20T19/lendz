package cmput301.team19.lendz;

import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Tests the Request class.
 */
public class RequestTest {
    /**
     * Test that getting Request objects by ID results in the same object each time.
     */
    @Test
    public void testRequestsAreUnique() {
        Request requestA1 = Request.getOrCreate("A");
        Request requestA2 = Request.getOrCreate("A");
        Request requestB = Request.getOrCreate("B");
        assertSame(requestA1, requestA2);
        assertNotSame(requestA1, requestB);
    }
}
