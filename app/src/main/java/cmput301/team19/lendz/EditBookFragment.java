package cmput301.team19.lendz;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class EditBookFragment extends Fragment {
    ImageView imgView;
    Button selectImg;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private static final String ARG_BOOK_ID = "bookId";

    private UUID bookId;

    private EditText bookISBNEditText;
    private EditText bookTitleEditText;
    private EditText bookAuthorEditText;
    private EditText bookDescriptionEditText;


//    public static EditBookFragment newInstance(String id) {
//        EditBookFragment fragment = new EditBookFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_BOOK_ID, id.toString());
//        fragment.setArguments(args);
//        return fragment;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_addbook, container, false);

        bookISBNEditText = view.findViewById(R.id.editBookDetailsISBN);
        bookTitleEditText = view.findViewById(R.id.editBookDetailsTitle);
        bookAuthorEditText = view.findViewById(R.id.editBookAuthor);
        bookDescriptionEditText = view.findViewById(R.id.editBookDescription);

        if (getArguments() != null) {
            bookId = UUID.fromString(getArguments().getString(ARG_BOOK_ID));

//            Book book = Book.getOrCreate(bookId);

//            bookISBNEditText.setText(book.getDescription().getIsbn());
//            bookTitleEditText.setText(book.getDescription().getTitle());
//            bookAuthorEditText.setText(book.getDescription().getTitle());
//            bookDescriptionEditText.setText(book.getDescription().getDescription());
        }

        //Attach views
//        imgView = view.findViewById(R.id.book_IV);
//        selectImg = view.findViewById(R.id.edit_BookImage);

        //handle selectImg btn click
//        selectImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //check for permission
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//
//                        //permission not granted,request it
//                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
//
//                        //SHOW permission popup
//                        requestPermissions(permissions, PERMISSION_CODE);
//
//                    } else {
//
//                        //permission already granted
//                        pickImageFromGallery();
//                    }
//                } else {
//                    //system os is less than marshmallow
//                    pickImageFromGallery();
//                }
//            }
//        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_book_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveBookDetails:
//                saveBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Save the edited book details to Firestore.
     */
//    private void saveBook() {
//        Book book = Book.getOrCreate(bookId);
//
//        // Set Book data based on contents of editing controls
//        BookDescription bookDescription = new BookDescription(bookISBNEditText.getText().toString(),
//                bookTitleEditText.getText().toString(),
//                bookAuthorEditText.getText().toString(),
//                bookDescriptionEditText.getText().toString());
//
//        book.setDescription(bookDescription);
//
//        // Save changes to Firestore
//        book.store()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        getFragmentManager().popBackStack();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(),
//                        R.string.book_edit_failed,
//                        Toast.LENGTH_LONG)
//                        .show();
//            }
//        });
//    }



}
