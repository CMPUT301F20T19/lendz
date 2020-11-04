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
 * A simple {@link Fragment} subclass.
 * Use the {@link MyBooksFragment#newInstance} factory method to
 * create an instance of this fragment.
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
        addBook(view);
        return view;
    }

    private void addBook(View view) {
        FloatingActionButton button = view.findViewById(R.id.add_book_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddBookActivity.class);
                startActivity(intent);
            }
        });
    }

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
        viewBooksRecyclerView = myBooksView.findViewById(R.id.myBooksFrag_recyclerView);
        viewBooksAdapter = new ViewBooksAdapter(myBooksView.getContext(), sections,this);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.setLayoutManager(new LinearLayoutManager(myBooksView.getContext()));
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(myBooksView.getContext(), DividerItemDecoration.VERTICAL));
    }

    private void loadBooks() {

        booksRef
                .whereEqualTo("owner", User.documentOf(userID))
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

    @Override
    public void onBookClick(int position) {
        Intent intent = new Intent(getActivity(),AddBookActivity.class);
        startActivity(intent);
    }

}
