package cmput301.team19.lendz;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class ViewBooksActivity extends AppCompatActivity {
    private static final String TAG = "ViewBooksActivity";

    ArrayList<Book> availableBooks = new ArrayList<>();
    ArrayList<Book> requestedBooks = new ArrayList<>();
    ArrayList<Book> acceptedBooks = new ArrayList<>();
    ArrayList<Book> borrowedBooks = new ArrayList<>();

    private void loadBooks() {
        // String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // User currentUser = User.getOrCreate(currentUserID);

        final String currentUserID = "gBDk9Ex6KTUcjIgP9LNBLIlJ6h72";
        final User currentUser = User.getOrCreate(currentUserID);

        User.documentOf(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("ViewBooksActivity",
                            "error getting user " + currentUserID + ": " + error);
                } else if (value == null || !value.exists()) {
                    Log.w("ViewBooksActivity",
                            "didn't find user " + currentUserID);
                } else {
                    currentUser.load(value);
                }
            }
        });
/*
        ArrayList<UUID> bookIDs = new ArrayList<>();
        bookIDs.addAll(currentUser.getOwnedBookIds());
        bookIDs.addAll(currentUser.getBorrowedBookIds());

        // declaring variables for using inside for loop
        BookStatus bookStatus;
        RequestStatus bookRequestStatus;

        for (int i = 0; i < bookIDs.size(); i++) {
            final UUID bookID = bookIDs.get(i);
            final Book book = Book.getOrCreate(bookID);

            Book.documentOf(bookID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG,
                                "error getting book with ID " + bookID.toString() + ": " + error);
                    } else if (value == null || !value.exists()) {
                        Log.w(TAG,
                                "didn't find book with ID " + bookID.toString());
                    } else {
                        book.load(value);
                    }
                }
            });

            //sorting books
            bookStatus = book.getStatus();
            bookRequestStatus = book.getAcceptedRequest().getStatus();

            if (bookStatus == BookStatus.BORROWED) {
                borrowedBooks.add(book);
            } else if (bookStatus == BookStatus.AVAILABLE) {
                availableBooks.add(book);
            } else if (bookRequestStatus == RequestStatus.SENT) {
                requestedBooks.add(book);
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedBooks.add(book);
            }
        }

 */
    Book testBook1 = Book.getOrCreate(UUID.randomUUID());
    try {
        testBook1.setPhoto(new URL("https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2F05k7MxDfbWf2xEUX5WnH?alt=media&token=9b46e3e7-729d-4044-9f7d-04127b773a2e"));
    } catch (Exception e) {}
    testBook1.setDescription(new BookDescription("00099726", "Station Eleven", "St. John Mandel", ""));
    availableBooks.add(testBook1);

    Book testBook2 = Book.getOrCreate(UUID.randomUUID());
    try {
        testBook2.setPhoto(new URL("https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2F131KW4RIqX8zxsTSsaJu?alt=media&token=e2b4903c-9140-4246-82d8-4cf9803d82c8"));
    } catch (Exception e) {}
    testBook2.setDescription(new BookDescription("99128464", "A long walk to water", "Linda Sue Park", ""));
    availableBooks.add(testBook2);

    Book testBook3 = Book.getOrCreate(UUID.randomUUID());
    try {
        testBook3.setPhoto(new URL("https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2F2TLX5w6cxJFDdvPRXQHW?alt=media&token=e2ba83a2-9657-43ca-b99c-530bf37034e4"));
    } catch (Exception e) {}
    testBook3.setDescription(new BookDescription("22299849", "All of my mother's lovers", "Ilana Masad", ""));
    borrowedBooks.add(testBook3);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        ArrayList<ViewBooksSection> sections = new ArrayList<>();
        loadBooks();

        // show a section only if there are books in it
        if (availableBooks.size() > 0) {
            sections.add(new ViewBooksSection("Available Books", availableBooks)); }
        if (requestedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Requested Books", requestedBooks)); }
        if (acceptedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Books", acceptedBooks)); }
        if (borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", borrowedBooks)); }

        RecyclerView viewBooksRecyclerView = findViewById(R.id.book_list);
        ViewBooksAdapter viewBooksAdapter = new ViewBooksAdapter(this, sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
