package cmput301.team19.lendz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private static final String OWNER_USERNAME_KEY = "ownerUsername";
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

    private String ownerUsername;

    private boolean loaded;

    private Book(@NonNull UUID id) {
        this.id = id;
        pendingRequests = new ArrayList<>();
    }

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
        loaded = true;

        // Load BookDescription
        Map<String, Object> descriptionMap = (Map<String, Object>) doc.get(DESCRIPTION_KEY);
        if (descriptionMap == null) {
            throw new NullPointerException("description cannot be null");
        }
        setDescription(new BookDescription(descriptionMap));

        // Load location
        GeoPoint geoPoint = doc.getGeoPoint(LOCATION_KEY);
        if (geoPoint == null) {
            setLocation(null);
        } else {
            setLocation(new Location(geoPoint));
        }

        // Load owner
        DocumentReference ownerReference = doc.getDocumentReference(OWNER_KEY);
        if (ownerReference == null) {
            throw new NullPointerException("owner cannot be null");
        }
        User owner = User.getOrCreate(ownerReference.getId());
        setOwner(owner);

        // Load owner username
        ownerUsername = doc.getString(OWNER_USERNAME_KEY);

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
        GeoPoint geoPoint = new GeoPoint(location.getLat(), location.getLon());
        map.put(LOCATION_KEY, geoPoint);
        map.put(OWNER_KEY, User.documentOf(owner.getId()));
        if (photo == null)
            map.put(PHOTO_KEY, null);
        else
            map.put(PHOTO_KEY, photo.toString());

        map.put(STATUS_KEY, status.ordinal());
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

    public void setAcceptedRequest(@Nullable Request acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }

    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public URL getPhoto() {
        return photo;
    }

    public void setPhoto(@Nullable URL photo) {
        this.photo = photo;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(@NonNull User owner) {
        this.owner = owner;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(@NonNull BookStatus status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
    }

    public BookDescription getDescription() {
        return description;
    }

    public void setDescription(@NonNull BookDescription description) {
        this.description = description;
    }

    public void addPendingRequest(@NonNull Request request) {
        this.pendingRequests.add(request);
    }

    /**
     * @return true if this Book has loaded data from Firestore, false otherwise
     */
    public boolean isLoaded() {
        return loaded;
    }
}
