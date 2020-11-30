package cmput301.team19.lendz;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This is the class that handles signing up a new user
 * It grabs data input by the user and signs them up using firebase inbuilt authentication then stores their data
 * in Firestore.
 * After this is successfully done it directs them to the Main Activity where they can use the app as they desire
 */
public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText usernameEditText;
    private EditText fullnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        hidePassword();
        signUpBtn = findViewById(R.id.signUp_button);
        usernameEditText = findViewById(R.id.editText_signup_username);
        passwordEditText = findViewById(R.id.editText_signup_password);
        fullnameEditText = findViewById(R.id.editText_signup_full_name);
        phoneNumberEditText = findViewById(R.id.editText_signup_phone_number);
        emailEditText = findViewById(R.id.editText_signup_email);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                passwordEditText = findViewById(R.id.editText_signup_password);
                emailEditText = findViewById(R.id.editText_signup_email);
                String username = usernameEditText.getText().toString();
                String fullname = fullnameEditText.getText().toString();
                String phoneNumber = phoneNumberEditText.getText().toString();
                final FirebaseAuth mFirebaseAuth= FirebaseAuth.getInstance();
                final String email = emailEditText.getText().toString();
                String pwd = passwordEditText.getText().toString();
                if(!isValidEmail(email))
                {
                    Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
                }
                else if(!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber))
                {
                    Toast.makeText(SignUpActivity.this, "Invalid phone number", Toast.LENGTH_LONG).show();
                }
                else if(pwd.length() < 6)
                {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_LONG).show();
                }
                else if(username.trim().isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter user name", Toast.LENGTH_LONG).show();
                }
                else if(fullname.trim().isEmpty())
                {
                    Toast.makeText(SignUpActivity.this, "Enter full Name", Toast.LENGTH_LONG).show();
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

                                                //Send Uid to Main Activity
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("userID",uid);
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();
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
    public void storeUser(User user) {
        user.store().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Unable to save user profile",
                        Toast.LENGTH_LONG).show();
                Log.e("err", "err", e);
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
        passwordEditText = findViewById(R.id.editText_signup_password);//Get password editText
        CheckBox showPasswordCheckBox = findViewById(R.id.show_password_checkbox);//Get showPassword CheckBox
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


}


