package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

public class BookDescription {
    private static final String ISBN_KEY = "isbn";
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String DESCRIPTION_KEY = "description";

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

    /**
     * Create a BookDescription from a data Map.
     */
    public BookDescription(@NonNull Map<String, Object> data) {
        isbn = (String) data.get(ISBN_KEY);
        title = (String) data.get(TITLE_KEY);
        author = (String) data.get(AUTHOR_KEY);
        description = (String) data.get(DESCRIPTION_KEY);
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
