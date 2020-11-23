package cmput301.team19.lendz;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public interface BookDescriptionLoadListener {
        void onSuccess(BookDescription bookDescription);

        void onFailure(Exception e);
    }

    public static void loadFromInternet(final String isbn,
                                        Context context,
                                        final BookDescriptionLoadListener listener) {
        if (isbn == null) {
            throw new NullPointerException("isbn cannot be null");
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalItems = response.getInt("totalItems");
                            if (totalItems < 1) {
                                throw new Exception("book with that ISBN not found");
                            }

                            JSONArray items = response.getJSONArray("items");
                            JSONObject item = items.getJSONObject(0);
                            JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                            String title = volumeInfo.getString("title");
                            JSONArray authors = volumeInfo.getJSONArray("authors");
                            String author = authors.join(", ");
                            String description = volumeInfo.getString("description");

                            BookDescription bookDescription = new BookDescription(
                                    isbn, title, author, description);
                            listener.onSuccess(bookDescription);
                        } catch (Exception e) {
                            listener.onFailure(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onFailure(error);
                    }
                }
        );
        Volley.newRequestQueue(context).add(request);
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
