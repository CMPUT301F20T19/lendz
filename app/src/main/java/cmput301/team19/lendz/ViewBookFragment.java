package cmput301.team19.lendz;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

        if (getArguments() == null)
            throw new IllegalArgumentException("no arguments");

        final UUID bookId = UUID.fromString(getArguments().getString(ARG_BOOK_ID));
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
        updateBookDetails();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.view_book_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.editBookDetails:
                startEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startEdit(){
        Fragment editBookDetailsFragment = EditBookFragment.newInstance(
                book.getId(),
                book.getDescription().getTitle(),
                book.getDescription().getIsbn(),
                book.getDescription().getAuthor(),
                book.getDescription().getAuthor());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.slide_out,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
        );

        transaction.replace(R.id.book_FragmentView, editBookDetailsFragment);
        transaction.addToBackStack(null);

        transaction.commit();

    }
}
