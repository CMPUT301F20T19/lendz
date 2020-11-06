package cmput301.team19.lendz;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private ImageView imageView;
    final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setImageView();
        showPassword();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser != null){
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);

                }
            }
        };
        signupText = findViewById(R.id.login_signUp_message);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
        login();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     * method that handles showing password
     */
    public void showPassword(){
        password = findViewById(R.id.editText_login_password);//Get password editText
        CheckBox showpassword = findViewById(R.id.checkbox_show_password);//Get showPassword CheckBox
        showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)//if checked showPassword
                {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


    /**
     * sets image view to an image stored in firestore
     */
    public void setImageView() {
        imageView = findViewById(R.id.app_logo);
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
     * this methods handles the logging in of the user
     */
    public void login()
    {
        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        Button loginBtn = findViewById(R.id.login_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailStr = email.getText().toString();
                final String pwd = password.getText().toString();
                if(emailStr.isEmpty()||pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"All Fields must be filled",Toast.LENGTH_SHORT).show();
                }
                else if(!isValidEmail(emailStr))
                {
                    Toast.makeText(LoginActivity.this,"InValid email",Toast.LENGTH_SHORT).show();
                }
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(emailStr,pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Email address or password not recognised, try again",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"Log in SuccessFul",Toast.LENGTH_SHORT).show();
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("userID", mFirebaseUser.getUid());
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
     * this method checks the validity of an email
     * @param target
     * this is email to be checked
     * @return
     * returns true if the email is valid (matches the pattern of a generic email)
     */
    private Boolean isValidEmail(CharSequence target){
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    };

}