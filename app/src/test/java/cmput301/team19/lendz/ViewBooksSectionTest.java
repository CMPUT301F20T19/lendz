package cmput301.team19.lendz;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class ViewBooksSectionTest {
    @Test
    void testGetSectionHeader() {
        ViewBooksSection mockSection = new ViewBooksSection("mock section",
                new ArrayList<Book>());
        assertEquals("mock section", mockSection.getSectionHeader());
    }

    @Test
    void testGetBooks() {
        Book book1 = Book.getOrCreate("123");
        Book book2 = Book.getOrCreate("456");

        ArrayList<Book> books = new ArrayList<>();
        books.add(book1);
        books.add(book2);

        ViewBooksSection mockSection = new ViewBooksSection("mock section",
                books);
        assertSame(books, mockSection.getBooks());
    }
}
