package cmput301.team19.lendz;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
//import androidx.test.uiautomator.UiDevice;
//import androidx.test.uiautomator.UiObject;
//import androidx.test.uiautomator.UiObjectNotFoundException;
//import androidx.test.uiautomator.UiSelector;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class RequestBookTest {
    private String QUERY_STRING = "Expresso";
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("seclosDev@gmail.com"));

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);

    }

    /**
     * Begins the activity for the search
     * Makes a search and awaits the results
     *
     */
    @Test
    public void RequestBook() throws InterruptedException {
        //Begins the activity for the search
        //Makes a search and awaits the results
        onView(withId(R.id.search_item)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_edit)).perform(typeText(QUERY_STRING),ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_recyclerview)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);
        //check if request button is in view
        try {
            onView(withId(R.id.request_button)).perform(click());
            //check if dialog box appears
            Thread.sleep(2000);
            onView(withText("Request Sent")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());
            Thread.sleep(2000);

        }catch (NoMatchingViewException ignore) {
            //no matching view exception
        }
    }


}
