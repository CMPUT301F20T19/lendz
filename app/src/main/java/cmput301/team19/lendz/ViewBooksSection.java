package cmput301.team19.lendz;

import java.util.ArrayList;

public class ViewBooksSection {
    private String sectionHeader;
    private ArrayList<Book> books;

    public ViewBooksSection(String sectionHeader, ArrayList<Book> books) {
        this.sectionHeader = sectionHeader;
        this.books = books;
    }

    /**
     * @return sectionHeader the text at the top of a section
     */
    public String getSectionHeader() {
        return sectionHeader;
    }

    /**
     * @return books an array list of book objects whose data is to be shown
     */
    public ArrayList<Book> getBooks() {
        return books;
    }
}
