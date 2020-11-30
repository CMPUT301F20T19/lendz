package cmput301.team19.lendz;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Test ViewBooksSection class.
 */
public class ViewBooksSectionTest {
    /**
     * Test that getSectionHeader method returns the correct section header text.
     */
    @Test
    public void testGetSectionHeader() {
        ViewBooksSection availableBooksSection = new ViewBooksSection("Available Books",
                new ArrayList<Book>());
        assertEquals("Available Books", availableBooksSection.getSectionHeader());

        ViewBooksSection requestedBooksSection = new ViewBooksSection("Requested Books",
                new ArrayList<Book>());
        assertEquals("Requested Books", requestedBooksSection.getSectionHeader());

        ViewBooksSection numericalSection = new ViewBooksSection("123",
                new ArrayList<Book>());
        assertEquals("123", numericalSection.getSectionHeader());

        ViewBooksSection alphaNumericalSection = new ViewBooksSection("Section 3",
                new ArrayList<Book>());
        assertEquals("Section 3", alphaNumericalSection.getSectionHeader());

        ViewBooksSection spaceSection = new ViewBooksSection(" ",
                new ArrayList<Book>());
        assertEquals(" ", spaceSection.getSectionHeader());

        ViewBooksSection emptySection = new ViewBooksSection("",
                new ArrayList<Book>());
        assertEquals("", emptySection.getSectionHeader());
    }

    /**
     * Test that getBooks method returns the correct list of books..
     */
    @Test
    public void testGetBooks() {
        // create books for testing
        Book book1 = Book.getOrCreate("123");
        Book book2 = Book.getOrCreate("456");
        Book book3 = Book.getOrCreate("789");

        // check if getBooks method returns an empty array list
        ArrayList<Book> books = new ArrayList<>();
        ViewBooksSection mockSection = new ViewBooksSection("mock section",
                books);
        assertSame(books, mockSection.getBooks());


        // check if getBooks method returns an array list with 1 book
        books.add(book1);
        mockSection = new ViewBooksSection("mock section",
                books);
        assertSame(books, mockSection.getBooks());

        // check if getBooks method returns an array list with 3 books
        books.add(book2);
        books.add(book3);
        mockSection = new ViewBooksSection("mock section",
                books);
        assertSame(books, mockSection.getBooks());
    }
}
