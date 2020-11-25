package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewRequestActivity extends AppCompatActivity {
    FirebaseFirestore firestoreRef;
    CollectionReference requestCollection;
    private ListView requestBookListView;
    private ViewRequestCustomAdapter adapter;
    private ArrayList<BorrowerInfo> requestObjectArray = new ArrayList<>();
    private ArrayList<QueryDocumentSnapshot> requesterRefHolder = new ArrayList<>();
    private TextView RequestCountLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_request);
        requestBookListView = findViewById(R.id.requestListView);
        RequestCountLabel = findViewById(R.id.requestCount);
        //connect ListView to its array content using a custom adapter
        adapter = new ViewRequestCustomAdapter(this, R.layout.view_book_request,requestObjectArray);
        requestBookListView.setAdapter(adapter);
        Intent intent = getIntent();
        final String bookId = intent.getStringExtra("bookId");
        firestoreRef = FirebaseFirestore.getInstance();
        //create a pointer to book details
        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);
        requestCollection = firestoreRef.collection("requests");
        requestCollection.whereEqualTo("book", bookReference).whereEqualTo("status", 0).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    Toast.makeText(ViewRequestActivity.this,"Listen failed" + e.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.clear();
                requesterRefHolder.clear();
                if(value.isEmpty() == true){
                    RequestCountLabel.setText(" No Requests Made for this Book");
                }
               synchronousTask1(new FireStoreCallback() {
                   @Override
                   public void onCallback() {
                       for (int i = 0; i < requesterRefHolder.size(); i++) {
                           QueryDocumentSnapshot doc = requesterRefHolder.get(i);
                           ReadDataFromFirebase(doc,i,bookId);
                       }
                   }
               },value);
            }
        });
    }

    //for loop for this callback
    public void ReadDataFromFirebase(QueryDocumentSnapshot doc, final int i, final String bookID){
        DocumentReference requesterRef = doc.getDocumentReference("requester");
        final String requestId = doc.getId();
        requesterRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               //get username of requester document
               String User_Name = documentSnapshot.getString("fullName");
               String User_id = documentSnapshot.getId();
               BorrowerInfo requesterInfo  = new BorrowerInfo(User_Name,User_id,requestId,bookID);
               requestObjectArray.add(requesterInfo);
               adapter.notifyDataSetChanged();
               Toast.makeText(ViewRequestActivity.this,"called again",Toast.LENGTH_SHORT).show();
               //check if its last element
               if(i == (requesterRefHolder.size()-1)){
                   RequestCountLabel.setText(" "+String.valueOf(adapter.getCount()) + " Requests Made for this Book");
               }
           }
       });
    }

    private interface FireStoreCallback{
        void onCallback();
    }

    private void synchronousTask1(FireStoreCallback firestoreCallback,QuerySnapshot value){
        for (QueryDocumentSnapshot doc : value) {
            if (doc.getId() != null){
                requesterRefHolder.add(doc);
            }
        }
        firestoreCallback.onCallback();
    }

}


