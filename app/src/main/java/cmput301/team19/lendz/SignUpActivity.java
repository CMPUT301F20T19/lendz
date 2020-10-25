package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText usernameEditText;
    private EditText fullnameEditText;
    private EditText emailEditText;
    private EditText passWordEditText;
    private EditText phoneNumberEditText;



    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.signUp_button);
        usernameEditText = findViewById(R.id.editText_signup_username);
        passWordEditText = findViewById(R.id.editText_signup_password);
        fullnameEditText = findViewById(R.id.editText_signup_full_name);
        phoneNumberEditText = findViewById(R.id.editText_signup_phone_number);
        emailEditText = findViewById(R.id.editText_signup_email);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference users = db.collection("users");

        setImageView();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //signUp();
                passWordEditText = findViewById(R.id.editText_signup_password);
                emailEditText = findViewById(R.id.editText_signup_email);
                final FirebaseAuth mFirebaseAuth;
                mFirebaseAuth = FirebaseAuth.getInstance();
                final String email = emailEditText.getText().toString();
                String pwd = passWordEditText.getText().toString();
                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {

                                    Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful, Try again!", Toast.LENGTH_LONG).show();
                                    Log.e("Err","error",task.getException());
                                }
                                else {
                                    FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
                                    //assert Fuser != null;
                                    String uid = Fuser.getUid();
                                    User user = User.getOrCreate(uid);
                                    String username = usernameEditText.getText().toString();
                                    String fullname = fullnameEditText.getText().toString();
                                    String phonenumber = phoneNumberEditText.getText().toString();
                                    String emailStr = emailEditText.getText().toString();


                                    if(username.isEmpty()||fullname.isEmpty()||phonenumber.isEmpty()||emailStr.isEmpty())
                                    {
                                        Toast.makeText(SignUpActivity.this, "One or more Fields is empty", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        user.setUsername(username);
                                        user.setFullName(fullname);
                                        user.setPhoneNumber(phonenumber);
                                        user.setEmail(emailStr);

                                        // Save changes to Firestore
                                        user.store()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //getFragmentManager().popBackStack();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this,"Unable to save user profile", Toast.LENGTH_LONG).show();
                                                Log.e("err","err",e);
                                            }
                                        });
                                    }
                                    Toast.makeText(SignUpActivity.this, "SignUp successful", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
           }
        });

    }



    /**
     * sets image view to an image stored in firestore
     */
    public void setImageView() {
        imageView = findViewById(R.id.signup_app_logo);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String imageUrl = "gs://lendz-7eb71.appspot.com/appLogo.png";
        storage.getReferenceFromUrl(imageUrl)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>()
                      {
                          @Override
                          public void onSuccess(Uri uri) {
                              Picasso.with(SignUpActivity.this).load(uri).into(imageView);
                          }
                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error","Did not work",e);
                    }
                });

    }

    /**
     * this handles signing  up with firebase authenticator
     */
    public void signUp() {
        passWordEditText = findViewById(R.id.editText_signup_password);
        emailEditText = findViewById(R.id.editText_signup_email);
        FirebaseAuth mFirebaseAuth;
        mFirebaseAuth = FirebaseAuth.getInstance();
        String email = emailEditText.getText().toString();
        String pwd = passWordEditText.getText().toString();
        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful, Try again!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "SignUp successful", Toast.LENGTH_LONG).show();
                        }
                    }
                });




//        user.setUsername(usernameEditText.getText().toString());
//        user.setFullName(fullnameEditText.getText().toString());
//        user.setPhoneNumber(phoneNumberEditText.getText().toString());
//        user.setEmail(emailEditText.getText().toString());
//
//        //Doing Authentication
//        FirebaseAuth mFirebaseAuth;
//        mFirebaseAuth = FirebaseAuth.getInstance();
//        authenticate(mFirebaseAuth,user);
//
//        //Saving the  user profile to firestore
//        saveProfile(users,userID,user);
    }

//    /**
//     * this saves the user profile to firestore
//     * @param users
//     * this is the data base collection reference
//     * @param userID
//     * this is the unique user id (UUID)
//     * @param user
//     * this is the User object
//     */
//    public void saveProfile(CollectionReference users, UUID userID, User user ){
//            users.document(userID.toString()).set(user.toData())
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(SignUpActivity.this,"User profile saved ", Toast.LENGTH_LONG).show();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(SignUpActivity.this,"User profile not saved ", Toast.LENGTH_LONG).show();
//                }
//            });
//    }
//
//    /**
//     * this handles authentication and sends user profile to home page
//      * @param mFirebaseAuth
//     * this is an inbuilt firestore class that handles authentication
//     * @param user
//     * this is the user to  be sent
//     */
//
//    public void authenticate(FirebaseAuth mFirebaseAuth, final User user)
//    {
//        String email = emailEditText.getText().toString();
//        String pwd = passWordEditText.getText().toString();
//        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
//                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (!task.isSuccessful()) {
//                            Toast.makeText(SignUpActivity.this, "SignUp Unsuccessful, Try again!", Toast.LENGTH_LONG).show();
//                        }
//                        else {
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("user", (Serializable) user);
//                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//                            intent.putExtras(bundle);
//                            startActivity(intent);
//                        }
//                    }
//                });
//
//    }
}


