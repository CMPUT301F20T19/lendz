package cmput301.team19.lendz.notifications;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import cmput301.team19.lendz.Book;
import cmput301.team19.lendz.R;
import cmput301.team19.lendz.Request;
import cmput301.team19.lendz.RequestStatus;
import cmput301.team19.lendz.User;

public class RequestAcknowledgedNotification extends Notification {
    private static final String REQUEST_KEY = "request";

    protected static RequestAcknowledgedNotification fromDocumentSnapshot(DocumentSnapshot documentSnapshot, String id, User notifiedUser, long timestamp) {
        DocumentReference requestReference = documentSnapshot.getDocumentReference(REQUEST_KEY);
        if (requestReference == null) {
            throw new NullPointerException("request is missing from documentSnapshot");
        }

        Request request = Request.getOrCreate(requestReference.getId());

        return new RequestAcknowledgedNotification(id, notifiedUser, timestamp, request);
    }

    public final Request request;

    public RequestAcknowledgedNotification(String id, User notifiedUser, long timestamp, Request request) {
        super(id, NotificationType.RequestAcknowledged, notifiedUser, timestamp);
        this.request = request;
    }

    public static class ViewHolder extends NotificationViewHolder {
        private final TextView notificationText;
        private final ImageView bookImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notification_text);
            bookImageView = itemView.findViewById(R.id.notification_book_photo);
        }

        @Override
        public void bind(final Context context, final Notification n) {
            notificationText.setText("");
            bookImageView.setImageBitmap(null);

            final RequestAcknowledgedNotification notification = (RequestAcknowledgedNotification) n;

            notification.request.getDocumentReference().get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot == null) {
                        return;
                    }

                    notification.request.load(documentSnapshot);

                    String ownerUsername = notification.request.getOwnerUsername();
                    String bookTitle = notification.request.getBookTitle();
                    String bookPhotoUrl = notification.request.getBookPhotoUrl();

                    int stringResourceId = notification.request.getStatus() == RequestStatus.ACCEPTED
                            ? R.string.request_accepted_notification_text
                            : R.string.request_declined_notification_text;

                    notificationText.setText(context.getResources().getString(
                            stringResourceId, ownerUsername, bookTitle));

                    Picasso.get().load(bookPhotoUrl).into(bookImageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Notification", "Failed to load request", e);
                }
            });
        }
    }
}
