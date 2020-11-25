package cmput301.team19.lendz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import static cmput301.team19.lendz.R.id.viewBookBack;

/**
 * Fragment for viewing book details information
 * Used in BookActivity
 */
public class ViewBookFragment extends Fragment {
    // Parameter names
    private static final String ARG_BOOK_ID = "bookId";
    Button requestBtn;
    private Book book;
    FirebaseFirestore firestoreRef;
    CollectionReference requestCollection;


    private TextView bookTitleTextView, bookStatusTextView, bookDescriptionTextView, bookAuthorTextView,
            bookISBNTextVIew, bookOwnerTextView, bookBorrowerTextView;

    private ImageView bookImage, ownerImage;

    public static ViewBookFragment newInstance(String bookId) {
        ViewBookFragment fragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * @param savedInstanceState set the menu option to true so icons are visible
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Set the displayed info to match that of book object
     */
    private void updateBookDetails() {
        if (book != null) {
            bookTitleTextView.setText(book.getDescription().getTitle());
            bookStatusTextView.setText(BookStatus.AVAILABLE.toString());
            bookDescriptionTextView.setText(book.getDescription().getDescription());
            bookAuthorTextView.setText(book.getDescription().getAuthor());
            bookISBNTextVIew.setText(book.getDescription().getIsbn());
            bookOwnerTextView.setText(book.getOwner().getFullName());
            bookBorrowerTextView.setText(book.getOwner().getUsername());
            Picasso.get().load(book.getPhoto()).into(bookImage);
            // call check user function
            updateRequestControls();
        }
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view that the works was done in
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_book_details, container, false);
        bookTitleTextView = view.findViewById(R.id.bookViewTitle);
        bookStatusTextView = view.findViewById(R.id.bookViewStatus);
        bookDescriptionTextView = view.findViewById(R.id.bookViewDescription);
        bookAuthorTextView = view.findViewById(R.id.bookViewAuthor);
        bookISBNTextVIew = view.findViewById(R.id.bookViewISBN);
        bookOwnerTextView = view.findViewById(R.id.bookViewOwner);
        bookBorrowerTextView = view.findViewById(R.id.bookViewUsername);
        bookImage = view.findViewById(R.id.bookImge);
        requestBtn = view.findViewById(R.id.request_book);
        requestBtn.setVisibility(View.INVISIBLE);

        final String bookId = getArguments().getString(ARG_BOOK_ID);
        firestoreRef = FirebaseFirestore.getInstance();


        //create a pointer to book details
        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);

        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String buttonName = ((Button) view).getText().toString();
                if (buttonName.equals("VIEW REQUESTS")) {
                    Intent intent = new Intent(getActivity(), ViewRequestActivity.class);
                    final String bookId = getArguments().getString(ARG_BOOK_ID);
                    intent.putExtra("bookId", bookId);
                    startActivity(intent);
                } else {

                    if(buttonName.equals("RETURN BOOK")){
                        Intent intent = new Intent(getActivity(), MapsActivity.class);
                        final String bookId = getArguments().getString(ARG_BOOK_ID);
                        intent.putExtra("bookId",bookId);
                        intent.putExtra("borrow",1);
                        startActivity(intent);


                    }else{
                        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //create a pointer to user details
                        //final DocumentReference userReference = firestoreRef.collection("users").document(userId);

                        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);
                        //create a pointer to user details
                        final DocumentReference userReference = firestoreRef.collection("users").document(userId);

                        requestCollection = firestoreRef.collection("requests");
                        Query query = requestCollection.whereEqualTo("book", bookReference).whereEqualTo("requester",userReference);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    boolean b = task.getResult().isEmpty();

                                    if (b == true){
                                        //go ahead and send request
                                        makeRequest(requestCollection,bookId,userId);
                                    }else{
                                        //Request already sent by user,So decline
                                        Toast.makeText(getContext(),"Request Already Sent",Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }

            }
        });


        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        book = Book.getOrCreate(bookId);

        Book.documentOf(bookId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("BookActivity", "error getting book" + bookId.toString() +
                            ": " + error);
                } else if (value == null || !value.exists()) {
                    Log.w("BookActivity", "did not find the Book" + bookId.toString());
                } else {
                    book.load(value);
                    updateBookDetails();
                }
            }
        });
        return view;
    }

    /**
     * @param requestCollection
     * @param bookId
     * @param userId
     * allows user to make a request on a book they do not own
     */
    public void makeRequest(CollectionReference requestCollection, String bookId, String userId) {
        final DocumentReference bookReference = firestoreRef.collection("books").document(bookId);


        //create request object
        Book bookObject = Book.getOrCreate(bookId);
        User requester = User.getOrCreate(userId);

        //generate id
        String id = requestCollection.document().getId();
        long timeStamp = System.currentTimeMillis();

        Request requestObject = Request.getOrCreate(id);
        requestObject.setBook(bookObject);
        requestObject.setTimestamp(timeStamp);
        requestObject.setRequester(requester);
        requestObject.setStatus(RequestStatus.SENT);

        //Send Request Object to Firestore
        requestObject.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialogBox(book.getDescription().getTitle(),"Request Sent");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dialogBox(String message, final String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(Html.fromHtml("your request for "+ "<b><i>"+ message+"</i></b>"+" has been sent"))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_book_details, menu);
    }

    /**
     * switches to the appropriate view
     * and perform actions
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteBook:
                book.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        bookTitleTextView.setText(null);
                        bookStatusTextView.setText(null);
                        bookDescriptionTextView.setText(null);
                        bookAuthorTextView.setText(null);
                        bookISBNTextVIew.setText(null);
                        bookOwnerTextView.setText(null);
                        bookBorrowerTextView.setText(null);
                        book.setPhoto("http://abcd");
                        Picasso.get().load(book.getPhoto()).into(bookImage);
                        getParentFragmentManager().popBackStack();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),
                                R.string.book_deletion_failed,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return true;
            case R.id.editBookDetails:
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                final String bookId = getArguments().getString(ARG_BOOK_ID);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            case viewBookBack:
                getParentFragmentManager().popBackStack();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Check the state of the current user, book owner, and accepted requester
     * to show the appropriate request controls.
     */
    public void updateRequestControls() {
        User currentUser = User.getOrCreate(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (book.getOwner() == currentUser) {
            // we want to display view request button
            setHasOptionsMenu(true);
            requestBtn.setVisibility(View.VISIBLE);
            requestBtn.setText("VIEW REQUESTS");

        } else {
            if (currentUser == book.getAcceptedRequester()) {
                requestBtn.setVisibility(View.VISIBLE);
                requestBtn.setText("RETURN BOOK");
            }
            else{
                // display make request button
                requestBtn.setVisibility(View.VISIBLE);
                requestBtn.setText("REQUEST BOOK");
            }

        }
    }



}


