package cmput301.team19.lendz;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

/**
 * Activity where a book object is created/edited and sent to Firestore.
 **/

public class EditBookActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView photoImgView;
    TextView tapToAddPhotoTextView;
    Button scanBtn;
    ImageButton deletePhotoImgBtn;
    EditText isbnEditText;
    EditText titleEditText;
    EditText authorEditText;
    EditText descriptionEditText;

    Uri photoToUploadUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean shouldDeletePhoto = false;

    Book book;
    boolean isNewBook;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        //Attach views
        photoImgView = findViewById(R.id.photo_imageview);
        tapToAddPhotoTextView = findViewById(R.id.tap_to_add_photo_textview);
        scanBtn = findViewById(R.id.scan_button);
        isbnEditText = findViewById(R.id.isbn_edittext);
        titleEditText = findViewById(R.id.title_edittext);
        authorEditText = findViewById(R.id.author_edittext);
        descriptionEditText = findViewById(R.id.description_edittext);
        deletePhotoImgBtn = findViewById(R.id.delete_photo_imagebutton);

        //some extra code
        Intent intent = getIntent();

        isNewBook = !intent.hasExtra("bookId");
        if (isNewBook) {
            String id = FirebaseFirestore.getInstance()
                    .collection("books").document().getId();
            book = Book.getOrCreate(id);
        } else {
            String id = intent.getStringExtra("bookId");
            book = Book.getOrCreate(id);
            // fetch book with id from firebase
            Book.documentOf(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    book.load(documentSnapshot);
                    isbnEditText.setText(book.getDescription().getIsbn());
                    titleEditText.setText(book.getDescription().getTitle());
                    authorEditText.setText(book.getDescription().getAuthor());
                    descriptionEditText.setText(book.getDescription().getDescription());
                    if (book.getPhoto() != null) {
                        Picasso.get().load(book.getPhoto()).into(photoImgView);
                        deletePhotoImgBtn.setVisibility(View.VISIBLE);
                        tapToAddPhotoTextView.setVisibility(View.GONE);
                    }
                }
            });
        }

        scanBtn.setOnClickListener(this);
        deletePhotoImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //remove image from image view
                shouldDeletePhoto = true;
                photoToUploadUri = null;
                photoImgView.setImageDrawable(null);
                deletePhotoImgBtn.setVisibility(View.GONE);
                tapToAddPhotoTextView.setVisibility(View.VISIBLE);
            }
        });

        //handle selectImg btn click
        photoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                        //permission not granted,request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        //SHOW permission popup
                        requestPermissions(permissions, PERMISSION_CODE);

                    } else {

                        //permission already granted
                        pickImageFromGallery();
                    }
                } else {
                    //system os is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    /**
     * checks for external storage permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    pickImageFromGallery();
                } else {
                    //permission denied
                    Toast.makeText(this, "permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Triggers the barcode scanner to open
     */
    @Override
    public void onClick(View v) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt("Scan a barcode or QR");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    /**
     * Handles result from image picker and ScanActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            photoImgView.setImageURI(data.getData());
            //hold image data temporarily
            photoToUploadUri = data.getData();
            deletePhotoImgBtn.setVisibility(View.VISIBLE);
            tapToAddPhotoTextView.setVisibility(View.GONE);
            shouldDeletePhoto = false;
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String isbn = result.getContents();
            isbnEditText.setText(isbn);
            BookDescription.loadFromInternet(isbn, this, new BookDescription.BookDescriptionLoadListener() {
                @Override
                public void onSuccess(BookDescription bookDescription) {
                    titleEditText.setText(bookDescription.getTitle());
                    authorEditText.setText(bookDescription.getAuthor());
                    descriptionEditText.setText(bookDescription.getDescription());
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditBookActivity.this,
                            getResources().getString(R.string.failed_to_get_book_description,
                                    e.getMessage()),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Triggered by the save button.
     * receives an image uri and converts it to a downloadable image link
     * calls the function "sendToFirestore"
     */
    public void uploadBook() {
        if (photoToUploadUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("BookImages/" + book.getId());
            ref.putFile(photoToUploadUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri bookImgUrl) {
                                    book.setPhoto(bookImgUrl.toString());
                                    sendToFirestore();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditBookActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            Log.d("PROGRESS IS ... ", String.valueOf(progress));

                            progressDialog.setMessage("Uploaded " + (int) progress + "%");

                        }
                    });
        } else {
            sendToFirestore();
        }
    }

    /**
     * takes bookCollection reference and book id as arguments
     * creates a book object and optionally removes a photo attached to a bookObject in Firestore.
     */
    public void sendToFirestore() {
        //get text from textViews
        String isbn = isbnEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String author = authorEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // Create keywords from title, author, and description
        // Splits by whitespace, removes all non-alpha characters
        String[] keywords = (title + " " + author + " " + description)
                .toLowerCase()
                .replaceAll("[^a-zA-Z ]", "")
                .split("\\s+");

        //check if any text field is empty
        if (TextUtils.isEmpty(title)) {
            titleEditText.setError("TextField Cannot be Empty");
            return;
        }
        if (TextUtils.isEmpty(isbn)) {
            isbnEditText.setError("TextField Cannot be Empty");
            return;
        }
        if (TextUtils.isEmpty(author)) {
            authorEditText.setError("TextField Cannot be Empty");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("TextField Cannot be Empty");
            return;
        }

        //DESCRIPTION
        BookDescription createdBook = new BookDescription(isbn, title, author, description);
        book.setDescription(createdBook);

        //construct book object
        book.setOwner(User.getCurrentUser());
        book.setKeywords(Arrays.asList(keywords));
        if (shouldDeletePhoto) {
            final StorageReference ref = storageReference.child("BookImages/" + book.getId());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e("Picture", "#deleted");
                    book.setPhoto(null);
                    saveBook();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // This means that a book is being edited or added without the picture being deleted
            saveBook();
        }
    }

    /**
     * takes a bookobject as argument and sends it to firebase.
     */
    public void saveBook() {
        book.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditBookActivity.this, "Failed to add Book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_book_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_book_details:
                uploadBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
