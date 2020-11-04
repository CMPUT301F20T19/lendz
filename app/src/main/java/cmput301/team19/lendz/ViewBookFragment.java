package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.UUID;

/**
 * Fragment for viewing book details information
 * Used in BookActivity
 */
public class ViewBookFragment extends Fragment {
    // Parameter names
    private static final String ARG_BOOK_ID = "bookId";

    private Book book;

    private TextView bookTitleTextView, bookStatusTextView, bookDescriptionTextView, bookAuthorTextView,
    bookISBNTextVIew, bookOwnerTextView, bookBorrowerTextView;

    private ImageView bookImage, ownerImage;

    public static ViewBookFragment newInstance(String bookId){
        ViewBookFragment fragment = new ViewBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Set the displayed info to match that of book object
     */
    private void updateBookDetails(){
        if (book != null){
            bookTitleTextView.setText(book.getDescription().getTitle());
            bookStatusTextView.setText(BookStatus.AVAILABLE.toString());
            bookDescriptionTextView.setText(book.getDescription().getDescription());
            bookAuthorTextView.setText(book.getDescription().getAuthor());
            bookISBNTextVIew.setText(book.getDescription().getIsbn());
            bookOwnerTextView.setText(book.getOwner().getFullName());
            bookBorrowerTextView.setText(book.getOwner().getUsername());
            Picasso.get().load(book.getPhoto()).into(bookImage);
        }
    }

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

        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        final String bookId = getArguments().getString(ARG_BOOK_ID);
        book = Book.getOrCreate(bookId);

        Book.documentOf(bookId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Log.e("BookActivity", "error getting book" + bookId.toString() +
                            ": " + error);
                } else if (value == null || !value.exists()){
                    Log.w("BookActivity", "did not find the Book" + bookId.toString());
                } else {
                    book.load(value);
                    updateBookDetails();
                }
            }
        });
       return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_book_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                        getParentFragmentManager().popBackStack();;
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
                //Toast.makeText(getContext(),  bookId ,Toast.LENGTH_SHORT).show();
                intent.putExtra("bookId", bookId);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
