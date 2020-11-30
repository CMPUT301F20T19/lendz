package cmput301.team19.lendz;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.RecursiveAction;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MyBooksTest extends TestCase {
    private static final String TEST_USER_EMAIL = "mybookstest@gmail.com";
    private static final String TEST_USER_PASSWORD = "123456";

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Signs out, logs into test user's account and
     * goes to my books tab for testing.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // sign out before logging in
        FirebaseAuth.getInstance().signOut();

        // log into test user's account
        onView(withId(R.id.editText_login_email))
                .perform(clearText(), replaceText(TEST_USER_EMAIL),
                        ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editText_login_password))
                .perform(clearText(), replaceText(TEST_USER_PASSWORD),
                        ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);

        // go to my books tab and wait for books to load
        onView(withId(R.id.my_books))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * Tests if section header is shown and
     * if section header shows correct test.
     */
    @Test
    public void testSectionHeader() {
        // test if section headers are shown and have correct texts
        onView(withId(R.id.myBooksFrag_recyclerView))
                .check(matches(hasDescendant(withText("Available Books"))));

        onView(withId(R.id.myBooksFrag_recyclerView))
                .check(matches(hasDescendant(withText("Requested Books"))));

        onView(withId(R.id.myBooksFrag_recyclerView))
                .perform(swipeUp())
                .check(matches(hasDescendant(withText("Accepted Books"))));
    }

    /**
     * Test book data being shown.
     * @param section_header name of the section the book is under
     * @param position position of the book in the list being shown
     * @param title expected title of the book being shown
     * @param author expected author of the book being shown
     */
    public void testBookData(String section_header, int position, String title, String author) {
        // test book title
        onView(allOf(isDescendantOfA(withId(R.id.myBooksFrag_recyclerView)),
                withParent(withParent(withParent(withChild(withText(section_header))))),
                isDescendantOfA(withId(R.id.section_body)),
                withParent(withParentIndex(position)),
                withId(R.id.book_title)))
                .check(matches(withText(title)));

        // test book author
        onView(allOf(isDescendantOfA(withId(R.id.myBooksFrag_recyclerView)),
                withParent(withParent(withParent(withChild(withText(section_header))))),
                isDescendantOfA(withId(R.id.section_body)),
                withParent(withParentIndex(position)),
                withId(R.id.book_author)))
                .check(matches(withText(author)));
    }

    /**
     * Tests if section body is shown and
     * if book data being shown is correct.
     */
    @Test
    public void testSectionBody() {
        // test book data
        testBookData("Available Books", 0, "Pride and Prejudice", "Jane Austen");
        testBookData("Available Books",1, "War and Peace", "Leo Tolstoy");
        testBookData("Requested Books",0, "Cosmos", "Carl Sagan");

        onView(withId(R.id.myBooksFrag_recyclerView))
                .perform(swipeUp());

        testBookData("Accepted Books",0, "The Republic", "Plato");
    }
}
