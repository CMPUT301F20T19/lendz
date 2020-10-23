package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class SignUpActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setImageView();
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
}