package cmput301.team19.lendz;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
//import androidx.test.espresso.intent.Intents;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReturnBookTest {

    //    private String QUERY_STRING = "eee";
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void logUserIn() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("hh@gmail.com"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("123456"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);

    }

    /**
     * tests the return book functionality.
     * an activity result is manually created to mock the actual isbn code being scanned.
     * handles exceptions for non matching book ids
     */
    @Test
    public void BorrowerConfirmReturn() throws InterruptedException {
        Thread.sleep(2000);
        onView(withId(R.id.borrowFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);
        //create a result you want to be returned from the scanner
        Intents.init();
        String qr ="9780141439518";
        Intent resultData = new Intent();
        resultData.putExtra(com.google.zxing.client.android.Intents.Scan.RESULT, qr);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);


        //Espresso responds with the ActivityResult we created
        intending(hasAction("com.google.zxing.client.android.SCAN")).respondWith(result);
        try{

            //check if confirm button is clickable
            onView(withId(R.id.confirm_pick_up_or_return_button)).check(matches(isClickable()));
            //launch the scanner
            onView(withId(R.id.confirm_pick_up_or_return_button)).perform(scrollTo(),click());

        }catch (NoMatchingViewException ignore){

            //this portion is called if the book isbn do not correspond with that of the owners book.
            intended(hasAction("com.google.zxing.client.android.SCAN"));
            onView(withText("The book you scanned has a non-matching ISBN of "+ qr)).check(matches(isDisplayed()));
        }
        Intents.release();
    }

}

