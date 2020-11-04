package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchBooksActivities extends AppCompatActivity implements OnBookClickListener{

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

    private void setUp() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();
    }

    private void initRecyclerView() {
        adapter = new SearchViewAdapter(books,this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void onSearchButtonClicked(View view) {
        books.clear();
        adapter.notifyDataSetChanged();
        searchET = findViewById(R.id.search_edit);
        String searchText = searchET.getText().toString().trim();
        performSearch(searchText);
    }

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
                        addBook(document.getId(),document);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addBook(String id, DocumentSnapshot snapshot) {
        Book book = Book.getOrCreate(id);
        book.load(snapshot);
        books.add(books.size(),book);
        adapter.notifyItemInserted(books.size());
    }

    @Override
    public void onBookClick(int position) {
        Intent intent = new Intent();
        startActivity(intent);
    }
}