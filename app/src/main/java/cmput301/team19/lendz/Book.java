package cmput301.team19.lendz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Book {
    private static final String ACCEPTED_REQUEST_KEY = "acceptedRequest";
    private static final String DESCRIPTION_KEY = "description";
    private static final String LOCATION_KEY = "location";
    private static final String OWNER_KEY = "owner";
    private static final String PENDING_REQUESTS_KEY = "pendingRequests";
    private static final String PHOTO_KEY = "photo";
    private static final String STATUS_KEY = "status";

    // Maps book ID to Book object, guaranteeing at most
    // one Book object for each book.
    private static final HashMap<UUID, Book> books = new HashMap<>();

    private UUID id;
    private URL photo;
    private User owner;
    private BookStatus status;
    private Location location;
    private BookDescription description;

    private ArrayList<Request> pendingRequests;
    private Request acceptedRequest;

    /**
     * Get or create the unique Book object with the given book ID.
     */
    public static Book getOrCreate(@NonNull UUID bookId) {
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
    public static DocumentReference documentOf(@NonNull UUID bookId) {
        return FirebaseFirestore.getInstance()
                .collection("books")
                .document(bookId.toString());
    }

    /**
     * Creates or updates a Book object by loading from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     * @return updated Book if doc is non-null and exists
     */
    public static @Nullable Book fromDocument(@Nullable DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) {
            return null;
        }

        Book book = getOrCreate(UUID.fromString(doc.getId()));

        Map<String, Object> descriptionMap = (Map<String, Object>) doc.get(DESCRIPTION_KEY);
        book.setDescription(new BookDescription(descriptionMap));

        GeoPoint geoPoint = doc.getGeoPoint(LOCATION_KEY);
        if (geoPoint == null) {
            book.setLocation(null);
        } else {
            book.setLocation(new Location(geoPoint));
        }

        DocumentReference ownerReference = doc.getDocumentReference(OWNER_KEY);
        if (ownerReference == null) {
            book.setOwner(null);
        } else {
            User owner = User.getOrCreate(UUID.fromString(ownerReference.getId()));
            book.setOwner(owner);
        }

        // TODO: get pendingRequests and acceptedRequsest data
        /*
        List<DocumentReference> pendingRequestsData =
                (List<DocumentReference>) doc.get(PENDING_REQUESTS_KEY);
        for (DocumentReference pendingRequest : pendingRequestsData) {
            // TODO
        }

        DocumentReference acceptedRequestData = doc.getDocumentReference(ACCEPTED_REQUEST_KEY);
        // TODO
         */

        String photoUrlString = doc.getString(PHOTO_KEY);
        if (photoUrlString == null) {
            book.setPhoto(null);
        } else {
            try {
                book.setPhoto(new URL(photoUrlString));
            } catch (MalformedURLException e) {
                book.setPhoto(null);
                Log.e("Book", "Failed to parse book photo URL " +
                        photoUrlString + ": " + e);
            }
        }

        Long bookStatusLong = doc.getLong(STATUS_KEY);
        if (bookStatusLong == null) {
            book.setStatus(null);
        } else {
            book.setStatus(BookStatus.values()[bookStatusLong.intValue()]);
        }

        return book;
    }

    /**
     * Converts this Book object to a Map.
     */
    private Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        // TODO
        return map;
    }

    /**
     * Store the current state of this Book to the Firestore database.
     */
    public Task<Void> store() {
        return documentOf(id).update(toData());
    }

    private Book(@NonNull UUID id) {
        this.id = id;
    }

    public void setAcceptedRequest(Request acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public URL getPhoto() {
        return photo;
    }

    public void setPhoto(URL photo) {
        this.photo = photo;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BookDescription getDescription() {
        return description;
    }

    public void setDescription(BookDescription description) {
        this.description = description;
    }

    public void addPendingRequest(Request request) {
        this.pendingRequests.add(request);
    }
}
