package cmput301.team19.lendz;

import android.text.SpannableString;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static org.junit.Assert.assertEquals;

/**
 * Test class for SignUp Activity. All the UI tests are written here. Robotium test framework is
 * used
 */
public class SignUpActivityTest {

    private Solo solo;

    private FirebaseUser createdUser;
    private boolean finishedDeletingUser;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @BeforeClass
    public static void signOut() {
        // Start signed out
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * test for signing up
     */
    @Test
    public void testSignup()
    {
        //​Asserts that the current activity is the LoginActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity",LoginActivity.class);

        //Click on signUp text
        solo.clickOnText("SignUp");

        //Get view for username and enter username
        solo.enterText((EditText) solo.getView(R.id.editText_signup_username),"Naruto");

        //Get view for Full name and enter username
        solo.enterText((EditText) solo.getView(R.id.editText_signup_full_name),"Naruto Uzumaki");

        //Get view for password and enter password
        solo.enterText((EditText) solo.getView(R.id.editText_signup_password),"123456");

        //Get view for phoneNumber and enter phoneNumber
        solo.enterText((EditText) solo.getView(R.id.editText_signup_phone_number),"1234567890");

        //Get view for email and enter email
        solo.enterText((EditText) solo.getView(R.id.editText_signup_email),"Naruto@gmail.com");

        //Click on SignUp
        solo.clickOnButton("Sign Up");

        //​Asserts that the current activity is the SignUp. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);

        //Get the bottom navigation bar
        final BottomNavigationView navigationBar = (BottomNavigationView) solo.getView(R.id.bottomNav);
        //Go to user profile
        solo.clickOnView(navigationBar.findViewById(R.id.profile));

        // Get the created user
        createdUser = FirebaseAuth.getInstance().getCurrentUser();

        //Click on logOut
        solo.clickOnButton("Log Out");

        //​Asserts that the current activity is the LoginActivity Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity",LoginActivity.class);
    }

    /**
     * Closes the activity and delete the created user after each test
     */
    @After
    public void tearDown() {
        // Delete the created user
        if (createdUser != null) {
            createdUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finishedDeletingUser = true;
                }
            });
        }

        solo.waitForCondition(new Condition() {
            @Override
            public boolean isSatisfied() {
                return finishedDeletingUser;
            }
        }, 5000);

        solo.finishOpenedActivities();
    }
}
