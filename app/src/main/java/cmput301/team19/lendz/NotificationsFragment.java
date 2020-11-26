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
import android.widget.TextView;
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
 * A Fragment that displays the user's notifications.
 */
public class NotificationsFragment extends Fragment {
    private View view;
    private NotificationAdapter adapter;

    public NotificationsFragment() {

    }

    /**
     * Create a new instance of this fragment.
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        final ArrayList<Notification> notifications = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        adapter = new NotificationAdapter(getContext(), notifications);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        String currentUserId = User.getCurrentUser().getId();
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

                        onNotificationsChange();
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

    private void onNotificationsChange() {
        adapter.notifyDataSetChanged();
        TextView noNotificationsTextView = view.findViewById(R.id.no_notifications);
        noNotificationsTextView.setVisibility(
                adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}