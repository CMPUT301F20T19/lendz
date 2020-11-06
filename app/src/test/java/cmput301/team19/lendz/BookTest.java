package cmput301.team19.lendz;

import org.junit.Test;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

/**
 * Tests the Book class.
 */
public class BookTest {
    /**
     * Test that getting Book objects by ID results in the same object each time.
     */
    @Test
    public void testBooksAreUnique() {
        Book bookA1 = Book.getOrCreate("A");
        Book bookA2 = Book.getOrCreate("A");
        Book bookB = Book.getOrCreate("B");
        assertSame(bookA1, bookA2);
        assertNotSame(bookA1, bookB);
    }
}

