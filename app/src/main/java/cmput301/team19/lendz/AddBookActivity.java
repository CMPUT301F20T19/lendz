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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
 *Activity where a book object is created and sent to firestore
 * it also implements a view.Onclicklistener which opens a barcode scanner when the scan Button is pressed
 */

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imgView;
    Button selectImg;
    Button scanBtn;
    Button del_Img;
    Button saveBtn;
    EditText isbnTv;
    EditText  titleTv;
    EditText  authorTV;
    EditText  descriptionTV;

    Uri FilePathUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore firestoreRef;
    String id;
    String existingBookId;
    String tempId;
    String url;
    User user;
    int triggerDelete = 0;

    private static final int IMAGE_PICK_CODE = 1000 ;
    private static final int PERMISSION_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);
        //Attach views
        imgView = findViewById(R.id.book_IV);
        selectImg = findViewById(R.id.addImg);
        scanBtn = findViewById(R.id.scanBTN);
        isbnTv = findViewById(R.id.ISBN_ID);
        titleTv = findViewById(R.id.title_id);
        authorTV = findViewById(R.id.author_id);
        descriptionTV = findViewById(R.id.description_id);
        saveBtn = findViewById((R.id.save_id));
        del_Img = findViewById(R.id.delImg);

        //some extra code
        Intent intent = getIntent();


        if(intent.hasExtra("bookId")){
            existingBookId = intent.getStringExtra("bookId");
        }
//        fetch book with parsed id from firebase;
        if (existingBookId != null){
            final Book book = Book.getOrCreate(existingBookId);
            Book.documentOf(existingBookId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    book.load(documentSnapshot);
                    isbnTv.setText(book.getDescription().getIsbn());
                    titleTv.setText(book.getDescription().getTitle());
                    authorTV.setText(book.getDescription().getAuthor());
                    descriptionTV.setText(book.getDescription().getDescription());
                    if (book.getPhoto() != null){
                        Picasso.get().load(book.getPhoto()).into(imgView);
                    }
                }
            });
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBook();
            }
        });
        scanBtn.setOnClickListener(this);
        del_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerDelete = 1;
                url = null ;
                //remove image from image view
                final Book book = Book.getOrCreate(existingBookId);
                book.setPhoto("http://abcd");
                Picasso.get().load(book.getPhoto()).into(imgView);
                imgView.setImageURI(null);

            }
        });



        //handle selectImg btn click
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                        //permission not granted,request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

                        //SHOW permission popup
                        requestPermissions(permissions,PERMISSION_CODE);

                    }else{

                        //permission already granted
                        pickImageFromGallery();
                    }
                }else{
                    //system os is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });
    }


    /**
     *
     */
    private void pickImageFromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

    /**
     *checks for external storage permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    pickImageFromGallery();
                }else{
                    //permission denied
                    Toast.makeText(this,"permission denied...!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    /**
     *Triggers the barcode scanner to open
     */
    @Override
    public void onClick(View v){
        IntentIntegrator integrator  = new IntentIntegrator(this);
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
            imgView.setImageURI(data.getData());
            //hold image data temporarily
            FilePathUri = data.getData();
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String isbn = result.getContents();
            isbnTv.setText(isbn);
            BookDescription.loadFromInternet(isbn, this, new BookDescription.BookDescriptionLoadListener() {
                @Override
                public void onSuccess(BookDescription bookDescription) {
                    titleTv.setText(bookDescription.getTitle());
                    authorTV.setText(bookDescription.getAuthor());
                    descriptionTV.setText(bookDescription.getDescription());
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddBookActivity.this,
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
     *Triggered by the save button.
     * receives an image uri and converts it to a downloadable image link
     * calls the function "sendToFirestore"
     *
     */
    public void uploadBook() {
        firestoreRef = FirebaseFirestore.getInstance();

        //get reference to book collection;
        final CollectionReference BookCollection = firestoreRef.collection("books");

        //generate a unique id for each book document. Images stored in firebase storage will use the same id
        id = BookCollection.document().getId();

        if (FilePathUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            if (tempId != null){
                id = tempId;
            }
            //check if existingBookId is null
            if (existingBookId != null){
                //change is made to existing book
                id = existingBookId;
            }

            final StorageReference ref = storageReference.child("BookImages/"+id);
            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddBookActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri bookImgUrl) {
                                    url =  bookImgUrl.toString();
                                    //SEND TO FIRESTORE
                                    sendToFirestore(BookCollection,id);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddBookActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            Log.d("PROGRESS IS ... ",String.valueOf(progress));

                            progressDialog.setMessage("Uploaded "+(int)progress+"%");

                        }
                    });
        }else{
            if (tempId != null){
                id = tempId;
            }

            //check if existingBookId is null
            if (existingBookId != null){
                //change is made to existing book
                id = existingBookId;
            }
            sendToFirestore(BookCollection,id);
        }
    }

    /**
     *takes bookCollection reference and book id as arguments
     *creates a book object and optionally removes a photo attached to a bookObject in Firestore.
     */
    public void sendToFirestore(final CollectionReference BookCollection, final String id){
        tempId = id;
        //get text from textViews
        String isbn = isbnTv.getText().toString();


        String description = descriptionTV.getText().toString();
        // Creating keywords out from description using whitespace as delimiters
        String[] strArray = description.split(" ");
        for (int i = 0; i < strArray.length; i++) {
            strArray[i] = strArray[i].toLowerCase();
        }

        String title = titleTv.getText().toString();
        String author = authorTV.getText().toString();

        //check if any text field is empty
        if(TextUtils.isEmpty(title)){
            titleTv.setError("TextField Cannot be Empty");
            return;
        }
        if(TextUtils.isEmpty(isbn)){
            isbnTv.setError("TextField Cannot be Empty");
            return;
        }
        if(TextUtils.isEmpty(author)){
            authorTV.setError("TextField Cannot be Empty");
            return;
        }
        if(TextUtils.isEmpty(description)){
            descriptionTV.setError("TextField Cannot be Empty");
            return;
        }

        if((isbnTv.getError() == null) && (descriptionTV.getError() == null) && (titleTv.getError() == null) && (authorTV.getError() == null)){
            //get Firebase User id

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            user = User.getOrCreate(userId);
            //construct book object
            final Book bookObject = Book.getOrCreate(id);
            bookObject.setOwner(user);
            bookObject.setKeywords(Arrays.asList(strArray));
            if (url != null){
                bookObject.setPhoto(url);
            }
            if(triggerDelete == 1){
                final Book book = Book.getOrCreate(existingBookId);


                final StorageReference ref = storageReference.child("BookImages/"+id);
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Picture","#deleted");

                        bookObject.setPhoto(null);
                        saveBook(bookObject);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                triggerDelete = 0;
            }
            bookObject.setStatus(BookStatus.AVAILABLE);
            //DESCRIPTION
            BookDescription createdBook = new BookDescription(isbn,title,author,description);
            bookObject.setDescription(createdBook);

            if (triggerDelete == 0){
                // This means that a book is being edited or added without the picture being deleted
                saveBook(bookObject);
            }
        }
    }

    /**
     * takes a bookobject as argument and sends it to firebase.
     */
    public void saveBook(Book bookObject){
        bookObject.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FilePathUri = null;
                        Toast.makeText(AddBookActivity.this, "Book Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddBookActivity.this, "Failed to add Book", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
