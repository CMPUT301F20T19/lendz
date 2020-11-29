package cmput301.team19.lendz;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of FirebaseMessagingService. Handles updating of Firebase Cloud Messaging token,
 * receipt of messages and notifications from Firebase, and creates notification channels.
 */
public class FirebaseMessagingServiceImpl extends FirebaseMessagingService {
    private String token;
    private boolean haveStoredToken;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    public void onCreate() {
        super.onCreate();

        // Update token after logging in
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    updateToken();
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);

        // Update token during service creation
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                token = task.getResult();
                updateToken();
            }
        });

        // Create notification channels
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "request_acknowledged";
            String name = getString(R.string.channel_request_acknowledged);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            notificationManager.createNotificationChannel(channel);

            channelId = "book_requested";
            name = getString(R.string.channel_book_requested);
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(channelId, name, importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // If token is the same, do nothing
        if (token.equals(this.token)) {
            return;
        }

        // When token changes or is generated for first time, update the token
        Log.d("FCM", "New token " + token);
        this.token = token;
        haveStoredToken = false;
        updateToken();
    }

    /**
     * Updates the Firebase Cloud Messaging token stored on Firestore for this user
     * to the last generated one.
     */
    public void updateToken() {
        if (User.getCurrentUser() != null && token != null && !haveStoredToken) {
            Map<String, Object> data = new HashMap<>();
            data.put(User.FCM_TOKEN, token);
            User.documentOf(User.getCurrentUser().getId())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            haveStoredToken = true;
                            Log.d("FCM", "Updated Firestore with token " + token);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("FCM", "Failed to update Firestore with token " + token, e);
                        }
                    });
        }
    }

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {
        final RemoteMessage.Notification msgNotification = remoteMessage.getNotification();
        if (msgNotification == null) {
            return;
        }

        // Built notification using message data
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, remoteMessage.getNotification().getChannelId())
                .setContentTitle(msgNotification.getTitle())
                .setContentText(msgNotification.getBody())
                .setSmallIcon(R.drawable.ic_baseline_book_24)
                .setAutoCancel(true);

        // Add intent to notification
        if (msgNotification.getClickAction().equals("view_book")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction("view_book");
            intent.putExtra("bookId", remoteMessage.getData().get("bookId"));
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
        }

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final int notificationId = remoteMessage.getMessageId().hashCode();

        // Load image into notification if one exists
        if (msgNotification.getImageUrl() != null) {
            Handler uiHandler = new Handler(Looper.getMainLooper());
            uiHandler.post(new Runnable(){
                @Override
                public void run() {
                    Picasso.get().load(msgNotification.getImageUrl()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            builder.setLargeIcon(bitmap);
                            // Send notification
                            notificationManager.notify(notificationId, builder.build());

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // Send notification without image
                            notificationManager.notify(notificationId, builder.build());
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
            });
        } else {
            // Send notification
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
