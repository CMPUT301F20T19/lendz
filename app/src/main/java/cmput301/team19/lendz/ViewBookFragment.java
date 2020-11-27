package cmput301.team19.lendz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

/**
 * Fragment for viewing book details information
 * Used in BookActivity
 */
public class ViewBookFragment extends Fragment {
    // Parameter names
    private static final String ARG_BOOK_ID = "bookId";

    private View view;

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
        if (book == null || !isVisible()) {
            return;
        }

        bookTitleTextView.setText(book.getDescription().getTitle());
        bookStatusTextView.setText(book.getStatus().toString(getResources()));
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

        book = Book.getOrCreate(getArguments().getString("bookId"));

        Book.documentOf(book.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && error == null) {
                    book.load(value);
                    updateBookDetails();
                } else {
                    Log.e("BookActivity", "error getting book" + book.getId() +
                            ": " + error);
                }
            }
        });

        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        Button requestBtn = view.findViewById(R.id.request_button);
        requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (book.getPendingRequesters().contains(User.getCurrentUser())) {
                    Toast.makeText(
                            getContext(),
                            R.string.already_sent_a_request,
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    makeRequest();
                }
            }
        });

        Button viewRequestsBtn = view.findViewById(R.id.view_requests_button);
        viewRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewRequestActivity.class);
                intent.putExtra("bookId", book.getId());
                startActivity(intent);
            }
        });

        Button viewLocationBtn = view.findViewById(R.id.view_location_button);
        viewLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Request request = book.getAcceptedRequest();
                request.getDocumentReference().get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                request.load(documentSnapshot);
                                Location location = request.getLocation();
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"
                                        + location.getLat() + "," + location.getLon()
                                        + "?z=15" + (location.getAddress() != null
                                        ? ("&q=" + location.getAddress().replace(' ', '+'))
                                        : "")));
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        Button confirmPickUpOrReturnButton =
                view.findViewById(R.id.confirm_pick_up_or_return_button);
        confirmPickUpOrReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator =
                        IntentIntegrator.forSupportFragment(ViewBookFragment.this);
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.setPrompt(getString(R.string.scan_the_barcode));
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });
        return view;
    }

    /**
     * allows user to make a request on a book they do not own
     */
    public void makeRequest() {
        //generate id
        String id = FirebaseFirestore.getInstance()
                .collection("requests").document().getId();
        long timeStamp = System.currentTimeMillis();

        Request request = Request.getOrCreate(id);
        request.setBook(book);
        request.setTimestamp(timeStamp);
        request.setRequester(User.getCurrentUser());
        request.setStatus(RequestStatus.SENT);

        //Send Request Object to Firestore
        request.store()
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

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (book == null) {
            return;
        }

        if (book.getOwner() == User.getCurrentUser()) {
            // Viewing as owner of this book
            menu.setGroupVisible(R.id.view_book_menu_for_owners, true);
        }
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
        view.findViewById(R.id.book_accepted_or_borrowed).setVisibility(View.GONE);
        view.findViewById(R.id.book_borrowed_by_someone_else).setVisibility(View.GONE);
        view.findViewById(R.id.request_button).setEnabled(true);
        view.findViewById(R.id.pending_request_exists_textview).setVisibility(View.GONE);

        if (book.getStatus() == BookStatus.AVAILABLE || book.getStatus() == BookStatus.REQUESTED) {
            if (book.getOwner() == User.getCurrentUser()) {
                view.findViewById(R.id.owner_book_available).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.borrower_book_available).setVisibility(View.VISIBLE);
                for (User requester : book.getPendingRequesters()) {
                    if (requester == User.getCurrentUser()) {
                        // Current user has a pending request for this book
                        view.findViewById(R.id.pending_request_exists_textview)
                                .setVisibility(View.VISIBLE);
                        view.findViewById(R.id.request_button).setEnabled(false);
                        break;
                    }
                }
            }
            return;
        }

        if (book.getStatus() != BookStatus.ACCEPTED && book.getStatus() != BookStatus.BORROWED) {
            return;
        }

        // Book is accepted or borrowed
        if (book.getOwner() == User.getCurrentUser()
                || book.getAcceptedRequester() == User.getCurrentUser()) {
            // Viewing as owner or borrower of this book
            showAcceptedOrBorrowedControls();
        } else {
            // Viewing as someone else
            view.findViewById(R.id.book_borrowed_by_someone_else).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Shows the request controls for the owner or borrower of a book
     * for when it is accepted or borrowed.
     */
    private void showAcceptedOrBorrowedControls() {
        // Show the book_accepted_or_borrowed layout
        view.findViewById(R.id.book_accepted_or_borrowed).setVisibility(View.VISIBLE);

        // Set text of confirm button
        Button confirmPickUpOrReturnButton =
                view.findViewById(R.id.confirm_pick_up_or_return_button);
        if (book.getStatus() == BookStatus.ACCEPTED) {
            confirmPickUpOrReturnButton.setText(R.string.confirm_pick_up);
        } else if (book.getStatus() == BookStatus.BORROWED) {
            confirmPickUpOrReturnButton.setText(R.string.confirm_return);
        }

        // Set enabled state of confirm button
        if (book.getAcceptedRequester() == User.getCurrentUser()) {
            confirmPickUpOrReturnButton.setEnabled(!book.isBorrowerScanned());
        } else if (book.getOwner() == User.getCurrentUser()) {
            confirmPickUpOrReturnButton.setEnabled(!book.isOwnerScanned());
        }

        // Set text of request_status TextView
        TextView requestStatus = view.findViewById(R.id.request_status);
        String requesterUsername = String.valueOf(book.getAcceptedRequesterUsername());
        String statusString;
        if (book.getAcceptedRequester() == User.getCurrentUser()) {
            if (book.getStatus() == BookStatus.ACCEPTED) {
                requestStatus.setText(R.string.your_request_was_accepted);
            } else {
                requestStatus.setText(R.string.you_are_borrowing_this_book);
            }
        } else if (book.getOwner() == User.getCurrentUser()) {
            // Create link to accepted requester in status
            statusString = getResources().getString(
                    book.getStatus() == BookStatus.ACCEPTED
                            ? R.string.you_accepted_a_request
                            : R.string.your_book_is_borrowed,
                    requesterUsername);
            SpannableString ss = new SpannableString(statusString);
            ClickableSpan requesterSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Fragment profileFragment = ViewUserProfileFragment.newInstance(book.getAcceptedRequester().getId());
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
            };
            int startIndex = ss.length() - requesterUsername.length();
            int endIndex = ss.length();
            ss.setSpan(requesterSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            ss.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            requestStatus.setText(ss);
            requestStatus.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String isbn = result.getContents();
            if (isbn.equals(book.getDescription().getIsbn())) {
                Task<Void> task;
                if (book.getOwner() == User.getCurrentUser()) {
                     task = book.notifyOwnerDidScan();
                } else {
                    task = book.notifyBorrowerDidScan();
                }
                task.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ViewBookFragment", "scan successful");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ViewBookFragment", "scan failed", e);
                    }
                });
            } else {
                Toast.makeText(getContext(),
                        R.string.isbn_does_not_match,
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}


