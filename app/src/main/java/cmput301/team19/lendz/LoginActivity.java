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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private CheckBox showpassword;
    private Button loginBtn;
    private TextView signupText;
    private ImageView imageView;
    final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setImageView();
        showPassword();
        signUpClick();
        login();


    }




    /**
     * method that handles showing password
     */
    public void showPassword(){
        password = findViewById(R.id.editText_login_password);//Get password editText
        showpassword = findViewById(R.id.checkbox_show_password);//Get showPassword CheckBox
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
     * method that handles clicking of the signup text
     */
    public void signUpClick()
    {
        signupText = findViewById(R.id.login_signUp_message);
        String text = "Don't have an account? SignUp";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.MAGENTA);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan,23,29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setText(ss);
        signupText.setMovementMethod(LinkMovementMethod.getInstance());
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
                        Picasso.with(LoginActivity.this).load(uri).into(imageView);
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
//    }

    public void login()
    {
        email = findViewById(R.id.editText_login_username);
        password = findViewById(R.id.editText_login_password);
        loginBtn = findViewById(R.id.login_button);

//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
//                if(mFirebaseUser != null)
//                {
//                    Toast.makeText(LoginActivity.this,"Log in succesful",Toast.LENGTH_SHORT).show();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("user", (Serializable) mFirebaseUser);
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                }
//                else{
//                    Toast.makeText(LoginActivity.this,"Account not recognised please signUp",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailStr = email.getText().toString();
                String pwd = password.getText().toString();
                if(emailStr.isEmpty()||pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"All Fields must be filled",Toast.LENGTH_SHORT).show();
                }
                else {
                    mFirebaseAuth.signInWithEmailAndPassword(emailStr,pwd)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Account not recognised, try again",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"Log in SuccessFul",Toast.LENGTH_SHORT).show();
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("user", (Serializable) mFirebaseUser.getUid());
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

    public void authenticate(){}
}