package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class ViewRequestActivity extends AppCompatActivity {
    FirebaseFirestore firestoreRef;
    CollectionReference requestCollection;
    private ListView requestBookListView;
    private ViewRequestCustomAdapter adapter;
    private ArrayList<BorrowerInfo> requestObjectArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        requestBookListView = findViewById(R.id.requestListView);

        //connect ListView to its array content using a custom adapter
        adapter = new ViewRequestCustomAdapter(this, R.layout.view_book_request,requestObjectArray);
        requestBookListView.setAdapter(adapter);
        final String bookId = "FTVxy59Hf0dGFD3FwibN";
        firestoreRef = FirebaseFirestore.getInstance();
        //create a pointer to book details
        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);
        requestCollection = firestoreRef.collection("requests");
        requestCollection.whereEqualTo("book", bookReference).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    Toast.makeText(ViewRequestActivity.this,"Listen failed" + e.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ViewRequestActivity.this,"in query",Toast.LENGTH_SHORT).show();
                requestObjectArray.clear();
                //retrieve newly added object
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.getId() != null){
                        DocumentReference requesterRef = doc.getDocumentReference("requester");
                        String RequestId = doc.getId();
                        long timeStamp = doc.getLong("timestamp");
                        ReadDataFromFirebase(requesterRef,RequestId, timeStamp);
                    }
                }
            }
        });
    }

    //

    public void ReadDataFromFirebase(DocumentReference u, final String id, final long timeStamp){
       DocumentReference requesterRef = u;
       requesterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               //get username of requester
               String User_Name = documentSnapshot.getString("fullName");
               BorrowerInfo requesterInfo  = new BorrowerInfo(User_Name, "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                       timeStamp, id);
               requestObjectArray.add(requesterInfo);
               adapter.notifyDataSetChanged();
           }
       });
    }

    //create a timer that updates the calculated time difference






    public void fetchUserDetails(){

    }

    public void calculateTimeDifference(){

    }

}
