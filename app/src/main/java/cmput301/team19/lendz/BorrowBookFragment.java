package cmput301.team19.lendz;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BorrowBookFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";


    private String userID;

    private RecyclerView viewBooksRecyclerView;
    private ArrayList<Book> availableBooks;
    private ArrayList<Book> requestedBooks;
    private ArrayList<Book> acceptedBooks;
    private ArrayList<Book> borrowedBooks;
    private ViewBooksAdapter viewBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    private View borrowView;
    FirebaseFirestore db;
    CollectionReference booksRef;

    public BorrowBookFragment() {
        // Required empty public constructor
    }

    public static BorrowBookFragment newInstance(String userId) {
        BorrowBookFragment fragment = new BorrowBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_borrow_book, container, false);
        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        userID = getArguments().getString(ARG_USER_ID);
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        setUp();
        loadBooks();
        borrowView = view;
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.borrow_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ff");
        int itemID = item.getItemId();
        if(itemID == R.id.search_item) {
            openSearchActivity();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void openSearchActivity() {
        Intent intent = new Intent(getActivity(),SearchBooksActivities.class);
        startActivity(intent);
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

    private void initRecyclerView() {
        viewBooksRecyclerView = borrowView.findViewById(R.id.borrowFrag_recyclerView);
        viewBooksAdapter = new ViewBooksAdapter(borrowView.getContext(), sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.setLayoutManager(new LinearLayoutManager(borrowView.getContext()));
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(borrowView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadBooks() {
        final String currentUserID = "gBDk9Ex6KTUcjIgP9LNBLIlJ6h72";

        booksRef
                .whereEqualTo("owner", User.documentOf(userID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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

    private void addBooks(String id, DocumentSnapshot snapshot) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        BookStatus bookStatus = book.getStatus();
        Request bookAcceptedRequest = book.getAcceptedRequest();
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

}