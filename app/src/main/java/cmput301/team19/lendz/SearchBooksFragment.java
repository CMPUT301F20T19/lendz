package cmput301.team19.lendz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity that allows the user to search for books based on keywords
 */
public class SearchBooksFragment extends Fragment implements OnBookClickListener{
    private SearchViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private EditText searchEditText;
    FirebaseFirestore db;
    CollectionReference booksRef;
    ProgressDialog progressDialog;

    private SearchBooksFragment() {

    }

    public static SearchBooksFragment newInstance() {
        return new SearchBooksFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_books, container, false);

        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        books = new ArrayList<>();
        recyclerView = view.findViewById(R.id.search_recyclerview);
        searchEditText = view.findViewById(R.id.search_edit);
        initRecyclerView();

        Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchButtonClicked();
            }
        });

        // Handle search button in soft keyboard
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearchButtonClicked();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    /**
     * initializes and sets up the recycler view to be used for displaying search results
     */
    private void initRecyclerView() {
        adapter = new SearchViewAdapter(books,getContext(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Listens for a click on the search button and triggers the assigned function for performing searches
     */
    public void onSearchButtonClicked() {
        String searchText = searchEditText.getText().toString().trim();
        performSearch(searchText);
    }

    /**
     * Receives a query and perform a search based on the query in the database
     * @param query the keyword the user typed which is used for searching the db
     */
    private void performSearch(String query) {
        books.clear();
        adapter.notifyDataSetChanged();

        // Show progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.searching_in_progress);
        progressDialog.show();

        // Query matching books
        booksRef.whereIn("status", Arrays.asList(0, 1))
                .whereArrayContains("keywords", query.toLowerCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                addBook(document.getId(), document);
                            }
                        } else {
                            Toast.makeText(
                                    getContext(), R.string.search_failed, Toast.LENGTH_LONG).show();
                            Log.e("Search", "Search failed", task.getException());
                        }
                    }
                });
    }

    /**
     * Creates and adds a book object to the search results recyclerView from a snapshot and book id
     ** @param id of the Book to be added to the respective arrayList
     ** @param snapshot of the book from the database
     */
    private void addBook(String id, DocumentSnapshot snapshot) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        books.add(books.size(),book);
        adapter.notifyItemInserted(books.size());
    }

    /**
     * Listens for a book click and starts an intent to view the book details
     * @param position the position in the recycler view that was clicked
     */
    @Override
    public void onBookClick(int position) {
        Book book = books.get(position);
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

    @Override
    public void onBookClick(Book book) {
        Log.d("onBookClick", "Book book");
    }
}