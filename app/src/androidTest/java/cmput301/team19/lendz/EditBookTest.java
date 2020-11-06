package cmput301.team19.lendz;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;

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

    /**
     * Makes sure that every test begins with an up-to date sign in .
     * @throws Exception
     */
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
        Thread.sleep(3000);

    }

    /**
     * Tests the sequencial order in successfully navigating to the addbook activity.
     * @throws Exception
     */
    @Test
    public void  Nagivate_To_Add_Book_Activity() throws Exception {
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        //CHECK IF FLOATING BUTTON EXIST
    onView(withId(R.id.add_book_button)).check(matches(isDisplayed()));

    onView(withId(R.id.add_book_button))
            .perform(ViewActions.click());

    check_if_editViews_exist();
    Fill_Book_details();


    }

//    @Test
//    public void editBook() throws Exception{
//        onView(withId(R.id.my_books))
//                .perform(ViewActions.click());
//
//        Thread.sleep(2000);
//
//        onView(withId(R.id.myBooksFrag_recyclerView))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//
//    }




    /**
     * Tests that all ui components in the add book activity are present.
     * @throws Exception
     */

    public void check_if_editViews_exist(){
        onView(withId(R.id.book_IV)).check(matches(isDisplayed()));
        onView(withId(R.id.title_id)).check(matches(isDisplayed()));
        onView(withId(R.id.ISBN_ID)).check(matches(isDisplayed()));
        onView(withId(R.id.scanBTN)).check(matches(isDisplayed()));
        onView(withId(R.id.author_id)).check(matches(isDisplayed()));
        onView(withId(R.id.textView5)).check(matches(isDisplayed()));
        onView(withId(R.id.description_id)).check(matches(isDisplayed()));
        onView(withId(R.id.addImg)).check(matches(isDisplayed()));
        onView(withId(R.id.save_id)).check(matches(isDisplayed()));
        onView(withId(R.id.delImg)).check(matches(isDisplayed()));
    }

    /**
     * Tests that a book object was successfully sent to firebase.
     * @throws Exception
     */
    public void Fill_Book_details(){
        onView(withId(R.id.title_id))
                .perform(clearText())
                .perform(typeText("Expresso Book Title"));
        onView(withId(R.id.ISBN_ID))
                .perform(clearText())
                .perform(typeText("Expresso isbn"));
        onView(withId(R.id.author_id))
                .perform(clearText())
                .perform(typeText("Expresso Author"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.description_id))
                .perform(clearText())
                .perform(typeText("Expresso Description"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.save_id))
                .perform(ViewActions.click());

    }




}
