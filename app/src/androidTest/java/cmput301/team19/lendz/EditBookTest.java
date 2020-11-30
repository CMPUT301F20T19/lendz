package cmput301.team19.lendz;

import androidx.test.espresso.Espresso;
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

@RunWith(AndroidJUnit4.class)
public class EditBookTest {
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
     * @throws Exception Switches the view to editing a book details
     */
    @Test
    public void editBook() throws Exception {
        Thread.sleep(2000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        // checks if the correct views for the book exist
        check_if_ViewBook();

        onView(withId(R.id.editBookDetails))
                .perform(ViewActions.click());

        editBookDetails("One Punch Man",
                "The life of one punch is " +
                        "simple, he just punch one and can" +
                        "literally end the world",
                "Students",
                "8008");


        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);

        Espresso.pressBack();
        Thread.sleep(2000);
    }


    /**
     * Checking to see if the correct details exits
     */
    public void check_if_ViewBook() {
        onView(withId(R.id.bookImage)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewAuthor)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewISBN)).check(matches(isDisplayed()));

    }


    /**
     * Checks if the displayed details in the AddBookActivity matches the expected values,
     * then replaces them with the new values.
     */
    private void editBookDetails(String newBookTitle,
            String newBookDesc,
            String newBookAuthor,
            String newBookISBN) {
        onView(withId(R.id.title_edittext))
                .perform(clearText())
                .perform(typeText(newBookTitle));
        onView(withId(R.id.isbn_edittext))
                .perform(clearText())
                .perform(typeText(newBookISBN));
        onView(withId(R.id.author_edittext))
                .perform(clearText())
                .perform(typeText(newBookAuthor), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(clearText())
                .perform(typeText(newBookDesc), ViewActions.closeSoftKeyboard());
    }




}
