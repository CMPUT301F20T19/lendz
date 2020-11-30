package cmput301.team19.lendz.notifications;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import cmput301.team19.lendz.Book;
import cmput301.team19.lendz.MainActivity;
import cmput301.team19.lendz.R;
import cmput301.team19.lendz.Request;
import cmput301.team19.lendz.SearchBooksFragment;
import cmput301.team19.lendz.User;
import cmput301.team19.lendz.ViewBookFragment;
import cmput301.team19.lendz.ViewUserProfileFragment;

public class BookRequestedNotification extends Notification {
    private static final String REQUEST_KEY = "request";

    protected static BookRequestedNotification fromDocumentSnapshot(DocumentSnapshot documentSnapshot, String id, User notifiedUser, long timestamp) {
        DocumentReference requestReference = documentSnapshot.getDocumentReference(REQUEST_KEY);
        if (requestReference == null) {
            throw new NullPointerException("request is missing from documentSnapshot");
        }

        Request request = Request.getOrCreate(requestReference.getId());

        return new BookRequestedNotification(id, notifiedUser, timestamp, request);
    }

    public final Request request;

    public BookRequestedNotification(String id, User notifiedUser, long timestamp, Request request) {
        super(id, NotificationType.BookRequested, notifiedUser, timestamp);
        this.request = request;
    }

    public static class ViewHolder extends NotificationViewHolder {
        public final View itemView;
        public final TextView notificationText;
        public final ImageView bookImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            notificationText = itemView.findViewById(R.id.notification_text);
            bookImageView = itemView.findViewById(R.id.notification_book_photo);
        }

        @Override
        public void bind(final Context context, final Notification n) {
            notificationText.setText("");
            bookImageView.setImageBitmap(null);

            final BookRequestedNotification notification = (BookRequestedNotification) n;

            notification.request.getDocumentReference().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot == null || !documentSnapshot.exists()) {
                        return;
                    }

                    notification.request.load(documentSnapshot);

                    String requesterUsername = notification.request.getRequesterUsername();
                    String requestedBookTitle = notification.request.getBookTitle();
                    notificationText.setText(context.getResources().getString(
                            R.string.book_requested_notification_text, requesterUsername, requestedBookTitle));

                    Picasso.get().load(notification.request.getBookPhotoUrl()).into(bookImageView);

                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainActivity mainActivity = (MainActivity) context;
                            Fragment bookFragment = ViewBookFragment.newInstance(
                                    notification.request.getBook().getId());
                            FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(
                                    R.anim.slide_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.slide_out
                            );

                            transaction.replace(R.id.container, bookFragment);
                            transaction.addToBackStack(null);

                            transaction.commit();
                        }
                    });
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
