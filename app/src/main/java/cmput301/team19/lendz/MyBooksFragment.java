package cmput301.team19.lendz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Fragment that displays all the books the current user owns
 */
public class MyBooksFragment extends Fragment implements OnBookClickListener {
    private RecyclerView viewBooksRecyclerView;
    private final ArrayList<Book> availableBooks = new ArrayList<>();
    private final ArrayList<Book> requestedBooks = new ArrayList<>();
    private final ArrayList<Book> acceptedBooks = new ArrayList<>();
    private final ArrayList<Book> borrowedBooks = new ArrayList<>();
    private ViewBooksAdapter viewBooksAdapter;
    private final ArrayList<ViewBooksSection> sections = new ArrayList<>();
    private View myBooksView;
    FirebaseFirestore db;
    CollectionReference booksRef;
    ProgressDialog progressDialog;

    public MyBooksFragment() {
        // Required empty public constructor
    }

    /**
     * Receives the current user's id and creates a new instance of the myBookFragment
     * @return a new MyBooksFragment if successful
     */
    public static MyBooksFragment newInstance() {
        MyBooksFragment fragment = new MyBooksFragment();
        Bundle args = new Bundle();
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
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myBooksView = view;

        // Show a progress dialog while books are loading for the first time
        progressDialog = new ProgressDialog(myBooksView.getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        // Add a snapshot listener to get the current user's owned books
        booksRef.whereEqualTo("owner", User.documentOf(User.getCurrentUser().getId()))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException error) {
                        if (error != null || value == null) {
                            Log.e(TAG, "Error getting owned books: ", error);
                        }
                        loadBooks(value);
                    }
                });

        // Set click listener of add book button
        FloatingActionButton addBookButton = view.findViewById(R.id.add_book_button);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditBookActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Creates a new ViewBooksSection if there is any book in a section
     * (i.e. available books, requested books, accepted books or borrowed books)
     * and adds it to the array list sections.
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
        boolean noBooks = availableBooks.isEmpty()
                && requestedBooks.isEmpty()
                && acceptedBooks.isEmpty()
                && borrowedBooks.isEmpty();
        myBooksView.findViewById(R.id.no_owned_books).setVisibility(
                noBooks ? View.VISIBLE : View.GONE);
    }

    /**
     * Initializes the recycler view that shows sections and the books in them.
     */
    private void initRecyclerView() {
        viewBooksRecyclerView = myBooksView.findViewById(R.id.myBooksFrag_recyclerView);
        viewBooksAdapter = new ViewBooksAdapter(myBooksView.getContext(), sections,this);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.setLayoutManager(new LinearLayoutManager(myBooksView.getContext()));
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(myBooksView.getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Clears existing books and sections.
     * Loads books from a QuerySnapshot then
     * calls checkSections and initRecyclerView.
     */
    private void loadBooks(@Nullable QuerySnapshot snapshot) {
        availableBooks.clear();
        requestedBooks.clear();
        acceptedBooks.clear();
        borrowedBooks.clear();
        sections.clear();

        progressDialog.dismiss();

        if (snapshot != null) {
            for (QueryDocumentSnapshot document : snapshot) {
                Book book = Book.getOrCreate(document.getId());
                book.load(document);
                addBook(book);
            }
        }
        checkSections();
        initRecyclerView();
    }

    /**
     * Adds a book object to
     * one of the following array lists: availableBooks,
     * requestedBooks, acceptedBooks or borrowedBooks.
     * @param book Book to add
     */
    private void addBook(Book book) {
        BookStatus bookStatus = book.getStatus();

        // adding book to the appropriate array list
        if (bookStatus == BookStatus.BORROWED) {
            borrowedBooks.add(borrowedBooks.size(), book);
        } else if (bookStatus == BookStatus.AVAILABLE) {
            availableBooks.add(availableBooks.size(),book);
        } else if (bookStatus == BookStatus.REQUESTED) {
            requestedBooks.add(requestedBooks.size(),book);
        } else if (bookStatus == BookStatus.ACCEPTED) {
            acceptedBooks.add(acceptedBooks.size(),book);
        }
    }

    @Override
    public void onBookClick(int position) {

    }

    /**
     * Receives the book that was clicked and passes it on
     * to the viewFragment; to view the book details
     * @param book clicked from the recycler view
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
