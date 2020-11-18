package cmput301.team19.lendz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cmput301.team19.lendz.notifications.BookRequestedNotification;
import cmput301.team19.lendz.notifications.Notification;
import cmput301.team19.lendz.notifications.NotificationAdapter;
import cmput301.team19.lendz.notifications.RequestAcknowledgedNotification;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment {
    private NotificationsFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NotificationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        final ArrayList<Notification> notifications = new ArrayList<>();

        //makeTestNotifications(notifications);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        final NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo(Notification.NOTIFIED_USER_KEY, User.documentOf(currentUserId))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        notifications.clear();

                        for (DocumentSnapshot notificationSnapshot : queryDocumentSnapshots) {
                            notifications.add(Notification.fromDocumentSnapshot(notificationSnapshot));
                        }

                        Collections.sort(notifications, new Comparator<Notification>() {
                            @Override
                            public int compare(Notification o1, Notification o2) {
                                return Long.valueOf(o1.timestamp).compareTo(Long.valueOf(o2.timestamp));
                            }
                        });

                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.failed_to_get_notifications, Toast.LENGTH_LONG).show();
                        Log.e("NotificationsFragment", "Failed to get notifications", e);
                    }
        });

        return view;
    }

    private void makeTestNotifications(ArrayList<Notification> notifications) {
        User thisUser = User.getOrCreate(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Request requestA = Request.getOrCreate("2121");
        final Book bookA = Book.getOrCreate("A6WzyLPM8UhSGQUQV6J3");
        requestA.setBook(bookA);
        final User userA = User.getOrCreate("ZO4nCClyAbMhhuPhJwQY6wwWW0x1");
        requestA.setRequester(userA);
        requestA.setStatus(RequestStatus.SENT);
        long timeA = System.currentTimeMillis() - 3000;
        requestA.setTimestamp(timeA);
        BookRequestedNotification bookRequestedNotificationA = new BookRequestedNotification("0", thisUser, timeA, requestA);

        Request requestB = Request.getOrCreate("2122");
        final Book bookB = Book.getOrCreate("l4dCtuESz53z7kWul7oD");
        requestB.setBook(bookB);
        final User userB = User.getOrCreate("HpYNt4gR6ZQ8MnSagr2rUKuO2i33");
        requestB.setRequester(userB);
        requestB.setStatus(RequestStatus.SENT);
        long timeB = System.currentTimeMillis() - 2000;
        requestB.setTimestamp(timeB);
        BookRequestedNotification bookRequestedNotificationB = new BookRequestedNotification("1", thisUser, timeB, requestB);

        Request requestC = Request.getOrCreate("2123");
        final Book bookC = Book.getOrCreate("WsIYkFc2gKJOuox7yj17");
        requestC.setBook(bookC);
        requestC.setRequester(thisUser);
        requestC.setStatus(RequestStatus.ACCEPTED);
        long timeC = System.currentTimeMillis() - 1000;
        requestC.setTimestamp(timeC);
        RequestAcknowledgedNotification requestAcknowledgedNotificationA = new RequestAcknowledgedNotification("2", thisUser, timeC, requestC);

        Request requestD = Request.getOrCreate("2124");
        final Book bookD = Book.getOrCreate("akRX8Yq0VpazrHFFLkNm");
        requestD.setBook(bookD);
        requestD.setRequester(thisUser);
        requestD.setStatus(RequestStatus.DECLINED);
        long timeD = System.currentTimeMillis();
        requestD.setTimestamp(timeD);
        RequestAcknowledgedNotification requestAcknowledgedNotificationB = new RequestAcknowledgedNotification("3", thisUser, timeD, requestD);

        notifications.add(requestAcknowledgedNotificationA);
        notifications.add(requestAcknowledgedNotificationB);
        notifications.add(bookRequestedNotificationB);
        notifications.add(bookRequestedNotificationA);
    }
}