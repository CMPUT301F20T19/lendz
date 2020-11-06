package cmput301.team19.lendz;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private static final String BOOK_KEY = "book";
    private static final String REQUESTER_KEY = "requester";
    private static final String STATUS_KEY = "status";

    // Maps request ID to Request object, guaranteeing at most
    // one Request object for each request.
    public static HashMap<String, Request> requests;

    /**
     * Get or create the unique Request object with the given request ID.
     */
    public static Request getOrCreate(@NonNull String id) {
        Request request = requests.get(id);
        if (request == null) {
            request = new Request(id);
            requests.put(id, request);
        }
        return request;
    }

    private final String id;
    private Book book;
    private User requester;
    private RequestStatus status;

    private boolean loaded;

    private Request(@NonNull String id) {
        this.id = id;
    }

    /**
     * @return the associated DocumentReference of this Request
     */
    public DocumentReference getDocumentReference() {
        return FirebaseFirestore.getInstance().collection("requests").document(id);
    }


    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * Updates this Request object with data from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     */
    public void load(@NonNull DocumentSnapshot doc) {
        loaded = true;
        book = Book.getOrCreate(doc.getDocumentReference(BOOK_KEY).getId());
        requester = User.getOrCreate(doc.getDocumentReference(REQUESTER_KEY).getId());
        Long statusLong = doc.getLong(STATUS_KEY);
        if (statusLong == null) {
            status = RequestStatus.SENT;
        } else {
            status = RequestStatus.values()[statusLong.intValue()];
        }
    }

    /**
     * Converts this Request object to a Map.
     */
    public Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        map.put(BOOK_KEY, Book.documentOf(book.getId()));
        map.put(REQUESTER_KEY, User.documentOf(requester.getId()));
        map.put(STATUS_KEY, status.ordinal());
        return map;
    }

    /**
     * Store the current state of this Request to the Firestore database.
     * @return Task of the store
     */
    public Task<Void> store() {
        return getDocumentReference().set(toData(), SetOptions.merge());
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public RequestStatus getStatus() { return status; }


    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isLoaded() {
        return loaded;
    }

}
