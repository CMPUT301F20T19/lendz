package cmput301.team19.lendz;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.UUID;

public class EditBookFragment extends Fragment implements View.OnClickListener{
    ImageView imgView;
    Button selectImg;
    Button scanBtn;
    Button saveBtn;
    TextView isbnTv;
    Uri FilePathUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore firestoreRef;

    private EditText bookISBNEditText;
    private EditText bookTitleEditText;
    private EditText bookAuthorEditText;
    private EditText bookDescriptionEditText;

    private UUID bookId;

    private static final int IMAGE_PICK_CODE = 1000 ;
    private static final int PERMISSION_CODE = 1001;
    private static final String ARG_BOOK_ID = "bookId";



    public static EditBookFragment newInstance(UUID id) {
        EditBookFragment fragment = new EditBookFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, id.toString());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_addbook, container, false);
        //Attach views
        imgView = view.findViewById(R.id.book_IV);
        selectImg = view.findViewById(R.id.addImg);

        scanBtn = view.findViewById(R.id.scanBTN);
        isbnTv = view.findViewById(R.id.ISBN_ID);
        saveBtn = view.findViewById((R.id.save_id));

        bookISBNEditText = view.findViewById(R.id.editBookDetailsISBN);
        bookTitleEditText = view.findViewById(R.id.editBookDetailsTitle);
        bookAuthorEditText = view.findViewById(R.id.editBookAuthor);
        bookDescriptionEditText = view.findViewById(R.id.editBookDescription);

        if (getArguments() != null) {
            bookId = UUID.fromString(getArguments().getString(ARG_BOOK_ID));

            final Book book = Book.getOrCreate(bookId);
            Book.documentOf(bookId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    book.load(documentSnapshot);
                    bookISBNEditText.setText(book.getDescription().getIsbn());
                    bookTitleEditText.setText(book.getDescription().getTitle());
                    bookAuthorEditText.setText(book.getDescription().getTitle());
                    bookDescriptionEditText.setText(book.getDescription().getDescription());
                }
            });
        } else {
            bookId = ha;



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBook();
            }
        });
        scanBtn.setOnClickListener(this);
        //handle selectImg btn click
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check for permission
//                Log.d("STATE","WOOOW");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

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
        return view;
    }

}
    private void pickImageFromGallery(){
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }

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
                    Toast.makeText(getContext(),"permission denied...!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v){
        IntentIntegrator integrator  = new IntentIntegrator(getActivity());
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt("Scan a barcode or QR");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imgView.setImageURI(data.getData());
            //hold image data temporarily
            FilePathUri =   data.getData();
        }
        IntentResult result =  IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){

            if (result.getContents() == null){

                Toast.makeText(getContext(),"CANCELLED",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getContext(),result.getContents(),Toast.LENGTH_LONG).show();
                isbnTv.setText(result.getContents());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void uploadBook() {
        if (FilePathUri != null) {

            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            firestoreRef = FirebaseFirestore.getInstance();
            //get reference to book collection;

            final CollectionReference BookCollection = firestoreRef.collection("books");
            //generate a unique id for each book document. Images stored in firebase storage will use the same id
            final String id = BookCollection.document().getId();
            Log.d("BOOK ID IS ",id);

            final StorageReference ref = storageReference.child("BookImages/"+id);
            ref.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditBookFragment.this.getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri bookImgUrl) {
                                   String url =  bookImgUrl.toString();
                                    Toast.makeText(EditBookFragment.this.getContext(),url, Toast.LENGTH_SHORT).show();
                                    //SEND TO FIRESTORE
                                    sendToFirestore(BookCollection,id,url);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditBookFragment.this.getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }
    public void sendToFirestore(CollectionReference BookCollection,String id,String url){
        BookDescription createdBook = new BookDescription("ISBN","TITLE","AUTHOR","DESCRIPTION");
        BookCollection.document(id).set(createdBook)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(EditBookFragment.this.getContext(), "Book Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditBookFragment.this.getContext(), "Failed to add Book", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
