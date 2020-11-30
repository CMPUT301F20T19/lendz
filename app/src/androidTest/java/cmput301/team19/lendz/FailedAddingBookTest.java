package cmput301.team19.lendz;
import androidx.test.espresso.action.ViewActions;
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
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)

public class FailedAddingBookTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("deleting@g.com"), closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());

        Thread.sleep(3000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());
        Thread.sleep(2000);

        onView(withId(R.id.add_book_button)).check(matches(isDisplayed()));
        onView(withId(R.id.add_book_button))
                .perform(click());
        Thread.sleep(2000);

    }

    /**
     * checks that book title field is empty
     */
    @Test
    public void checkBookTitle() throws Exception{
        onView(withId(R.id.isbn_edittext))
                .perform(typeText("ISBN"), closeSoftKeyboard());
        onView(withId(R.id.author_edittext))
                .perform(typeText("Author"), closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(typeText("Description"), closeSoftKeyboard());
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);
    }

    /**
     * checks that book ISBN field is empty
     */
    @Test
    public void checkBookISBN() throws Exception{
        onView(withId(R.id.title_edittext))
                .perform(typeText("Book Title"), closeSoftKeyboard());
        onView(withId(R.id.author_edittext))
                .perform(typeText("Author"), closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(typeText("Description"), closeSoftKeyboard());
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);
    }

    /**
     * checks that book author field is empty
     */
    @Test
    public void checkBookAuthor() throws Exception{
        onView(withId(R.id.title_edittext))
                .perform(typeText("Book Title"), closeSoftKeyboard());
        onView(withId(R.id.isbn_edittext))
                .perform(typeText("ISBN"), closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(typeText("Description"), closeSoftKeyboard());
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);
    }

    /**
     * checks that book description field is empty
     */
    @Test
    public void checkBookDescription() throws Exception{
        onView(withId(R.id.title_edittext))
                .perform(typeText("Book Title"), closeSoftKeyboard());
        onView(withId(R.id.isbn_edittext))
                .perform(typeText("ISBN"), closeSoftKeyboard());
        onView(withId(R.id.author_edittext))
                .perform(typeText("Author"), closeSoftKeyboard());
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);
    }
}
