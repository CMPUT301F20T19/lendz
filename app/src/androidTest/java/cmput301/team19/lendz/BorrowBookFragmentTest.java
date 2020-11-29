package cmput301.team19.lendz;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class BorrowBookFragmentTest {


    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Makes sure that every test begins with an up-to date sign in
     * any test are run
     * @throws Exception
     */
    @Test
    public void startSearchActivity() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("who@you.com"));

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);

    }

}
