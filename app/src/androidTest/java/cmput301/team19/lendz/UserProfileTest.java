package cmput301.team19.lendz;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class UserProfileTest {
    private static final String TEST_USER_EMAIL = "testuser@tests.com";
    private static final String TEST_USER_PASSWORD = "testpassword";
    private static final String TEST_USER_USERNAME = "testuser";
    private static final String TEST_USER_FULL_NAME = "Test User";
    private static final String TEST_USER_PHONE_NUMBER = "911";

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    /**
     * Logs in as the test user.
     */
    @Before
    public void login() throws Exception {
        onView(withId(R.id.editText_login_username))
                .perform(clearText())
                .perform(typeText(TEST_USER_EMAIL));
        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText(TEST_USER_PASSWORD), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * Checks if the displayed details in the ViewUserProfileFragment matches the expected values.
     */
    private void checkViewProfileDetails(String expectedUsername,
                                         String expectedFullName,
                                         String expectedEmail,
                                         String expectedPhoneNumber) {
        onView(withId(R.id.usernameTextView))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedUsername)));
        onView(withId(R.id.fullNameTextView))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedFullName)));
        onView(withId(R.id.emailTextView))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedEmail)));
        onView(withId(R.id.phoneNumberTextView))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedPhoneNumber)));
    }

    /**
     * Checks if the displayed details in the EditUserProfileFragment matches the expected values,
     * then replaces them with the new values.
     */
    private void checkAndReplaceEditProfileDetails(
            String expectedUsername, String newUsername,
            String expectedFullName, String newFullName,
            String expectedEmail, String newEmail,
            String expectedPhoneNumber, String newPhoneNumber) {
        onView(withId(R.id.usernameEditText))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedUsername)))
                .perform(clearText())
                .perform(typeText(newUsername));
        onView(withId(R.id.fullNameEditText))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedFullName)))
                .perform(clearText())
                .perform(typeText(newFullName));
        onView(withId(R.id.emailEditText))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedEmail)))
                .perform(clearText())
                .perform(typeText(newEmail));
        onView(withId(R.id.phoneNumberEditText))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedPhoneNumber)))
                .perform(clearText())
                .perform(typeText(newPhoneNumber));
    }

    /**
     * Check if pressing the Profile tab in the navigation of the MainActivity displays
     * the profile details of the test user.
     */
    @Test
    public void testShowsOwnProfile() throws Exception {
        onView(withId(R.id.profile))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        checkViewProfileDetails(
                TEST_USER_USERNAME,
                TEST_USER_FULL_NAME,
                TEST_USER_EMAIL,
                TEST_USER_PHONE_NUMBER);
    }

    /**
     * Check if editing the profile details of the test user works.
     */
    @Test
    public void testEditProfile() throws Exception {
        testShowsOwnProfile();
        onView(withId(R.id.edit_user_profile))
                .perform(click());
        Thread.sleep(2000);

        // Edit the profile with these values
        String editedUsername = "bestuser";
        String editedFullName = "Best User";
        String editedEmail = "bestuser@tests.com";
        String editedPhoneNumber = "119";

        // Test that the original values are reflected in the EditUserProfileFragment,
        // then replace them with the new values
        checkAndReplaceEditProfileDetails(
                TEST_USER_USERNAME, editedUsername,
                TEST_USER_FULL_NAME, editedFullName,
                TEST_USER_EMAIL, editedEmail,
                TEST_USER_PHONE_NUMBER, editedPhoneNumber);

        onView(withId(R.id.save_user_profile))
                .perform(click());
        Thread.sleep(2000);

        // Test that the edited values are reflected in the ViewUserProfileFragment
        checkViewProfileDetails(
                editedUsername,
                editedFullName,
                editedEmail,
                editedPhoneNumber);

        onView(withId(R.id.edit_user_profile))
                .perform(click());
        Thread.sleep(2000);

        // Test that the new values are reflected in the EditUserProfileFragment,
        // then replace them with the original values
        checkAndReplaceEditProfileDetails(
                editedUsername, TEST_USER_USERNAME,
                editedFullName, TEST_USER_FULL_NAME,
                editedEmail, TEST_USER_EMAIL,
                editedPhoneNumber, TEST_USER_PHONE_NUMBER);

        onView(withId(R.id.save_user_profile))
                .perform(click());
        Thread.sleep(2000);

        // Test that the original values are reflected in the ViewUserProfileFragment
        checkViewProfileDetails(
                TEST_USER_USERNAME,
                TEST_USER_FULL_NAME,
                TEST_USER_EMAIL,
                TEST_USER_PHONE_NUMBER);

    }
}
