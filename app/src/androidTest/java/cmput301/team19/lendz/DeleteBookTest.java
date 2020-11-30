package cmput301.team19.lendz;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)

public class DeleteBookTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> rule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void initialize() throws Exception {
        // Ensure started logged out
        FirebaseAuth.getInstance().signOut();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference testBookRef = db.document("books/deleteBookTestBook");
        DocumentReference ownerRef = db.document("users/viBo18AsSuabcOL6Q3fmaIJE3Ml1");

        // Create the required book
        Map<String, Object> bookData = new HashMap<>();
        Map<String, Object> descriptionMap = new HashMap<>();
        descriptionMap.put("author", "Horrendous Author");
        descriptionMap.put("description", "This book deserves to be deleted");
        descriptionMap.put("isbn", "9991117772223");
        descriptionMap.put("title", "Delete this book");
        bookData.put("description", descriptionMap);
        List<String> keywordsArray = new ArrayList<>();
        keywordsArray.add("9991117772223");
        bookData.put("keywords", keywordsArray);
        bookData.put("owner", ownerRef);
        bookData.put("ownerUsername", "deleting");
        bookData.put("photo", null);
        bookData.put("status", BookStatus.AVAILABLE.ordinal());
        testBookRef.set(bookData);

        // Ensure book is present as owned book of user
        Map<String, Object> ownerUpdate = new HashMap<>();
        List<DocumentReference> ownedBooksArray = new ArrayList<>();
        ownedBooksArray.add(testBookRef);
        ownerUpdate.put("ownedBooks", ownedBooksArray);
        ownerRef.update(ownerUpdate);

        // Log in
        onView(withId(R.id.editText_login_email))
                .perform(clearText())
                .perform(typeText("deleting@g.com"),closeSoftKeyboard());

        onView(withId(R.id.editText_login_password))
                .perform(clearText())
                .perform(typeText("1234567"), ViewActions.closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());
        Thread.sleep(3000);
    }

    /**
     * @throws Exception
     * simple deletion of a book
     */
    @Test
    public void deleteOwnedBook() throws Exception{
        Thread.sleep(8000);
        onView(withId(R.id.my_books))
                .perform(ViewActions.click());

        Thread.sleep(2000);

        onView(withId(R.id.myBooksFrag_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Thread.sleep(2000);
        onView(withText("Delete"))
                .perform(click());
        Thread.sleep(2000);
    }
}
