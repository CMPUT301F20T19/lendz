package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.auth.User;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(tabSelected);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new BorrowBookFragment()).commit();
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
                        fragment = new ViewUserProfileFragment();
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                    return true;
                }
            };
}