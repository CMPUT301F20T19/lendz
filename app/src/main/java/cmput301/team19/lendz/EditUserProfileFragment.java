package cmput301.team19.lendz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.UUID;

/**
 * Fragment for editing user profile information.
 * Used in UserProfileActivity.
 */
public class EditUserProfileFragment extends Fragment {
    // Parameter names
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_USERNAME = "username";
    private static final String ARG_FULL_NAME = "fullName";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PHONE_NUMBER = "phoneNumber";

    private String userId;

    private EditText usernameEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;


    public EditUserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Create and return a new instance of EditUserProfileFragment with the given arguments.
     */
    public static EditUserProfileFragment newInstance(
            String id, String username, String fullName, String email, String phoneNumber) {
        EditUserProfileFragment fragment = new EditUserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, id);
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_FULL_NAME, fullName);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PHONE_NUMBER, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        fullNameEditText = view.findViewById(R.id.fullNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);

        if (getArguments() != null) {
            // Set the contents of editing controls based on the argument values
            userId = getArguments().getString(ARG_USER_ID);
            String username = getArguments().getString(ARG_USERNAME);
            String fullName = getArguments().getString(ARG_FULL_NAME);
            String email = getArguments().getString(ARG_EMAIL);
            String phoneNumber = getArguments().getString(ARG_PHONE_NUMBER);

            usernameEditText.setText(username);
            fullNameEditText.setText(fullName);
            emailEditText.setText(email);
            phoneNumberEditText.setText(phoneNumber);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_user_profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_user_profile:
                saveProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save the edited profile details to Firestore.
     */
    private void saveProfile() {
        User user = User.getOrCreate(userId);

        // Set User data based on contents of editing controls
        user.setUsername(usernameEditText.getText().toString());
        user.setFullName(fullNameEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());
        user.setPhoneNumber(phoneNumberEditText.getText().toString());

        // Save changes to Firestore
        user.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getParentFragmentManager().popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),
                                R.string.user_profile_edit_failed,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                 });
    }
}