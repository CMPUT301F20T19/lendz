package cmput301.team19.lendz;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.UUID;

public class ViewBooksActivity extends AppCompatActivity {
    private static final String TAG = "ViewBooksActivity";

    ArrayList<Book> availableBooks = new ArrayList<>();
    ArrayList<Book> requestedBooks = new ArrayList<>();
    ArrayList<Book> acceptedBooks = new ArrayList<>();
    ArrayList<Book> borrowedBooks = new ArrayList<>();

    private void loadBooks() {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User currentUser = User.getOrCreate(currentUserID);

        ArrayList<UUID> bookIDs = new ArrayList<>();
        bookIDs.addAll(currentUser.getOwnedBookIds());
        bookIDs.addAll(currentUser.getBorrowedBookIds());

        // declaring variables for using inside for loop
        BookStatus bookStatus;
        RequestStatus bookRequestStatus;

        for (int i = 0; i < bookIDs.size(); i++) {
            final UUID bookID = bookIDs.get(i);
            final Book book = Book.getOrCreate(bookID);

            Book.documentOf(bookID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e(TAG,
                                "error getting book with ID " + bookID.toString() + ": " + error);
                    } else if (value == null || !value.exists()) {
                        Log.w(TAG,
                                "didn't find book with ID " + bookID.toString());
                    } else {
                        book.load(value);
                    }
                }
            });

            //sorting books
            bookStatus = book.getStatus();
            bookRequestStatus = book.getAcceptedRequest().getStatus();

            if (bookStatus == BookStatus.BORROWED) {
                borrowedBooks.add(book);
            } else if (bookStatus == BookStatus.AVAILABLE) {
                availableBooks.add(book);
            } else if (bookRequestStatus == RequestStatus.SENT) {
                requestedBooks.add(book);
            } else if (bookRequestStatus == RequestStatus.ACCEPTED) {
                acceptedBooks.add(book);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vew_books);

        ArrayList<ViewBooksSection> sections = new ArrayList<>();
        loadBooks();
        if (availableBooks.size() > 0) {
            sections.add(new ViewBooksSection("Available Books", availableBooks)); }
        if (requestedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Requested Books", requestedBooks)); }
        if (acceptedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Accepted Books", acceptedBooks)); }
        if (borrowedBooks.size() > 0) {
            sections.add(new ViewBooksSection("Borrowed Books", borrowedBooks)); }

        RecyclerView viewBooksRecyclerView = findViewById(R.id.book_list);
        ViewBooksAdapter viewBooksAdapter = new ViewBooksAdapter(this, sections);
        viewBooksRecyclerView.setAdapter(viewBooksAdapter);
        viewBooksRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
