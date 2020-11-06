package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

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
        setImageView();
        hidePassword();
        signUpBtn = findViewById(R.id.signUp_button);
        usernameEditText = findViewById(R.id.editText_signup_username);
        passWordEditText = findViewById(R.id.editText_signup_password);
        fullnameEditText = findViewById(R.id.editText_signup_full_name);
        phoneNumberEditText = findViewById(R.id.editText_signup_phone_number);
        emailEditText = findViewById(R.id.editText_signup_email);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                passWordEditText = findViewById(R.id.editText_signup_password);
                emailEditText = findViewById(R.id.editText_signup_email);
                String username = usernameEditText.getText().toString();
                String fullname = fullnameEditText.getText().toString();
                String phonenumber = phoneNumberEditText.getText().toString();
                final FirebaseAuth mFirebaseAuth= FirebaseAuth.getInstance();
                final String email = emailEditText.getText().toString();
                String pwd = passWordEditText.getText().toString();
                if(!isValidEmail(email))
                {
                    Toast.makeText(SignUpActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                }
                else if(!(phonenumber.length() == 10))
                {
                    Toast.makeText(SignUpActivity.this, "Invalid Phone Number", Toast.LENGTH_LONG).show();
                }
                else if(!(pwd.length() >= 6))
                {
                    Toast.makeText(SignUpActivity.this, "Password Must be atleast 6 characters ", Toast.LENGTH_LONG).show();
                }
                else if(username.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter UserName", Toast.LENGTH_LONG).show();
                }
                else if(fullname.isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter Full Name", Toast.LENGTH_LONG).show();
                }
                else {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                            .addOnCompleteListener(SignUpActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {

                                                Toast.makeText(SignUpActivity.this,task.getException().toString(), Toast.LENGTH_LONG).show();
                                                Log.e("Err","error",task.getException());
                                            }
                                            else {
                                                String uid = createUser(mFirebaseAuth);
                                                Toast.makeText(SignUpActivity.this, "SignUp successful", Toast.LENGTH_LONG).show();

                                                //Send Uid to Main Activity
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("userID",uid);
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);

                                            }
                                        }
                                    });
                }
            }
        });
    }

    /**
     * this creates user and stores to firebase
     * @param mFirebaseAuth
     * to get the current signed in user
     */
    public String createUser(FirebaseAuth mFirebaseAuth) {
        FirebaseUser Fuser = mFirebaseAuth.getCurrentUser();
        String uid = Fuser.getUid();
        User user = User.getOrCreate(uid);
        String username = usernameEditText.getText().toString();
        String fullname = fullnameEditText.getText().toString();
        String phonenumber = phoneNumberEditText.getText().toString();
        String emailStr = emailEditText.getText().toString();

        user.setUsername(username);
        user.setFullName(fullname);
        user.setPhoneNumber(phonenumber);
        user.setEmail(emailStr);

        // Save changes to Firestore
        storeUser(user);
        return uid;
    }

    /**
     * this stores the user firestore
     * @param user
     * user to be stored
     */
    public void storeUser(User user )
    {
        user.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUpActivity.this, "User profile Saved ", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Unable to save user profile", Toast.LENGTH_LONG).show();
                Log.e("err", "err", e);
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
                              Picasso.get().load(uri).into(imageView);
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
     * this method validates email
     * @param target
     * the email to be validated
     * @return
     * returns true if valid
     */
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



    /**
     * method that handles hiding password
     */
    public void hidePassword(){
        passWordEditText = findViewById(R.id.editText_signup_password);//Get password editText
        CheckBox passwordCheckBox = findViewById(R.id.signup_checkbox);//Get hidePassword CheckBox
        passwordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)//if checked hidePassword
                {
                    passWordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                }
                else {
                    passWordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }


}


