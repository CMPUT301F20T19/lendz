package cmput301.team19.lendz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.Inet4Address;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Fragment that displays all the books the current user owns
 */
public class MyBooksFragment extends Fragment implements OnBookClickListener {

    private static final String ARG_USER_ID = "userId";


    private String userID;

    private RecyclerView viewBooksRecyclerView;
    private ArrayList<Book> availableBooks;
    private ArrayList<Book> requestedBooks;
    private ArrayList<Book> acceptedBooks;
    private ArrayList<Book> borrowedBooks;
    private ViewBooksAdapter viewBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    private View myBooksView;
    FirebaseFirestore db;
    CollectionReference booksRef;
    ProgressDialog progressDialog;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Receives the current user's id and creates a new instance of the myBookFragment
     * @param userId the current user's id
     * @return a new MyBooksFragment if successful
     */
    public static MyBooksFragment newInstance(String userId) {
        MyBooksFragment fragment = new MyBooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_books, container, false);
        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");
        myBooksView = view;
        userID = getArguments().getString(ARG_USER_ID);
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        setUp();
        loadBooks();
        onAddBook(view);
        return view;
    }

    /**
     * sets a click listener of the add book button and
     * starts an intent when the button is clicked
     * @param view the current view the fragment is being displayed
     */
    private void onAddBook(View view) {
        FloatingActionButton button = view.findViewById(R.id.add_book_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddBookActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the array lists availableBooks, requestedBooks, acceptedBooks,
     * borrowedBooks and sections.
     */
    private void setUp() {
        progressDialog = new ProgressDialog(myBooksView.getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        availableBooks = new ArrayList<>();
        requestedBooks = new ArrayList<>();
        acceptedBooks = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
        sections = new ArrayList<>();
    }

    /**
<<<<<<< HEAD
     * Creates a new ViewBooksSection if there is any book in a section
     * (i.e. available books, requested books, accepted books or borrowed books)
     * and adds it to the array list sections.
=======
     * Check if the sections contains books to be displayed
>>>>>>> dev
     */
    private void checkSections() {
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
<<<<<<< HEAD
     * Initializes the recycler view that shows sections and the books in them.
=======
     * initializes and sets up the recycler view to be used for displaying books
>>>>>>> dev
     */
    private void initRecyclerView() {
        viewBooksRecyclerView = myBooksView.findViewById(R.id.myBooksFrag_recyclerView);
        viewBooksAdapter = new ViewBooksAdapter(myBooksView.getContext(), sections,this);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.setLayoutManager(new LinearLayoutManager(myBooksView.getContext()));
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(myBooksView.getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
<<<<<<< HEAD
     * Finds books owned by a user and
     * calls checkSections and initRecyclerView.
=======
     * Searches firebase for the current user's books
     * and sorts them
>>>>>>> dev
     */
    private void loadBooks() {
        booksRef
                .whereEqualTo("owner", User.documentOf(userID))
//                .orderBy("description.title")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addBooks(document.getId(),document);
                            }
                            checkSections();
                            initRecyclerView();
                        } else {
                            Log.d(TAG, "Error getting book ID: ", task.getException());
                        }
                    }
                });
    }

    /**
<<<<<<< HEAD
     * Loads data about book and adds the book object to
     * one of the following array lists: availableBooks,
     * requestedBooks, acceptedBooks or borrowedBooks.
     * @param id   ID of the book
     * @param snapshot document snapshot corresponding to the book
=======
     * this functions loads book from the snapshot and id given
     * and adds them to their respective arrayList
     * @param id of the Book to be added to the respective arrayList
     * @param snapshot of the book from the database
>>>>>>> dev
     */
    private void addBooks(String id, DocumentSnapshot snapshot) {
        // loading book data
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        BookStatus bookStatus = book.getStatus();
        Request bookAcceptedRequest = book.getAcceptedRequest();

        // adding book to the appropriate array list
        if (bookStatus == BookStatus.BORROWED) {
            borrowedBooks.add(borrowedBooks.size(), book);
        } else if (bookStatus == BookStatus.AVAILABLE) {
            availableBooks.add(availableBooks.size(),book);
        } else if (bookAcceptedRequest != null) {
            RequestStatus bookRequestStatus = bookAcceptedRequest.getStatus();

            if (bookRequestStatus == RequestStatus.SENT) {
                requestedBooks.add(requestedBooks.size(),book);
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedBooks.add(acceptedBooks.size(),book);
            }
        }
    }

    @Override
    public void onBookClick(int position) {

    }

    /**
<<<<<<< HEAD
     * Shows data about the book clicked on.
     * @param book the book clicked on
=======
     * Receives the book that was clicked and passes it on
     * to the viewFragment; to view the book details
     * @param book clicked from the recycler view
>>>>>>> dev
     */
    @Override
    public void onBookClick(Book book) {
        Fragment viewBookFragment = ViewBookFragment.newInstance(book.getId());
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
        );

        transaction.replace(R.id.container, viewBookFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

}
