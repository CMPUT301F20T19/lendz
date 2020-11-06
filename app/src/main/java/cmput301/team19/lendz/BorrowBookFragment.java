package cmput301.team19.lendz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

/**
 * This fragment displays the borrowed books of the current user
 * as well as the sent and accepted request for books.
 *
 * BUG
 * It is currently not functional as other functionalities need to be finished before
 */
public class BorrowBookFragment extends Fragment implements OnBookClickListener{
    private static final String ARG_USER_ID = "userId";

    private String userID;

    private RecyclerView borrowBooksRecyclerView;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> sentRequests;
    private ArrayList<Book> acceptedRequests;
    private ViewBooksAdapter borrowedBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    private View borrowedBooksView;
    FirebaseFirestore db;
    CollectionReference booksRef;
    ProgressDialog progressDialog;

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
//        if (getArguments() == null)
//            throw new IllegalArgumentException("no arguments");
        borrowedBooksView = view;
//        userID = getArguments().getString(ARG_USER_ID);
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
//        setUp();
//        loadBooks();
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
        Intent intent = new Intent(getActivity(), SearchBooksActivity.class);
        startActivity(intent);
    }

    private void setUp() {
        progressDialog = new ProgressDialog(borrowedBooksView.getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        borrowedBooks = new ArrayList<>();
        sentRequests = new ArrayList<>();
        acceptedRequests = new ArrayList<>();
        borrowedBooks = new ArrayList<>();
        sections = new ArrayList<>();
    }

    private void checkSections() {
        if (borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", borrowedBooks));
        }
        if (acceptedRequests.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Request", acceptedRequests));
        }
        if (sentRequests.size() > 0) {
            sections.add(new ViewBooksSection("Sent Request", sentRequests));
        }
    }

    private void initRecyclerView() {
        borrowBooksRecyclerView = borrowedBooksView.findViewById(R.id.myBooksFrag_recyclerView);
        borrowedBooksAdapter = new ViewBooksAdapter(borrowedBooksView.getContext(), sections,this);
        borrowBooksRecyclerView.setAdapter(borrowedBooksAdapter);
        borrowBooksRecyclerView.setLayoutManager(new LinearLayoutManager(borrowedBooksView.getContext()));
        borrowBooksRecyclerView.addItemDecoration(new DividerItemDecoration(borrowedBooksView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadBooks() {

        booksRef
                .whereEqualTo("owner", User.documentOf(userID))
                .whereIn("requestStatus", Arrays.asList(0,2))
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


    private void addBooks(String id, DocumentSnapshot snapshot) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        BookStatus bookStatus = book.getStatus();
//        RequestStatus requestStatus = book.
        Request bookAcceptedRequest = book.getAcceptedRequest();
        if (bookStatus == BookStatus.BORROWED) {
            borrowedBooks.add(borrowedBooks.size(), book);
        } else if (bookAcceptedRequest != null) {
            RequestStatus bookRequestStatus = bookAcceptedRequest.getStatus();
            if (bookRequestStatus == RequestStatus.SENT) {
                sentRequests.add(sentRequests.size(),book);
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedRequests.add(acceptedRequests.size(),book);
            }
        }
    }

    @Override
    public void onBookClick(int position) {

    }

    @Override
    public void onBookClick(Book book) {

    }
}