package cmput301.team19.lendz;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)

public class FailedLoginTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void signUp() throws Exception{
        // Start signed out
        FirebaseAuth.getInstance().signOut();
    }

    /**
     * signing in with an incorrect email
     */
    @Test
    public void incorrectEmail() throws Exception{
        onView(withId(R.id.editText_login_email))
                .perform(typeText("me@you"), closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(typeText("1234567"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(2000);
    }

    /**
     * signing in with an incorrect password
     */
    @Test
    public void incorrectPassword() throws Exception{
        onView(withId(R.id.editText_login_email))
                .perform(typeText("me@you.com"), closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(typeText("23467"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(2000);

    }

    /**
     *sign in with the correct details
     */
    @Test
    public void login() throws Exception{
        onView(withId(R.id.editText_login_email))
                .perform(typeText("me@you.com"), closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(typeText("1234567"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(2000);

    }
}
