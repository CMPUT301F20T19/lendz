package cmput301.team19.lendz;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testUsersAreUnique() {
        User userA1 = User.getOrCreate("A");
        User userA2 = User.getOrCreate("A");
        User userB = User.getOrCreate("B");
        assertSame(userA1, userA2);
        assertNotSame(userA1, userB);
    }
}
