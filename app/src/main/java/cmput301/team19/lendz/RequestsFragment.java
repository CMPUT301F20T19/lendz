package cmput301.team19.lendz;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFragment extends Fragment {
    private static final String BOOK_ID = "bookId";

    private String bookId;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressDialog progressDialog;

    public RequestsFragment() {
        // Required empty public constructor
    }

    public static RequestsFragment newInstance(String bookId) {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putString(BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookId = getArguments().getString(BOOK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // Show progress dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(R.string.loading_requests);
        progressDialog.show();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.requests_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Initialize adapter
        final ArrayList<Request> requests = new ArrayList<>();
        adapter = new RequestsAdapter(requests);

        // Query requests for this book
        DocumentReference bookReference = Book.documentOf(bookId);
        Query query = FirebaseFirestore.getInstance()
                .collection("requests")
                .whereEqualTo(Request.BOOK_KEY, bookReference);
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Request request = Request.getOrCreate(documentSnapshot.getId());
                            request.load(documentSnapshot);
                            requests.add(request);
                        }

                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), R.string.failed_to_load_requests, Toast.LENGTH_LONG).show();
                    }
                });

        return view;
    }
}