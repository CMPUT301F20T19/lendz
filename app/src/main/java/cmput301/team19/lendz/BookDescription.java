package cmput301.team19.lendz;

public class BookDescription {

    private String isbn;
    private String title;
    private String author;
    private String description;

    public BookDescription(
            String isbn,
            String title,
            String author,
            String description
    ) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }
}
