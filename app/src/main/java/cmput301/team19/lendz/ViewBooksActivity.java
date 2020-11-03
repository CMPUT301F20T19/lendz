package cmput301.team19.lendz;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewBooksActivity extends AppCompatActivity {
    private static final String TAG = "ViewBooksActivity";

    static ArrayList<Book> availableBooks = new ArrayList<>();
    static ArrayList<Book> requestedBooks = new ArrayList<>();
    static ArrayList<Book> acceptedBooks = new ArrayList<>();
    static ArrayList<Book> borrowedBooks = new ArrayList<>();

    private void loadBooks() {
        // String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // User currentUser = User.getOrCreate(currentUserID);

        // new code for loading books of user
        final String currentUserID = "gBDk9Ex6KTUcjIgP9LNBLIlJ6h72";

        Query query = FirebaseFirestore.getInstance()
                .collection("books")
                .whereEqualTo("owner", User.documentOf(currentUserID));

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // loading book data
                                Log.d(TAG, "Processing book ID: " + document.getId());

                                final String bookID = document.getId();
                                final Book book = Book.getOrCreate(bookID);

                                Book.documentOf(bookID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Log.e(TAG,
                                                    "error getting book with ID " + bookID + ": " + error);
                                        } else if (value == null || !value.exists()) {
                                            Log.w(TAG,
                                                    "didn't find book with ID " + bookID);
                                        } else {
                                            book.load(value);

                                            // adding book to appropriate array list
                                            BookStatus bookStatus = book.getStatus();
                                            Request bookAcceptedRequest = book.getAcceptedRequest();

                                            if (bookStatus == BookStatus.BORROWED) {
                                                Log.e(TAG, "adding borrowed book");
                                                ViewBooksActivity.borrowedBooks.add(book);
                                            } else if (bookStatus == BookStatus.AVAILABLE) {
                                                Log.e(TAG, "adding available book");
                                                ViewBooksActivity.availableBooks.add(book);
                                            } else if (bookAcceptedRequest != null) {
                                                Log.e(TAG, "adding unsorted book");
                                                RequestStatus bookRequestStatus = bookAcceptedRequest.getStatus();

                                                if (bookRequestStatus == RequestStatus.SENT) {
                                                    ViewBooksActivity.requestedBooks.add(book);
                                                } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                                                    ViewBooksActivity.acceptedBooks.add(book);
                                                }
                                            }

                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d(TAG, "Error getting book ID: ", task.getException());
                        }
                    }
                });

/*
    // hard-coded book objects for testing
    Book testBook1 = Book.getOrCreate(UUID.randomUUID().toString());
    try {
         testBook1.setPhoto("https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2Fokz3QPexrrsvSCmVJhA9?alt=media&token=2d7d8a8e-54f7-45b0-ae78-e131ad0d01d0");
    } catch (Exception e) {}
    testBook1.setDescription(new BookDescription("111985423", "More than a woman", "Caitlin Moran", ""));
    availableBooks.add(testBook1);

    Book testBook2 = Book.getOrCreate(UUID.randomUUID().toString());
    try {
         testBook2.setPhoto("https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FqfBc7bX4Bp1YdOr08J8N?alt=media&token=89d84c66-c275-4f96-97ee-9fc74fb4ec8b");
    } catch (Exception e) {}
    testBook2.setDescription(new BookDescription("88822115", "The Hobbit", "J.R.R. Tolkien", ""));
    availableBooks.add(testBook2);

    Book testBook3 = Book.getOrCreate(UUID.randomUUID().toString());
    try {
         testBook3.setPhoto("");
    } catch (Exception e) {}
    testBook3.setDescription(new BookDescription("213476", "Pride and prejudice", "Jane Austen", ""));
    borrowedBooks.add(testBook3);
*/
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        ArrayList<ViewBooksSection> sections = new ArrayList<>();
        loadBooks();

        // code for testing
        Log.e(TAG, "ViewBooksActivity.availableBooks.size() = " + ViewBooksActivity.availableBooks.size());
        Log.e(TAG, "ViewBooksActivity.borrowedBooks.size() = " + ViewBooksActivity.borrowedBooks.size());
        Log.e(TAG, "ViewBooksActivity.requestedBooks.size() = " + ViewBooksActivity.requestedBooks.size());
        Log.e(TAG, "ViewBooksActivity.acceptedBooks.size() = " + ViewBooksActivity.acceptedBooks.size());

        // show a section if and only if there are books in it
        if (ViewBooksActivity.availableBooks.size() > 0) {
            sections.add(new ViewBooksSection("Available Books", ViewBooksActivity.availableBooks)); }
        if (ViewBooksActivity.requestedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Requested Books", ViewBooksActivity.requestedBooks)); }
        if (ViewBooksActivity.acceptedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Books", ViewBooksActivity.acceptedBooks)); }
        if (ViewBooksActivity.borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", ViewBooksActivity.borrowedBooks)); }

        // showing book list
        RecyclerView viewBooksRecyclerView = findViewById(R.id.book_list);
        ViewBooksAdapter viewBooksAdapter = new ViewBooksAdapter(this, sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
