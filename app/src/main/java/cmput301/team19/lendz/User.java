package cmput301.team19.lendz;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private static final String USERNAME_KEY = "username";
    private static final String FULL_NAME_KEY = "fullName";
    private static final String EMAIL_KEY = "email";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";

    private UUID id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;

    public static @Nullable User fromDocument(@Nullable DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) {
            return null;
        }

        User user = new User(UUID.fromString(doc.getId()));
        user.setUsername(doc.getString(USERNAME_KEY));
        user.setFullName(doc.getString(FULL_NAME_KEY));
        user.setEmail(doc.getString(EMAIL_KEY));
        user.setPhoneNumber(doc.getString(PHONE_NUMBER_KEY));

        return user;
    }

    public Map<String, Object> toData() {
        Map<String, Object> map = new HashMap<>();
        map.put(USERNAME_KEY, username);
        map.put(FULL_NAME_KEY, fullName);
        map.put(EMAIL_KEY, email);
        map.put(PHONE_NUMBER_KEY, phoneNumber);
        return map;
    }

    public User(UUID id) {
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
