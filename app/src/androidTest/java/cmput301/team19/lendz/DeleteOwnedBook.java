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
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)

public class DeleteOwnedBook {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("deleting@g.com"),closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * @throws Exception
     * simple deletion of a book
     */
    @Test
    public void deleteOwnedBook() throws Exception{
        Thread.sleep(1000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(2000);
        onView(withText("Delete"))
                .perform(click());
        Thread.sleep(2000);

    }
}
