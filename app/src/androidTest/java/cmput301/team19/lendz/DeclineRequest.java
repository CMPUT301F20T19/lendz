package cmput301.team19.lendz;

import android.widget.Toast;

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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)

public class DeclineRequest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("me@you.com"));

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * @throws Exception
     * Declines a book request
     */
    @Test
    public void declineBookRequest() throws Exception{
        Thread.sleep(1000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);


        onView(withText("Requested Books")).check(matches(isDisplayed()));
        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        checkingBookViews();

        onView(withId(R.id.view_requests_button))
                .perform(ViewActions.click());

        checkRequestControl();

        //decline the request
        onView(withId(R.id.declineRequest))
                .perform(ViewActions.click());
        Thread.sleep(2000);

        onView(withText("Decline Book Request")).check(matches(isDisplayed()));
        onView(withText("do you want to decline this request")).check(matches(isDisplayed()));
        onView(withText("do you want to decline this request")).check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withText("Yes")).perform(click());
        Thread.sleep(2000);

        Espresso.pressBack();
        Thread.sleep(2000);

    }

    /**
     * checking if views exist for the book being declined
     */
    public void checkingBookViews() {
        onView(withId(R.id.bookImage)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewAuthor)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewISBN)).check(matches(isDisplayed()));

    }

    /**
     * checking if the views and button for requesting actions exits
     */
    public void checkRequestControl(){
        onView(withId(R.id.n1)).check(matches(isDisplayed()));
        onView(withId(R.id.acceptRequest)).check(matches(isDisplayed()));
        onView(withId(R.id.declineRequest)).check(matches(isDisplayed()));
    }
}
