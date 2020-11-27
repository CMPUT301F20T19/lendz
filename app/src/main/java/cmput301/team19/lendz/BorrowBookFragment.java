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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * This fragment displays the borrowed books of the current user
 * as well as the sent and accepted request for books.
 *
 * BUG
 * It is currently not functional as other functionalities need to be finished before
 */
public class BorrowBookFragment extends Fragment implements OnBookClickListener{
    private RecyclerView borrowBooksRecyclerView;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> sentRequests;
    private ArrayList<Book> acceptedRequests;
    private ViewBooksAdapter borrowedBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    private ArrayList<DocumentReference> bookReferences;
    private View borrowedBooksView;
    FirebaseFirestore db;
    CollectionReference booksRef;
    CollectionReference requestsRef;
    ProgressDialog progressDialog;

    private boolean borrowDone = false;
    private boolean sentDone = false;
    private boolean acceptedDone = false;

    public BorrowBookFragment() {
        // Required empty public constructor
    }

    public static BorrowBookFragment newInstance() {
        BorrowBookFragment fragment = new BorrowBookFragment();
        Bundle args = new Bundle();
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
        borrowedBooksView = view;
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        requestsRef = db.collection("requests");
        setUp();
        borrowBooksRecyclerView = borrowedBooksView.findViewById(R.id.borrowFrag_recyclerView);
        borrowedBooksAdapter = new ViewBooksAdapter(borrowedBooksView.getContext(), sections,this);
        getSentRequests();
        getAcceptedRequests();
        getBorrowRequest();
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
            openSearchFragment();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkCompletion() {
        if( sentDone && borrowDone && acceptedDone) {
            progressDialog.dismiss();
            checkSections();
            initRecyclerView();
        }
    }

    private void openSearchFragment() {
        Fragment searchFragment = SearchBooksFragment.newInstance();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
        );

        transaction.replace(R.id.container, searchFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void setUp() {
        progressDialog = new ProgressDialog(borrowedBooksView.getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.show();
        bookReferences = new ArrayList<>();
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
        borrowBooksRecyclerView = borrowedBooksView.findViewById(R.id.borrowFrag_recyclerView);
        borrowedBooksAdapter = new ViewBooksAdapter(borrowedBooksView.getContext(), sections,this);
        borrowBooksRecyclerView.setAdapter(borrowedBooksAdapter);
        borrowBooksRecyclerView.setLayoutManager(new LinearLayoutManager(borrowedBooksView.getContext()));
        borrowBooksRecyclerView.addItemDecoration(new DividerItemDecoration(borrowedBooksView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void getBorrowRequest() {
        borrowDone = true;
        checkCompletion();
    }

    private void getSentRequests() {
        if (User.getCurrentUser() == null) {
            return;
        }

        booksRef
                .whereArrayContains("pendingRequesters", User.documentOf(User.getCurrentUser().getId()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addBook(document.getId(),document,RequestStatus.SENT);
                            }
                        }else {
                            Log.d(TAG, "Error getting book ID: ", task.getException());
                        }
                        sentDone = true;
                        checkCompletion();
                    }
                });
    }

    private void getAcceptedRequests() {
        if (User.getCurrentUser() == null) {
            return;
        }

        booksRef
                .whereEqualTo("acceptedRequester", User.documentOf(User.getCurrentUser().getId()))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addBook(document.getId(),document,RequestStatus.ACCEPTED);
                            }
                        }else{
                            Log.d(TAG, "Error getting book ID: ", task.getException());
                        }
                        acceptedDone = true;
                        checkCompletion();
                    }
                });
    }

    private void addBook(String id, DocumentSnapshot snapshot, RequestStatus requestStatus) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        switch (requestStatus) {
            case SENT:
                sentRequests.add(sentRequests.size(),book);
                break;
            case ACCEPTED:
                if (book.getStatus() == BookStatus.ACCEPTED) {
                    acceptedRequests.add(acceptedRequests.size(), book);
                } else if (book.getStatus() == BookStatus.BORROWED) {
                    borrowedBooks.add(book);
                }
            default:
                break;
        }
    }

    @Override
    public void onBookClick(int position) {

    }

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