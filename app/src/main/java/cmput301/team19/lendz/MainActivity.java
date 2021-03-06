package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * this activity contains the bottom navigation of the app
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start Firebase Cloud Messaging service
        Intent fcmServiceIntent = new Intent(this, FirebaseMessagingServiceImpl.class);
        startService(fcmServiceIntent);

        // If not logged in, go to LoginActivity
        if (User.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(tabSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, BorrowBookFragment.newInstance()).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateBackButton();
            }
        });
        updateBackButton();

        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null && intent.getAction().equals("view_book")) {
            ViewBookFragment viewBookFragment = ViewBookFragment.newInstance(intent.getStringExtra("bookId"));
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, viewBookFragment).commit();
        }
    }

    private void updateBackButton() {
        boolean enabled = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener tabSelected =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    int itemID = item.getItemId();
                    if (itemID == R.id.borrow) {
                        fragment = BorrowBookFragment.newInstance();
                    } else if (itemID == R.id.my_books) {
                        fragment = MyBooksFragment.newInstance();
                    } else if (itemID == R.id.notifications) {
                        fragment = NotificationsFragment.newInstance();
                    } else if (itemID == R.id.profile) {
                        fragment = ViewUserProfileFragment.newInstance(User.getCurrentUser().getId());
                    }
                    if (fragment != null) {
                        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    }
                    return true;
                }
            };
}

