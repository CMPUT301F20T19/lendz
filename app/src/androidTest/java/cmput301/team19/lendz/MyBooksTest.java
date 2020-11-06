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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MyBooksTest {
    private static final String TEST_USER_EMAIL = "seclosDev@gmail.com";
    private static final String TEST_USER_PASSWORD = "123456";

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() throws Exception {
        // sign out before logging in
        FirebaseAuth.getInstance().signOut();

        // log into test user's account
        onView(withId(R.id.editText_login_email))
                .perform(clearText(), typeText(TEST_USER_EMAIL));
        onView(withId(R.id.editText_login_password))
                .perform(clearText(), typeText(TEST_USER_PASSWORD),
                        ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(5000);

        // go to my books tab and wait for books to load
        onView(withId(R.id.my_books))
                .perform(click());
        Thread.sleep(5000);
    }

    @Test
    public void testSectionHeader() {
        onView(withId(R.id.myBooksFrag_recyclerView))
                .check(matches(withText("Available Books")))
                .check(matches(withText("Borrowed Books")));
    }
}
