package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stores information about a user and provides methods for synchronizing with Firestore.
 */
public class User {
    private static final String USERNAME_KEY = "username";
    private static final String FULL_NAME_KEY = "fullName";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";
    private static final String OWNED_BOOKS_KEY = "ownedBooks";
    private static final String BORROWED_BOOKS_KEY = "borrowedBooks";
    public static final String FCM_TOKEN = "fcmToken";

    // Maps user ID to User object, guaranteeing at most
    // one User object for each user.
    private static final HashMap<String, User> users = new HashMap<>();

    private final String id;
    private String username;

    private String fullName;
    private String email;
    private String phoneNumber;
    private final ArrayList<String> ownedBookIds = new ArrayList<>();
    private final ArrayList<String> borrowedBookIds = new ArrayList<>();

    private boolean loaded;

    /**
     * Get or create the unique User object with the given user ID.
     * @return the User object
     */
    public static User getOrCreate(String userId) {
        User user = users.get(userId);
        if (user == null) {
            user = new User(userId);
            users.put(userId, user);
        }
        return user;
    }

    /**
     * Get the currently logged in User.
     * @return the logged in User, or null if there is none
     */
    public static User getCurrentUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            return User.getOrCreate(firebaseUser.getUid());
        }
        return null;
    }

    /**
     * @return document of user with ID userId
     * @param userId ID of User
     */
    public static DocumentReference documentOf(@NonNull String userId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId.toString());
    }

    /**
     * Update this User object with data from a Firebase DocumentSnapshot.
     * @param doc DocumentSnapshot to load from
     */
    public void load(@NonNull DocumentSnapshot doc) {
        loaded = true;

        setUsername(doc.getString(USERNAME_KEY));
        setFullName(doc.getString(FULL_NAME_KEY));
        setEmail(doc.getString(EMAIL_KEY));
        setPhoneNumber(doc.getString(PHONE_NUMBER_KEY));

        ownedBookIds.clear();
        Object ownedBookRefs = doc.get(OWNED_BOOKS_KEY);
        if (ownedBookRefs != null) {
            for (DocumentReference bookRef : (List<DocumentReference>) ownedBookRefs) {
                ownedBookIds.add(bookRef.getId());
            }
        }
        borrowedBookIds.clear();
        Object borrowedBooksRefs = doc.get(BORROWED_BOOKS_KEY);
        if (borrowedBooksRefs != null) {
            for (DocumentReference bookRef : (List<DocumentReference>) borrowedBooksRefs) {
                borrowedBookIds.add(bookRef.getId());
            }
        }
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
        for (String bookId : ownedBookIds) {
            ownedBookRefs.add(
                    FirebaseFirestore.getInstance()
                            .collection("books")
                            .document(bookId.toString()));
        }
        map.put(OWNED_BOOKS_KEY, ownedBookRefs);
        List<DocumentReference> borrowedBookRefs = new ArrayList<>();
        for (String bookId : borrowedBookIds) {
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
        return documentOf(id).set(toData(), SetOptions.merge());
    }

    private User(String id) {
        this.id = id;
    }

    /**
     * @return the ID of this User
     */
    public String getId() {
        return id;
    }

    /**
     * @return the username of this User
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username of this User
     * @param username username to use
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the full name of this User
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set the full name of this User
     * @param fullName full name to use
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the email address of this User
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email address of this User
     * @param email email address to use
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone number of this User
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Set the phone number of this User
     * @param phoneNumber the phne number to use
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return true if this User has loaded data from Firestore, false otherwise
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * @return a list of IDs of Books this User owns
     */
    public ArrayList<String> getOwnedBookIds() {
        return ownedBookIds;
    }

    /**
     * @return a list of IDs of Books this User is currently borrowing
     */
    public ArrayList<String> getBorrowedBookIds() {
        return borrowedBookIds;
    }
}
