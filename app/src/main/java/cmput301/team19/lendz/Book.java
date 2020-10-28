package cmput301.team19.lendz;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    private final ArrayList<Request> pendingRequests;
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
     * Updates this Book object with data from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     */
    public void load(@NonNull DocumentSnapshot doc) {
        DocumentReference documentReference = Book.documentOf(id);

        Map<String, Object> descriptionMap = (Map<String, Object>) doc.get(DESCRIPTION_KEY);
        setDescription(new BookDescription(descriptionMap));

        GeoPoint geoPoint = doc.getGeoPoint(LOCATION_KEY);
        if (geoPoint == null) {
            setLocation(null);
        } else {
            setLocation(new Location(geoPoint));
        }

        DocumentReference ownerReference = doc.getDocumentReference(OWNER_KEY);
        if (ownerReference == null) {
            setOwner(null);
        } else {
            User owner = User.getOrCreate(ownerReference.getId());
            setOwner(owner);
        }

        // TODO: get pendingRequests and acceptedRequest data
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
            setPhoto(null);
        } else {
            try {
                setPhoto(new URL(photoUrlString));
            } catch (MalformedURLException e) {
                setPhoto(null);
                Log.e("Book", "Failed to parse book photo URL " +
                        photoUrlString + ": " + e);
            }
        }

        Long bookStatusLong = doc.getLong(STATUS_KEY);
        if (bookStatusLong == null) {
            setStatus(null);
        } else {
            setStatus(BookStatus.values()[bookStatusLong.intValue()]);
        }
    }

    /**
     * Converts this Book object to a Map.
     */
    private Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        map.put(DESCRIPTION_KEY, description.toData());
        GeoPoint geoPoint = new GeoPoint(location.getLat(), location.getLon());
        map.put(LOCATION_KEY, geoPoint);
        map.put(OWNER_KEY, User.documentOf(owner.getId()));
        // TODO: set pendingRequests data
        // TODO: set acceptedRequest data
        map.put(PHOTO_KEY, photo.toString());
        map.put(STATUS_KEY, status.ordinal());
        return map;
    }

    /**
     * Store the current state of this Book to the Firestore database.
     */
    public Task<Void> store() {
        return documentOf(id).set(toData(), SetOptions.merge());
    }

    private Book(@NonNull UUID id) {
        this.id = id;
        pendingRequests = new ArrayList<>();
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

    public ArrayList<Request> getPendingRequests() {
        return pendingRequests;
    }

    public Request getAcceptedRequest() {
        return acceptedRequest;
    }

    public void setAcceptedRequest(Request acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }
}
