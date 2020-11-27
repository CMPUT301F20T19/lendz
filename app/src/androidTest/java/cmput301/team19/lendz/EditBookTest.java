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
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(10000);

        // checks if the correct views for the book exist
        check_if_ViewBook();

        // Checking if the strings here match the book details displayed
        checkBookDetails("Sun",
                "hello there",
                "Me",
                "45");
        onView(withId(R.id.editBookDetails))
                .perform(ViewActions.click());

        // Test that the new values are reflected in the AddBookActivity,
        // then replace them with the original values
        checkAndReplaceBookDetails("Sun", "Moon",
                "hello there", "Hi there",
                "Me", "You",
                "45", "54");

        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);

        Espresso.pressBack();
        Thread.sleep(5000);

        onView(withId(R.id.my_books))
                .perform(ViewActions.click());
        Thread.sleep(8500);

        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(8500);


        // checks if the correct views for the book exist
        check_if_ViewBook();

        onView(withId(R.id.editBookDetails))
                .perform(ViewActions.click());

        //Setting old book details back
        setOldDetailsBack("Sun",
                "hello there",
                "Me",
                "45");
        onView(withId(R.id.save_book_details))
                .perform(ViewActions.click());
        Thread.sleep(2000);

        Espresso.pressBack();
        Thread.sleep(2000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());
        Thread.sleep(3000);


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
     * Checking if the strings here match the book details displayed
     */
    private void checkBookDetails(String bookTitle,
                                  String bookDescription,
                                  String bookAuthor,
                                  String bookISBN) {
        onView(withId(R.id.bookViewTitle))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookTitle)));
        onView(withId(R.id.bookViewDescription))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookDescription)));
        onView(withId(R.id.bookViewAuthor))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookAuthor)));
        onView(withId(R.id.bookViewISBN))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookISBN)));
    }

    /**
     * Checks if the displayed details in the AddBookActivity matches the expected values,
     * then replaces them with the new values.
     */
    private void checkAndReplaceBookDetails(
            String bookTitle, String newBookTitle,
            String bookDescription, String newBookDesc,
            String bookAuthor, String newBookAuthor,
            String bookISBN, String newBookISBN) {
        onView(withId(R.id.title_edittext))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookTitle)))
                .perform(clearText())
                .perform(typeText(newBookTitle));
        onView(withId(R.id.isbn_edittext))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookISBN)))
                .perform(clearText())
                .perform(typeText(newBookISBN));
        onView(withId(R.id.author_edittext))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookAuthor)))
                .perform(clearText())
                .perform(typeText(newBookAuthor), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .check(ViewAssertions.matches(ViewMatchers.withText(bookDescription)))
                .perform(clearText())
                .perform(typeText(newBookDesc), ViewActions.closeSoftKeyboard());
    }

    private void setOldDetailsBack(String oldBookTitle,
                                   String oldDesc,
                                   String oldAuthor,
                                   String oldISBN) {
        onView(withId(R.id.title_edittext))
                .perform(clearText())
                .perform(typeText(oldBookTitle));
        onView(withId(R.id.isbn_edittext))
                .perform(clearText())
                .perform(typeText(oldISBN));
        onView(withId(R.id.author_edittext))
                .perform(clearText())
                .perform(typeText(oldAuthor), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.description_edittext))
                .perform(clearText())
                .perform(typeText(oldDesc), ViewActions.closeSoftKeyboard());

    }


}
