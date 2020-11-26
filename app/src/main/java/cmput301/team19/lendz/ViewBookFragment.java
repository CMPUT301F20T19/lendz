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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

/**
 * Fragment for viewing book details information
 * Used in BookActivity
 */
public class ViewBookFragment extends Fragment {
    // Parameter names
    private static final String ARG_BOOK_ID = "bookId";

    private View view;

    private Menu menu;
    private TextView bookTitleTextView, bookStatusTextView, bookDescriptionTextView, bookAuthorTextView,
    bookISBNTextView;
    private ImageView bookImage;
    private Chip ownerButton;

    private Book book;

    public ViewBookFragment() {

    }

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
        setHasOptionsMenu(true);
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
            bookISBNTextView.setText(book.getDescription().getIsbn());
            ownerButton.setText(book.getOwnerUsername());
            ownerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment profileFragment = ViewUserProfileFragment.newInstance(book.getOwner().getId());
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(
                            R.anim.slide_in,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out
                    );

                    transaction.replace(R.id.container, profileFragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            });
            Picasso.get().load(book.getPhoto()).into(bookImage);
            // call check user function
            updateRequestControls();

            if (book.getOwner() == User.getCurrentUser()) {
                // Viewing as owner of this book
                menu.setGroupVisible(R.id.view_book_menu_for_owners, true);
            }
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
        view = inflater.inflate(R.layout.fragment_view_book_details, container, false);
        bookTitleTextView = view.findViewById(R.id.bookViewTitle);
        bookStatusTextView = view.findViewById(R.id.bookViewStatus);
        bookDescriptionTextView = view.findViewById(R.id.bookViewDescription);
        bookAuthorTextView = view.findViewById(R.id.bookViewAuthor);
        bookISBNTextView = view.findViewById(R.id.bookViewISBN);
        ownerButton = view.findViewById(R.id.owner_button);
        bookImage = view.findViewById(R.id.bookImage);

        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        final String bookId = getArguments().getString(ARG_BOOK_ID);
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

        Button requestBtn = view.findViewById(R.id.request_button);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                final String userId = User.getCurrentUser().getId();
                final DocumentReference bookReference = firestore.collection("books").document(bookId);
                //create a pointer to user details
                final DocumentReference userReference = firestore.collection("users").document(userId);

                final CollectionReference requestCollection = firestore.collection("requests");
                Query query = requestCollection.whereEqualTo("book", bookReference).whereEqualTo("requester", userReference);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            boolean b = task.getResult().isEmpty();

                            if (b == true) {
                                //go ahead and send request
                                makeRequest(requestCollection, userId);
                            } else {
                                //Request already sent by user,So decline
                                Toast.makeText(getContext(), "Request Already Sent", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button viewRequestsBtn = view.findViewById(R.id.view_requests_button);
        viewRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewRequestActivity.class);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
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
     * @param userId
     * allows user to make a request on a book they do not own
     */
    public void makeRequest(CollectionReference requestCollection, String userId) {
        //create request object
        User requester = User.getOrCreate(userId);

        //generate id
        String id = requestCollection.document().getId();
        long timeStamp = System.currentTimeMillis();

        Request requestObject = Request.getOrCreate(id);
        requestObject.setBook(book);
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
        this.menu = menu;
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
                        bookISBNTextView.setText(null);
                        ownerButton.setText(null);
                        ownerButton.setOnClickListener(null);
                        book.setPhoto(null);
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
                Intent intent = new Intent(getActivity(), EditBookActivity.class);
                final String bookId = getArguments().getString(ARG_BOOK_ID);
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Check the state of the current user, book owner, and accepted requester
     * to show the appropriate request controls.
     */
    public void updateRequestControls() {
        view.findViewById(R.id.borrower_book_available).setVisibility(View.GONE);
        view.findViewById(R.id.owner_book_available).setVisibility(View.GONE);
        view.findViewById(R.id.waiting_for_pick_up).setVisibility(View.GONE);

        if (book.getOwner() == User.getCurrentUser()) {
            // Viewing as owner of this book
            if (book.getStatus() == BookStatus.AVAILABLE
                    || book.getStatus() == BookStatus.REQUESTED) {
                view.findViewById(R.id.owner_book_available).setVisibility(View.VISIBLE);
            } else if (book.getStatus() == BookStatus.ACCEPTED) {
                // TODO
            } else if (book.getStatus() == BookStatus.BORROWED) {
                // TODO
            }
        } else {
            // Viewing as non-owner of this book
            if (book.getStatus() == BookStatus.AVAILABLE
                    || book.getStatus() == BookStatus.REQUESTED) {
                view.findViewById(R.id.borrower_book_available).setVisibility(View.VISIBLE);
            } else if (book.getStatus() == BookStatus.ACCEPTED) {
                // TODO
            } else if (book.getStatus() == BookStatus.BORROWED) {
                // TODO
            }
        }
    }
}


