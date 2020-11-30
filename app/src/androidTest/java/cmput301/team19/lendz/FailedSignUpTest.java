package cmput301.team19.lendz;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)

public class FailedSignUpTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void signUp() throws Exception{
        Thread.sleep(2000);
        onView(withId(R.id.login_signUp_message))
                .perform(ViewActions.click());
        Thread.sleep(2000);
    }
    @Test
    public void fakeSignUpemail() throws Exception {
        onView(withId(R.id.editText_signup_username))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_full_name))
                .perform(typeText("Mugi Wara"));
        onView(withId(R.id.editText_signup_password))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_phone_number))
                .perform(typeText("78012345469"));
        onView(withId(R.id.editText_signup_email))
                .perform(typeText("MugiWara"), closeSoftKeyboard());

        onView(withId(R.id.signUp_button))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void checkNumber() throws Exception{
        onView(withId(R.id.editText_signup_username))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_full_name))
                .perform(typeText("Mugi Wara"));
        onView(withId(R.id.editText_signup_password))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_email))
                .perform(typeText("MugiWara@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.signUp_button))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void checkPassword() throws Exception{
        onView(withId(R.id.editText_signup_username))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_full_name))
                .perform(typeText("Mugi Wara"));
        onView(withId(R.id.editText_signup_password))
                .perform(typeText("Mug"));
        onView(withId(R.id.editText_signup_phone_number))
                .perform(typeText("78012345469"));
        onView(withId(R.id.editText_signup_email))
                .perform(typeText("MugiWara@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.signUp_button))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void checkFullName() throws Exception{
        onView(withId(R.id.editText_signup_username))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_password))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_phone_number))
                .perform(typeText("78012345469"));
        onView(withId(R.id.editText_signup_email))
                .perform(typeText("MugiWara@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.signUp_button))
                .perform(click());
        Thread.sleep(2000);
    }

    @Test
    public void checkUsername() throws Exception{
        onView(withId(R.id.editText_signup_full_name))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_password))
                .perform(typeText("MugiWara"));
        onView(withId(R.id.editText_signup_phone_number))
                .perform(typeText("78012345469"));
        onView(withId(R.id.editText_signup_email))
                .perform(typeText("MugiWara@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.signUp_button))
                .perform(click());
        Thread.sleep(2000);
    }
}
