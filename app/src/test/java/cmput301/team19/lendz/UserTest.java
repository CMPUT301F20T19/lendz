package cmput301.team19.lendz;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the User class.
 */
public class UserTest {
    /**
     * Test that getting User objects by ID results in the same object each time.
     */
    @Test
    public void testUsersAreUnique() {
        User userA1 = User.getOrCreate("A");
        User userA2 = User.getOrCreate("A");
        User userB = User.getOrCreate("B");
        assertSame(userA1, userA2);
        assertNotSame(userA1, userB);
    }
}
