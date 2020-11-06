package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity that allows the user to search for books based on keywords
 */
public class SearchBooksActivity extends AppCompatActivity implements OnBookClickListener{

    private static final String TAG = "Search created" ;
    private SearchViewAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Book> books;
    private EditText searchET;
    FirebaseFirestore db;
    CollectionReference booksRef;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books_activities);

        db = FirebaseFirestore.getInstance();
        booksRef = db.collection("books");
        books = new ArrayList<>();
        recyclerView = findViewById(R.id.search_recyclerview);
        initRecyclerView();

    }

    /**
     * Initializes and shows the progress dialog
     */
    private void setUp() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
    }

    /**
     * initializes and sets up the recycler view to be used for displaying search results
     */
    private void initRecyclerView() {
        adapter = new SearchViewAdapter(books,this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    /**
     * Listens for a click on the search button and triggers the assigned function for performing searches
     * @param view the current view being displayed.i.e the searchBookActivities
     */
    public void onSearchButtonClicked(View view) {
        books.clear();
        adapter.notifyDataSetChanged();
        searchET = findViewById(R.id.search_edit);
        String searchText = searchET.getText().toString().trim();
        performSearch(searchText);
    }

    /**
     * Receives a query and perform a search based on the query in the database
     * @param query the keyword the user typed which is used for searching the db
     */
    private void performSearch(String query ) {
        setUp();
        booksRef
                .whereIn("status", Arrays.asList(0,1))
                .whereArrayContains("keywords",query)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
                        if(document != null) {
                            addBook(document.getId(),document);
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
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

    }

    @Override
    public void onBookClick(Book book) {

    }
}