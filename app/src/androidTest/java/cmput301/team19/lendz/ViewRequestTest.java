package cmput301.team19.lendz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)

public class ViewRequestTest {
    private final String QUERY_STRING = "2536732323232";

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        // Call Firebase function to create the required book and request
        FirebaseFunctions.getInstance()
                .getHttpsCallable("viewRequestTestBefore")
                .call()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ViewRequestTest", "call to viewRequestTestBefore failed");
                        fail();
                    }
                });

        // Log in
        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("frankwoods@gmail.com"),
                        ViewActions.closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"),
                        ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
    }

    /**
     * Begins the activity for the search
     * Makes a search and awaits the results
     *
     */
    @Test
    public void acceptRequest() throws InterruptedException {
        Thread.sleep(16000);
        onView(withId(R.id.search_item)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_edit)).perform(typeText(QUERY_STRING),ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_recyclerview)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);
        //check if view requests button is in view
        try {
            onView(withId(R.id.view_requests_button))
                    .check(matches(withText("VIEW REQUESTS")));
            onView(withId(R.id.view_requests_button)).perform(scrollTo(), click());
            //navigate to list view
            onData(anything()).inAdapterView(withId(R.id.requestListView)).onChildView(withId(R.id.acceptRequest)).atPosition(0).perform(click());
            Thread.sleep(2000);
            onView(withText("Accept Request")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());
            Thread.sleep(2000);
            //open map activity

            onView(withText("Pickup Location")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());

        }catch (NoMatchingViewException ignore) {
            //no matching view exception
        }


    }

    /**
     * Closes the activity and delete the created user after each test
     */
    @After
    public void tearDown() {
        // Call Firebase function to delete the book
        FirebaseFunctions.getInstance()
                .getHttpsCallable("viewRequestTestAfter")
                .call(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ViewRequestTest", "call to viewRequestTestAfter failed");
                    }
                });
    }
}


