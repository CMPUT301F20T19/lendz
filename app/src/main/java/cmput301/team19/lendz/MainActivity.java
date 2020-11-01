package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "USER_ID" ;
    private BottomNavigationView bottomNavigationView;


    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserID();

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(tabSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new BorrowBookFragment()).commit();
    }

    private void getUserID() {
        Intent intent = getIntent();
        if(intent.hasExtra("userID")) {
          uid = (String) intent.getSerializableExtra("userID");
            Log.d(TAG, "getUserID: " + uid);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener tabSelected =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment fragment = null;
                    int itemID = item.getItemId();
                    if (itemID == R.id.borrow) {
                        fragment = new BorrowBookFragment();
                    }else if (itemID == R.id.my_books) {
                        fragment = new MyBooksFragment();
                    }else if (itemID == R.id.notifications) {
                        fragment = new NotificationsFragment();
                    } else if(itemID == R.id.profile) {
                        fragment = ViewUserProfileFragment.newInstance(uid);
                    }
                    if(fragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                    }
                    return true;
                }
            };
}