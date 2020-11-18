package cmput301.team19.lendz.notifications;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import cmput301.team19.lendz.User;

public abstract class Notification {
    private static final String TYPE_KEY = "type";
    public static final String NOTIFIED_USER_KEY = "notifiedUser";
    public static final String TIMESTAMP_KEY = "timestamp";

    public static Notification fromDocumentSnapshot(DocumentSnapshot documentSnapshot) {
        String notificationId = documentSnapshot.getId();

        Long typeLong = documentSnapshot.getLong(TYPE_KEY);
        if (typeLong == null) {
            throw new NullPointerException("type is missing from documentSnapshot");
        }

        DocumentReference userReference = documentSnapshot.getDocumentReference(NOTIFIED_USER_KEY);
        if (userReference == null) {
            throw new NullPointerException("notifiedUser is missing from documentSnapshot");
        }

        Long timestampLong = documentSnapshot.getLong(TIMESTAMP_KEY);
        if (timestampLong == null) {
            throw new NullPointerException("timestamp is missing from documentSnapshot");
        }

        NotificationType type = NotificationType.values()[typeLong.intValue()];
        User notifiedUser = User.getOrCreate(userReference.getId());
        long timestamp = timestampLong;

        switch (type) {
            case BookRequested:
                return BookRequestedNotification.fromDocumentSnapshot(documentSnapshot, notificationId, notifiedUser, timestamp);
            case RequestAcknowledged:
                return RequestAcknowledgedNotification.fromDocumentSnapshot(documentSnapshot, notificationId, notifiedUser, timestamp);
            default:
                throw new IllegalArgumentException("unknown request type " + type);
        }
    }

    public final String id;
    public final NotificationType type;
    public final User notifiedUser;
    public final long timestamp;

    protected Notification(String id, NotificationType type, User notifiedUser, long timestamp) {
        this.id = id;
        this.type = type;
        this.notifiedUser = notifiedUser;
        this.timestamp = timestamp;
    }
}
