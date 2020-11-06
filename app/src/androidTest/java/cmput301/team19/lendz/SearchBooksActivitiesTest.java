package cmput301.team19.lendz;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchBooksActivitiesTest extends TestCase {

    private String QUERY_STRING = "funny";

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule
            = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Makes sure the current activity is MainActivity
     * and the current tab is the borrow book fragment before
     * any test are run
     * @throws Exception
     */
    @Before
    public void startSearchActivity() throws Exception {
        onView(withId(R.id.bottomNav));
        onView(withId(R.id.borrow)).check(matches(isDisplayed()));
    }

    /**
     * Begins the activity for the search
     * Makes a search and awaits the results
     */
    @Test
    public void beginSearch() {
        onView(withId(R.id.search_item)).perform(click());
        onView(withId(R.id.search_edit)).perform(typeText(QUERY_STRING),ViewActions.closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
    }

}