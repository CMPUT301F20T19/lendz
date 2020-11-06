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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewBooksActivity extends AppCompatActivity {
    private static final String TAG = "ViewBooksActivity";
    FirebaseFirestore database;
    CollectionReference booksReference;

    private ArrayList<Book> availableBooks;
    private ArrayList<Book> requestedBooks;
    private ArrayList<Book> acceptedBooks;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<ViewBooksSection> sections;

    private RecyclerView viewBooksRecyclerView;
    private ViewBooksAdapter viewBooksAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);

        database = FirebaseFirestore.getInstance();
        booksReference = database.collection("books");

        initializeArrayLists();
        loadBooks();
//        checkSections();
//        initRecyclerView();
    }

    /**
    * Initializes the array lists availableBooks, requestedBooks, acceptedBooks,
     * borrowedBooks and sections.
     */
    private void initializeArrayLists() {
        availableBooks = new ArrayList<>();
        requestedBooks = new ArrayList<>();
        acceptedBooks = new ArrayList<>();
        borrowedBooks = new ArrayList<>();

        sections = new ArrayList<>();
    }

    /**
     * Creates a new ViewBooksSection if there is any book in a section
     * (i.e. available books, requested books, accepted books or borrowed books)
     * and adds it to the array list sections.
     */
    private void createSections() {
        if (availableBooks.size() > 0) {
            sections.add(new ViewBooksSection("Available Books", availableBooks));
        }

        if (requestedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Requested Books", requestedBooks));
        }

        if (acceptedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Books", acceptedBooks));
        }

        if (borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", borrowedBooks));
        }
    }

    /**
     * Initializes the recycler view that shows sections and the books in them.
     */
    private void initializeRecyclerView() {
        viewBooksRecyclerView = findViewById(R.id.book_list);
        viewBooksAdapter = new ViewBooksAdapter(this, sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Finds books owned by a user and
     * calls createSections and initializeRecyclerView.
     */
    private void loadBooks() {
        // String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // User currentUser = User.getOrCreate(currentUserID);

        // new code for loading books of user
        final String currentUserId = "gBDk9Ex6KTUcjIgP9LNBLIlJ6h72";

        booksReference
                .whereEqualTo("owner", User.documentOf(currentUserId))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        addBook(document.getId(), document);
                    }

                    createSections();
                    initializeRecyclerView();
                } else {
                    Log.d(TAG, "Error getting book ID: ", task.getException());
                }
            }
        });

    }

    /**
     * Loads data about book and adds the book object to
     * one of the following array lists: availableBooks,
     * requestedBooks, acceptedBooks or borrowedBooks.
     * @param bookId   ID of the book
     * @param snapshot document snapshot corresponding to the book
     */
    private void addBook(String bookId, DocumentSnapshot snapshot) {
        // loading book data
        Book book = Book.getOrCreate(bookId);
        book.load(snapshot);
        BookStatus bookStatus = book.getStatus();
        Request bookAcceptedRequest = book.getAcceptedRequest();

        // adding book to the appropriate array list
        if (bookStatus == BookStatus.BORROWED) {
            borrowedBooks.add(borrowedBooks.size(), book);
        } else if (bookStatus == BookStatus.AVAILABLE) {
            availableBooks.add(availableBooks.size(), book);
        } else if (bookAcceptedRequest != null) {
            RequestStatus bookRequestStatus = bookAcceptedRequest.getStatus();

            if (bookRequestStatus == RequestStatus.SENT) {
                requestedBooks.add(requestedBooks.size(), book);
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedBooks.add(acceptedBooks.size(), book);
            }
        }
    }
}
