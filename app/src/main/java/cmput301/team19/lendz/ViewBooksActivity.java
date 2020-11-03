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
import com.google.firebase.firestore.CollectionReference;
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

    private RecyclerView viewBooksRecyclerView;
    private ArrayList<Book> availableBooks;
    private ArrayList<Book> requestedBooks;
    private ArrayList<Book> acceptedBooks;
    private ArrayList<Book> borrowedBooks;
    private ViewBooksAdapter viewBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    FirebaseFirestore db;
    CollectionReference booksRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        setUp();
        loadBooks();
        checkSections();
        initRecyclerView();
    }

    private void setUp() {
        availableBooks = new ArrayList<>();
        requestedBooks = new ArrayList<>();
        acceptedBooks = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
        sections = new ArrayList<>();
    }

    private void checkSections() {
        if (availableBooks.size() > 0) {
            sections.add(new ViewBooksSection("Available Books", availableBooks)); }
        if (requestedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Requested Books", requestedBooks)); }
        if (acceptedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Books", acceptedBooks)); }
        if (borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", borrowedBooks)); }
    }

    private void initRecyclerView() {
        viewBooksRecyclerView = findViewById(R.id.book_list);
        viewBooksAdapter = new ViewBooksAdapter(this, sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadBooks() {
        // String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // User currentUser = User.getOrCreate(currentUserID);

        // new code for loading books of user
        final String currentUserID = "gBDk9Ex6KTUcjIgP9LNBLIlJ6h72";

        booksRef
            .whereEqualTo("owner", User.documentOf(currentUserID))
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        // loading book data
//                                Log.d(TAG, "Processing book ID: " + document.getId());
                        addBooks(document.getId(),document);
                    }
                } else {
                    Log.d(TAG, "Error getting book ID: ", task.getException());
                }
            }
        });


    }

    private void addBooks(String id, DocumentSnapshot snapshot) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        BookStatus bookStatus = book.getStatus();
        Request bookAcceptedRequest = book.getAcceptedRequest();
        if (bookStatus == BookStatus.BORROWED) {
            Log.e(TAG, "adding borrowed book");
            borrowedBooks.add(borrowedBooks.size(), book);
            viewBooksAdapter.notifyItemInserted(borrowedBooks.size());
        } else if (bookStatus == BookStatus.AVAILABLE) {
            Log.e(TAG, "adding available book");
            availableBooks.add(availableBooks.size(),book);
            viewBooksAdapter.notifyItemInserted(availableBooks.size());
        } else if (bookAcceptedRequest != null) {
            Log.e(TAG, "adding unsorted book");
            RequestStatus bookRequestStatus = bookAcceptedRequest.getStatus();
            if (bookRequestStatus == RequestStatus.SENT) {
                requestedBooks.add(requestedBooks.size(),book);
                viewBooksAdapter.notifyItemInserted(requestedBooks.size());
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedBooks.add(acceptedBooks.size(),book);
                viewBooksAdapter.notifyItemInserted(acceptedBooks.size());
            }
        }
        Log.e(TAG, book.getDescription().getTitle());
    }
}






//                                final String bookID = document.getId();
//                                final Book book = Book.getOrCreate(bookID);
//                                book.load(document);

//                                Book.documentOf(bookID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                        if (error != null) {
//                                            Log.e(TAG,
//                                                    "error getting book with ID " + bookID + ": " + error);
//                                        } else if (value == null || !value.exists()) {
//                                            Log.w(TAG,
//                                                    "didn't find book with ID " + bookID);
//                                        } else {
//                                            // adding book to appropriate array list
//
//
//                                        }
//                                    }
//                                });




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