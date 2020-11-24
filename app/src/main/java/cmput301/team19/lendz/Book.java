package cmput301.team19.lendz;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Stores data about books and provides methods for synchronization with Firestore.
 */
public class Book {
    private static final String DESCRIPTION_KEY = "description";
    private static final String LOCATION_KEY = "location";
    private static final String OWNER_KEY = "owner";
    private static final String OWNER_USERNAME_KEY = "ownerUsername";
    private static final String PHOTO_KEY = "photo";
    private static final String STATUS_KEY = "status";
    private static final String KEYWORDS_KEY = "keywords";
    public static final String ACCEPTED_REQUEST_KEY = "acceptedRequest";
    public static final String ACCEPTED_REQUESTER_KEY = "acceptedRequester";
    public static final String PENDING_REQUESTS_KEY = "pendingRequests";
    public static final String PENDING_REQUESTERS_KEY = "pendingRequesters";

    // Maps book ID to Book object, guaranteeing at most
    // one Book object for each book.
    private static final HashMap<String, Book> books = new HashMap<>();

    private final String id;
    private String photo;
    private User owner;
    private BookStatus status;
    private BookDescription description;
    private List<String> keywords;

    private Request acceptedRequest;
    private User acceptedRequester;

    private String ownerUsername;

    private boolean loaded;

    private Book(@NonNull String id) {
        this.id = id;
    }

    /**
     * Get or create the unique Book object with the given book ID.
     */
    public static Book getOrCreate(@NonNull String bookId) {
        Book book = books.get(bookId);
        if (book == null) {
            book = new Book(bookId);
            books.put(bookId, book);
        }
        return book;
    }

    /**
     * @return document of book with ID bookId
     */
    public static DocumentReference documentOf(@NonNull String bookId) {
        return FirebaseFirestore.getInstance()
                .collection("books")
                .document(bookId.toString());
    }

    /**
     * Updates this Book object with data from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     */
    public void load(@NonNull DocumentSnapshot doc) {
        loaded = true;

        // Load BookDescription
        Map<String, Object> descriptionMap = (Map<String, Object>) doc.get(DESCRIPTION_KEY);
        if (descriptionMap == null) {
            throw new NullPointerException("description cannot be null");
        }
        setDescription(new BookDescription(descriptionMap));

        // Load owner
        DocumentReference ownerReference = doc.getDocumentReference(OWNER_KEY);
        if (ownerReference == null) {
            throw new NullPointerException("owner cannot be null");
        }
        User owner = User.getOrCreate(ownerReference.getId());
        setOwner(owner);

        // Load owner username
        ownerUsername = doc.getString(OWNER_USERNAME_KEY);

        // Load accepted request
        DocumentReference acceptedRequestData = doc.getDocumentReference(ACCEPTED_REQUEST_KEY);
        if (acceptedRequestData == null) {
            acceptedRequest = null;
        } else {
            acceptedRequest = Request.getOrCreate(acceptedRequestData.getId());
        }

        // Load accepted requester
        DocumentReference acceptedRequesterData = doc.getDocumentReference(ACCEPTED_REQUESTER_KEY);
        if (acceptedRequesterData == null) {
            acceptedRequester = null;
        } else {
            acceptedRequester = User.getOrCreate(acceptedRequesterData.getId());
        }

        String photoUrlString = doc.getString(PHOTO_KEY);
        if (photoUrlString == null) {
            setPhoto(null);
        } else {
            setPhoto(photoUrlString);
        }

        Long bookStatusLong = doc.getLong(STATUS_KEY);
        if (bookStatusLong == null) {
            throw new NullPointerException("bookStatus cannot be null");
        }
        setStatus(BookStatus.values()[bookStatusLong.intValue()]);
    }

    /**
     * Converts this Book object to a Map.
     */
    private Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        map.put(DESCRIPTION_KEY, description.toData());

        map.put(OWNER_KEY, User.documentOf(owner.getId()));

        if (photo == null)
            map.put(PHOTO_KEY, null);
        else
            map.put(PHOTO_KEY, photo.toString());
        map.put(STATUS_KEY, status.ordinal());
        map.put(KEYWORDS_KEY, keywords);
        return map;
    }

    /**
     * Store the current state of this Book to the Firestore database.
     * @return Task of the store
     */
    public Task<Void> store() {
        return documentOf(id).set(toData(), SetOptions.merge());
    }

    /**
     * Delete this Book from the Firestore database.
     * @return Task of the deletion
     */
    public Task<Void> delete() {
        return documentOf(id).delete();
    }

    /**
     * @return the accepted Request for this Book, may be null
     */
    public Request getAcceptedRequest() {
        return acceptedRequest;
    }

    /**
     * @return the user who made the accepted request, or null if there is none
     */
    public User getAcceptedRequester() {
        return acceptedRequester;
    }

    /**
     * @return the ID of this Book
     */
    public String getId() {
        return id;
    }

    /**
     * @return the photo URL for this Book, may be null
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * Set the photo URL of this book.
     * @param photo photo URL, may be null for no photo
     */
    public void setPhoto(@Nullable String photo) {
        this.photo = photo;
    }

    /**
     * @return User object for the owner of this Book
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Sets the owner of this Book
     * @param owner user object of the owner
     */
    public void setOwner(@NonNull User owner) {
        this.owner = owner;
    }

    /**
     * @return the username of the owner of this Book
     */
    public String getOwnerUsername() {
        return ownerUsername;
    }

    /**
     * @return the current BookStatus for this Book
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * Set the current BookStatus of this Book
     * @param status status to use
     */
    public void setStatus(@NonNull BookStatus status) {
        this.status = status;
    }

    /**
     * @return the BookDescription of this Book
     */
    public BookDescription getDescription() {
        return description;
    }

    /**
     * Set the BookDescription of this Book
     * @param description BookDescription to use
     */
    public void setDescription(@NonNull BookDescription description) {
        this.description = description;
    }

    /**
     * @return the list of keywords associated with this Book
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Set the list of keywords associated with this Book
     * @param keywords the list to use
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * @return true if this Book has loaded data from Firestore, false otherwise
     */
    public boolean isLoaded() {
        return loaded;
    }
}
