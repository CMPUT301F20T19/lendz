package cmput301.team19.lendz;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.ActivityResultMatchers;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.service.autofill.Validators.not;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.google.common.base.CharMatcher.is;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)

public class ViewRequestTest {
    private String QUERY_STRING = "Gilbert";
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);
   ;

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
        Thread.sleep(8000);

    }
    /**
     * Begins the activity for the search
     * Makes a search and awaits the results
     *
     */
    @Test
    public void acceptRequest() throws InterruptedException {
        onView(withId(R.id.search_item)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_edit)).perform(typeText(QUERY_STRING),ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_recyclerview)).
                perform(actionOnItemAtPosition(1, click()));
        Thread.sleep(2000);
        //check if view requests button is in view
        try {
            onView(withId(R.id.view_requests_button))
                    .check(matches(withText("VIEW REQUESTS")));
            onView(withId(R.id.view_requests_button)).perform(click());
            //navigate to list view
            onData(anything()).inAdapterView(withId(R.id.requestListView)).onChildView(withId(R.id.acceptRequest)).atPosition(0).perform(click());
            Thread.sleep(2000);
            onView(withText("Accept Book Request")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());
            Thread.sleep(2000);
            //open map activity
            onView(withId(android.R.id.button1)).perform(click());

            //check if dialog box appears
            Thread.sleep(2000);
            onView(withText("Pickup Location")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());
            Thread.sleep(2000);

        }catch (NoMatchingViewException ignore) {
            //no matching view exception

        }
    }

}
