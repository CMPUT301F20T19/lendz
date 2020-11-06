package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Immutable object that stores information about the
 * ISBN, title, author, and description of a Book.
 */
public class BookDescription {
    private static final String ISBN_KEY = "isbn";
    private static final String TITLE_KEY = "title";
    private static final String AUTHOR_KEY = "author";
    private static final String DESCRIPTION_KEY = "description";

    private final String isbn;
    private final String title;
    private final String author;
    private final String description;

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

    /**
     * Converts this BookDescription object to a Map.
     */
    public Map<String, Object> toData() {
        Map<String, Object> data = new HashMap<>();
        data.put(ISBN_KEY, isbn);
        data.put(TITLE_KEY, title);
        data.put(AUTHOR_KEY, author);
        data.put(DESCRIPTION_KEY, description);
        return data;
    }

    /**
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return the description of the book
     */
    public String getDescription() {
        return description;
    }
}
