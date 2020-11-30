package cmput301.team19.lendz;

import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.internal.util.Checks.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *Test class for LoginActivity. All the UI tests are written here. Robotium test framework is
 * used
 */
public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    @BeforeClass
    public static void signOut() {
        // Start signed out
        FirebaseAuth.getInstance().signOut();

    }

    /**
     *Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }


    /**
     * tests if user successfully Logs in and logs them out
     */
    @Test
    public void testLogin() throws InterruptedException {
        //​Asserts that the current activity is the LoginActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity",LoginActivity.class);

        //Get view for email EditText and enter email
        solo.enterText((EditText) solo.getView(R.id.editText_login_email),"frankwoods@gmail.com");
        //Get view for password EditText and enter password
        solo.enterText((EditText) solo.getView(R.id.editText_login_password),"1234567");

        /**True if there is a text: frankwoods@gmail.com on the screen, wait at least 2 seconds and find
         minimum one match. */
        assertTrue(solo.waitForText("frankwoods@gmail.com",1,2000));
        //Click on login button
        solo.clickOnButton("LOGIN");

        //​Asserts that the current activity is the MainActivity. Otherwise, show "Wrong Activity"
        solo.assertCurrentActivity("Wrong Activity",MainActivity.class);


    }


    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
