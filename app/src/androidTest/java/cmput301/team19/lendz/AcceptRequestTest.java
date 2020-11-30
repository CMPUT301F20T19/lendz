package cmput301.team19.lendz;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)

public class AcceptRequestTest {
    private final String QUERY_STRING = "2536732323232";

    private final DocumentReference testBookRef = FirebaseFirestore.getInstance()
            .document("books/acceptRequestTestBook");

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void initialize() {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create the required book
        Map<String, Object> bookData = new HashMap<>();
        Map<String, Object> descriptionMap = new HashMap<>();
        descriptionMap.put("author", "boyonda");
        descriptionMap.put("description", "the witcher");
        descriptionMap.put("isbn", "2536732323232");
        descriptionMap.put("title", "Chelsea Book");
        bookData.put("description", descriptionMap);
        List<String> keywordsArray = new ArrayList<>();
        keywordsArray.add("2536732323232");
        bookData.put("keywords", keywordsArray);
        bookData.put("owner", db.document("users/dwpqY6Wnr4MTavg9pJkvfjFadJ73"));
        bookData.put("ownerUsername", "WoodieFrank101");
        bookData.put("photo", null);
        bookData.put("status", BookStatus.REQUESTED.ordinal());
        List<DocumentReference> pendingRequestsArray = new ArrayList<>();
        pendingRequestsArray.add(db.document("requests/acceptRequestTestRequest"));
        bookData.put("pendingRequests", pendingRequestsArray);
        List<DocumentReference> pendingRequestersArray = new ArrayList<>();
        pendingRequestersArray.add(db.document("users/dqdzdaUMthZxuyo43LLpeCfkvjb2"));
        bookData.put("pendingRequesters", pendingRequestersArray);

        testBookRef.set(bookData);

        // Create the required request
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("book", testBookRef);
        requestData.put("bookPhotoUrl", null);
        requestData.put("bookTitle", "Chelsea book");
        requestData.put("location", null);
        requestData.put("ownerUsername", "WoodieFrank101");
        requestData.put("requester", db.document("users/dqdzdaUMthZxuyo43LLpeCfkvjb2"));
        requestData.put("requesterFullName", "James Harden");
        requestData.put("requesterUsername", "jamesHarden");
        requestData.put("status", RequestStatus.SENT.ordinal());
        requestData.put("timestamp", 1606715798873L);
        db.document("requests/acceptRequestTestRequest")
                .set(requestData);

        // Log in
        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("frankwoods@gmail.com"),
                        ViewActions.closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"),
                        ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_button))
                .perform(click());
    }

    /**
     * Begins the activity for the search
     * Makes a search and awaits the results
     *
     */
    @Test
    public void acceptRequest() throws InterruptedException {
        Thread.sleep(8000);
        onView(withId(R.id.search_item)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_edit)).perform(typeText(QUERY_STRING),ViewActions.closeSoftKeyboard());
        Thread.sleep(2000);
        onView(withId(R.id.search_button)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.search_recyclerview)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);
        //check if view requests button is in view
        try {
            onView(withId(R.id.view_requests_button))
                    .check(matches(withText("VIEW REQUESTS")));
            onView(withId(R.id.view_requests_button)).perform(scrollTo(), click());
            //navigate to list view
            onData(anything()).inAdapterView(withId(R.id.requestListView)).onChildView(withId(R.id.acceptRequest)).atPosition(0).perform(click());
            Thread.sleep(2000);
            onView(withText("Accept Request")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());
            Thread.sleep(2000);
            //open map activity

            onView(withText("Pickup Location")).check(matches(isDisplayed()));
            Thread.sleep(2000);
            onView(withId(android.R.id.button1)).perform(click());

        }catch (NoMatchingViewException ignore) {
            //no matching view exception
        }


    }
}


