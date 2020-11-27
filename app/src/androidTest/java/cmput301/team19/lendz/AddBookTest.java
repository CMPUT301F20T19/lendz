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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AddBookTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
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

    /**
     * Tests the sequential order in successfully navigating to the addBook activity.
     * @throws Exception
     */
    @Test
    public void  Navigate_To_Add_Book_Activity() throws Exception {
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        //CHECK IF FLOATING BUTTON EXIST
        onView(withId(R.id.add_book_button)).check(matches(isDisplayed()));

        onView(withId(R.id.add_book_button))
                .perform(ViewActions.click());

        check_if_addViews_exist();
        Fill_Book_details();


    }

    /**
     * Tests that all ui components in the add book activity are present.
     * @throws Exception
     */

    public void check_if_addViews_exist(){
        onView(withId(R.id.photo_imageview)).check(matches(isDisplayed()));
        onView(withId(R.id.title_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.isbn_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.scan_button)).check(matches(isDisplayed()));
        onView(withId(R.id.author_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.description_edittext)).check(matches(isDisplayed()));
        onView(withId(R.id.tap_to_add_photo_textview)).check(matches(isDisplayed()));
        onView(withId(R.id.save_book_details)).check(matches(isDisplayed()));
        onView(withId(R.id.delete_photo_imagebutton)).check(matches(isDisplayed()));
    }



    /**
     * Tests that a book object was successfully sent to firebase.
     * @throws Exception
     */
    public void Fill_Book_details(){
        onView(withId(R.id.title_edittext))
                .perform(clearText())
                .perform(typeText("Expresso Book Title"));
        onView(withId(R.id.isbn_edittext))
                .perform(clearText())
                .perform(typeText("Expresso isbn"));
        onView(withId(R.id.author_edittext))
                .perform(clearText())
                .perform(typeText("Expresso Author"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(clearText())
                .perform(typeText("Expresso Description"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());

    }
}
