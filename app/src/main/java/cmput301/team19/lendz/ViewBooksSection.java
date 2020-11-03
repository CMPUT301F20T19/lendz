package cmput301.team19.lendz;

import java.util.ArrayList;

public class ViewBooksSection {
    private String sectionHeader;
    private ArrayList<Book> books;

    public ViewBooksSection(String sectionHeader, ArrayList<Book> books) {
        this.sectionHeader = sectionHeader;
        this.books = books;
    }

    public String getSectionHeader() {
        return sectionHeader;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }
}
