package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewRequestActivity extends AppCompatActivity {
    FirebaseFirestore firestoreRef;
    CollectionReference requestCollection;
    private ListView requestBookListView;
    private ViewRequestCustomAdapter adapter;
    private ArrayList<BorrowerInfo> bb = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        requestBookListView = findViewById(R.id.requestListView);

        BorrowerInfo newOne = new BorrowerInfo("Andrews", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "23:21");
        BorrowerInfo newTwo = new BorrowerInfo("Isaac", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "24:21");
        BorrowerInfo newThree = new BorrowerInfo("Ziggy", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "25:21");
        bb.add(newOne);
        bb.add(newTwo);
        bb.add(newThree);

        //connect ListView to its array content using a custom adapter
        adapter = new ViewRequestCustomAdapter(this, R.layout.view_book_request,bb);
        requestBookListView.setAdapter(adapter);

        final String bookId = "6vfFWoW3WKIZhikCuv7q";

        //check if Book Request exist
        firestoreRef = FirebaseFirestore.getInstance();

        //create a pointer to book details
        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);

        requestCollection = firestoreRef.collection("requests");
        Query query = requestCollection.whereEqualTo("book", bookReference);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    boolean b = task.getResult().isEmpty();

                    if (b == true){
                        Toast.makeText(ViewRequestActivity.this,"Request Exist",Toast.LENGTH_SHORT).show();
                        //load requests into listview


                    }else{
                        //No request for selected book
                        Toast.makeText(ViewRequestActivity.this,"No Request Exist",Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ViewRequestActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
