package cmput301.team19.lendz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.UUID;

/**
 * Fragment for viewing user profile information.
 * Used in UserProfileActivity.
 */
public class ViewUserProfileFragment extends Fragment {
    // Parameter names
    private static final String ARG_USER_ID = "userId";

    private User user;

    private TextView usernameTextView, fullNameTextView, emailTextView, phoneNumberTextView;

    public ViewUserProfileFragment() {
        // Required empty public constructor
    }

    public static ViewUserProfileFragment newInstance(UUID userId) {
        ViewUserProfileFragment fragment = new ViewUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Sets the displayed info to match that of the user object.
     */
    private void updateUserInfo() {
        if (user != null) {
            usernameTextView.setText(user.getUsername());
            fullNameTextView.setText(user.getFullName());
            emailTextView.setText(user.getEmail());
            phoneNumberTextView.setText(user.getPhoneNumber());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_user_profile, container, false);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        fullNameTextView = view.findViewById(R.id.fullNameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);

        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        final UUID userId = UUID.fromString(getArguments().getString(ARG_USER_ID));
        user = User.getOrCreate(userId);

        User.documentOf(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("UserProfileActivity",
                            "error getting user " + userId.toString() + ": " + error);
                } else if (value == null || !value.exists()) {
                    Log.w("UserProfileActivity",
                            "didn't find user " + userId.toString());
                } else {
                    user.load(value);
                    updateUserInfo();
                }
            }
        });
        updateUserInfo();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_user_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_user_profile:
                startEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Switch to the EditUserProfileFragment to edit the user details.
     */
    private void startEdit() {
        Fragment editUserProfileFragment = EditUserProfileFragment.newInstance(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
        );

        transaction.replace(R.id.fragment_container, editUserProfileFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}