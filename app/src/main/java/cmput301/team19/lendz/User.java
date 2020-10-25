package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private static final String USERNAME_KEY = "username";
    private static final String FULL_NAME_KEY = "fullName";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String OWNED_BOOKS_KEY = "ownedBooks";
    private static final String BORROWED_BOOKS_KEY = "borrowedBooks";

    // Maps user ID to User object, guaranteeing at most
    // one User object for each user.
    private static final HashMap<UUID, User> users = new HashMap<>();

    private UUID id;
    private String username;

    private String fullName;
    private String email;
    private String phoneNumber;

    private final ArrayList<UUID> ownedBookIds = new ArrayList<>();
    private final ArrayList<UUID> borrowedBookIds = new ArrayList<>();

    /**
     * Get or create the unique User object with the given user ID.
     */
    public static User getOrCreate(UUID userId) {
        User user = users.get(userId);
        if (user == null) {
            user = new User(userId);
            users.put(userId, user);
        }
        return user;
    }

    /**
     * @return document of user with ID userId
     */
    public static DocumentReference documentOf(@NonNull UUID userId) {
        return FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId.toString());
    }

    /**
     * Creates a User object by loading data from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     * @return created User, or null if doc is null or does not exist
     */
    public static @Nullable User fromDocument(@Nullable DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) {
            return null;
        }

        User user = getOrCreate(UUID.fromString(doc.getId()));
        user.setUsername(doc.getString(USERNAME_KEY));
        user.setFullName(doc.getString(FULL_NAME_KEY));
        user.setEmail(doc.getString(EMAIL_KEY));
        user.setPhoneNumber(doc.getString(PHONE_NUMBER_KEY));

        user.ownedBookIds.clear();
        Object ownedBookRefs = doc.get(OWNED_BOOKS_KEY);
        if (ownedBookRefs != null) {
            for (DocumentReference bookRef : (List<DocumentReference>) ownedBookRefs) {
                user.ownedBookIds.add(UUID.fromString(bookRef.getId()));
            }
        }
        user.borrowedBookIds.clear();
        Object borrowedBooksRefs = doc.get(BORROWED_BOOKS_KEY);
        if (borrowedBooksRefs != null) {
            for (DocumentReference bookRef : (List<DocumentReference>) borrowedBooksRefs) {
                user.borrowedBookIds.add(UUID.fromString(bookRef.getId()));
            }
        }

        return user;
    }

    /**
     * Converts this User object to a Map.
     */
    private Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        map.put(USERNAME_KEY, username);
        map.put(FULL_NAME_KEY, fullName);
        map.put(EMAIL_KEY, email);
        map.put(PHONE_NUMBER_KEY, phoneNumber);
        List<DocumentReference> ownedBookRefs = new ArrayList<>();
        for (UUID bookId : ownedBookIds) {
            ownedBookRefs.add(
                    FirebaseFirestore.getInstance()
                            .collection("books")
                            .document(bookId.toString()));
        }
        map.put(OWNED_BOOKS_KEY, ownedBookRefs);
        List<DocumentReference> borrowedBookRefs = new ArrayList<>();
        for (UUID bookId : borrowedBookIds) {
            borrowedBookRefs.add(
                    FirebaseFirestore.getInstance()
                            .collection("books")
                            .document(bookId.toString()));
        }
        map.put(BORROWED_BOOKS_KEY, borrowedBookRefs);
        return map;
    }

    /**
     * Store the current state of this User to the Firestore database.
     */
    public Task<Void> store() {
        return documentOf(id).update(toData());
    }

    private User(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
