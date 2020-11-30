package cmput301.team19.lendz;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)

public class DeclineRequestTest {
    private final DocumentReference testBookRef =
            FirebaseFirestore.getInstance().document("books/declineRequestTestBook");

    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void initialize() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create the required book
        Map<String, Object> bookData = new HashMap<>();
        Map<String, Object> descriptionMap = new HashMap<>();
        descriptionMap.put("author", "Famous Author");
        descriptionMap.put("description", "This is a book that literally everyone wants.");
        descriptionMap.put("isbn", "9172631827461");
        descriptionMap.put("title", "Book That Everyone Wants");
        bookData.put("description", descriptionMap);
        List<String> keywordsArray = new ArrayList<>();
        keywordsArray.add("9172631827461");
        bookData.put("keywords", keywordsArray);
        bookData.put("owner", db.document("users/T4x10rJR4mOV4UhQFNeb1jneSsH2"));
        bookData.put("ownerUsername", "DeclineRequestTestOwner");
        bookData.put("photo", null);
        bookData.put("status", BookStatus.REQUESTED.ordinal());
        List<DocumentReference> pendingRequestsArray = new ArrayList<>();
        pendingRequestsArray.add(db.document("requests/declineRequestTestRequest"));
        bookData.put("pendingRequests", pendingRequestsArray);
        List<DocumentReference> pendingRequestersArray = new ArrayList<>();
        pendingRequestersArray.add(db.document("users/OryOs90dhaXBXo0cfNqqvkf7qn12"));
        bookData.put("pendingRequesters", pendingRequestersArray);
        testBookRef.set(bookData);

        // Create the required request
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("book", testBookRef);
        requestData.put("bookPhotoUrl", null);
        requestData.put("bookTitle", "Book That Everyone Wants");
        requestData.put("location", null);
        requestData.put("ownerUsername", "DeclineRequestTestOwner");
        requestData.put("requester", db.document("users/OryOs90dhaXBXo0cfNqqvkf7qn12"));
        requestData.put("requesterFullName", "DeclineRequestTest Requester");
        requestData.put("requesterUsername", "DeclineRequestTestRequester");
        requestData.put("status", RequestStatus.SENT.ordinal());
        requestData.put("timestamp", 1606765885890L);
        db.document("requests/declineRequestTestRequest")
                .set(requestData);

        // Log in
        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("owner@declinerequesttest.example.com"),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("password"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * @throws Exception
     * Declines a book request
     */
    @Test
    public void declineBookRequest() throws Exception{
        Thread.sleep(8000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withText("Requested Books")).check(matches(isDisplayed()));
        onView(withId(R.id.myBooksFrag_recyclerView)).
                perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        checkingBookViews();

        onView(withId(R.id.view_requests_button))
                .perform(ViewActions.click());

        //decline the request
        onData(anything()).inAdapterView(withId(R.id.requestListView))
                .onChildView(withId(R.id.declineRequest))
                .atPosition(0).perform(click());

        onView(withText("Decline Request")).check(matches(isDisplayed()));
        onView(withText("Do you want to decline this request?")).check(matches(isDisplayed()));
        Thread.sleep(2000);

        onView(withText("Yes")).perform(click());
        Thread.sleep(2000);

        Espresso.pressBack();
        Thread.sleep(2000);

    }

    /**
     * checking if views exist for the book being declined and if so do it
     */
    public void checkingBookViews() {
        onView(withId(R.id.bookImage)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewAuthor)).check(matches(isDisplayed()));
        onView(withId(R.id.bookViewISBN)).check(matches(isDisplayed()));

    }
}
