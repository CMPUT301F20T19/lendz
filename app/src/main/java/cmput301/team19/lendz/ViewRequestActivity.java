package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewRequestActivity extends AppCompatActivity {
    private ViewRequestCustomAdapter adapter;
    TextView requestCountLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        ListView requestBookListView = findViewById(R.id.requestListView);
        requestCountLabel = findViewById(R.id.requestCount);

        final ArrayList<BorrowerInfo> requestObjectArray = new ArrayList<>();;
        //connect ListView to its array content using a custom adapter
        adapter = new ViewRequestCustomAdapter(this, R.layout.view_book_request,requestObjectArray);
        requestBookListView.setAdapter(adapter);
        Intent intent = getIntent();
        final String bookId = intent.getStringExtra("bookId");
        //create a pointer to book details
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        final DocumentReference bookReference = firestore.collection("books").document(bookId);
        firestore.collection("requests")
                .whereEqualTo("book", bookReference)
                .whereEqualTo("status", 0)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(ViewRequestActivity.this,"Listen failed" + e.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.clear();
                if(value == null || value.isEmpty()){
                    requestCountLabel.setText(" No Requests Made for this Book");
                    return;
                }
               for (DocumentSnapshot doc : value) {
                   Request request = Request.getOrCreate(doc.getId());
                   request.load(doc);
                   String username = request.getRequesterUsername();
                   String userId = request.getRequester().getId();
                   BorrowerInfo requesterInfo = new BorrowerInfo(username,userId,doc.getId(),bookId);
                   requestObjectArray.add(requesterInfo);
               }
               adapter.notifyDataSetChanged();
               requestCountLabel.setText(adapter.getCount() + " requests made for this book");
            }
        });
    }
}


