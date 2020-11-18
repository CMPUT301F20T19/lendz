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

    private interface FirestoreCallback{
        void onCallback();
    }

    private static final String ARG_USER_ID = "userId";

    private String userID;

    private RecyclerView borrowBooksRecyclerView;
    private ArrayList<Book> borrowedBooks;
    private ArrayList<Book> sentRequests;
    private ArrayList<Book> acceptedRequests;
    private ViewBooksAdapter borrowedBooksAdapter;
    private ArrayList<ViewBooksSection> sections;
    private ArrayList<DocumentReference> bookReferences;
    private ArrayList<Book> books;
    private View borrowedBooksView;
    private int lol=0;
    FirebaseFirestore db;
    CollectionReference booksRef;
    CollectionReference requestsRef;
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
        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");
        borrowedBooksView = view;
        userID = getArguments().getString(ARG_USER_ID);
        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        requestsRef = db.collection("requests");
        setUp();
        borrowBooksRecyclerView = borrowedBooksView.findViewById(R.id.borrowFrag_recyclerView);
        borrowedBooksAdapter = new ViewBooksAdapter(borrowedBooksView.getContext(), sections,this);
        findRequests(new FirestoreCallback() {
            @Override
            public void onCallback() {
//                getBook();
                Toast.makeText(getContext(),"Check Text",Toast.LENGTH_LONG).show();
//                Log.e(TAG, "CheckSections Called: ");
                checkSections();
                initRecyclerView();
            }
        });
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

    private void findRequests(final FirestoreCallback firestoreCallback) {

        requestsRef
                .whereEqualTo("requester", User.documentOf(userID))
                .whereIn("status", Arrays.asList(0,2))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Toast.makeText(getContext(),"IN for loop",Toast.LENGTH_SHORT).show();
                                Log.e(TAG, document.getId() + " => " + document.get("status"));
                                Long statusLong = (Long) document.get("status");
                                RequestStatus requestStatus;
                                if(statusLong == 0) {
                                    requestStatus = RequestStatus.SENT;
                                }else if (statusLong == 2){
                                    requestStatus = RequestStatus.ACCEPTED;
                                }else{
                                    requestStatus = RequestStatus.DECLINED;
                                }
                                DocumentReference reference = (DocumentReference) document.get("book");
//                                Log.e(TAG, "onComplete: " + reference );
                                bookReferences.add(reference);
//                                references.add(reference);
//                                getBook((DocumentReference) document.get("book"),requestStatus);
                            };
                            Log.e(TAG, "onComplete: " + bookReferences);
                            Toast.makeText(getContext(),"Below for loop",Toast.LENGTH_SHORT).show();
//                            Log.e(TAG, "CheckSections Called: ");
                            getBook();
                        } else {
                            Log.d(TAG, "Error getting book ID: ", task.getException());
                        }
                        Log.e(TAG, "CheckSections Called: ");
//                        firestoreCallback.onCallback();
                    }
                });
    }

    private void getBook() {
        for(int i=0; i < bookReferences.size(); i++) {
            lol = i;
            bookReferences.get(i)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            Toast.makeText(getContext(),"Get book",Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, snapshot.getId() + " => " + requestStatus);
//                            addBook(snapshot.getId(),snapshot,requestStatus);
                            addBook(snapshot.getId(),snapshot,RequestStatus.SENT,lol);

                        }
                    });
        }
//        for(DocumentReference reference1: bookReferences) {
//
//        }

    }

    private void addBook(String id, DocumentSnapshot snapshot, RequestStatus requestStatus, int i) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        books.add(book);

        switch (requestStatus) {
            case SENT :
                sentRequests.add(sentRequests.size(),book);
                break;
            case ACCEPTED:
                acceptedRequests.add(acceptedRequests.size(),book);
            default:
                break;
        }
        Toast.makeText(getContext(),"Add book",Toast.LENGTH_SHORT).show();
//        Log.e(TAG, "onComplete: " + sentRequests);
//        Log.e(TAG, "onComplete: " + acceptedRequests);
        if(i == bookReferences.size() - 1){
            checkSections();
            initRecyclerView();
        }
    }

    @Override
    public void onBookClick(int position) {

    }

    @Override
    public void onBookClick(Book book) {

    }
}