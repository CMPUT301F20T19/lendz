package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    //widgets
    private EditText mSearchText;
    private FloatingActionButton mConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);
        //mGps =  findViewById(R.id.ic_gps);
        mConfirm = findViewById(R.id.confirm_location_button);
        mConfirm.hide();
        getLocationPermission();

    }

    /**
     * this method initialises the map
     */
    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapsActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d("onMapReady", "map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            //init();
            initPlacesApi();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0;i< grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize the map
                    initMap();
                }
            }
        }
    }

    /**
     * this method gets all the permisions need to for the map to run smoothly
     */
    private void getLocationPermission()
    {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * this method gets the device current location 
     */
    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the current device's location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) // if location is gotten succefully
                        {
                            Log.d(TAG, "onComplete: found location");
                            android.location.Location currentLocation = (android.location.Location) task.getResult();
                            //move the camera of the map to that location
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                            moveCamera(latLng,DEFAULT_ZOOM,"My Location");

                        }else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get devices loaction", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        }catch ( SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage() );
        }
        
    }

    /**
     *this method moves the camera of the map to desired location (also puts the pin on the map )
     * @param latLng
     * this is the longitude and latitude of the desired  location
     * @param zoom
     * gives us the ability to zoom
     */
    private void moveCamera(LatLng latLng,float zoom, String title){
        Log.d(TAG, "moveCamera: moving camera to current location");

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        //setting the marker on the map
        MarkerOptions options =  new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);

    }

    /**
     * initialise places API
     */
    private void initPlacesApi()
    {
        Places.initialize(getApplicationContext(),"AIzaSyDdULVvHA9tOu8OKbdSPuavcKXjFJ6pGr0");
        mSearchText.setFocusable(false);
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList)
                        .build(MapsActivity.this);
                //start activity for result
                startActivityForResult(intent,100);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            //when sucess
            //initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            //set address on EditText
            mSearchText.setText(place.getAddress());
            setRequestLocation(place);
            moveCamera(place.getLatLng(),DEFAULT_ZOOM,"Pick Up location");
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this,status.getStatusMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * this will set the location of the requested book
     * @param place
     * this is the desired place the user selects
     */
    private void setRequestLocation(Place place)
    {
        Intent intent = getIntent();
        String requestId = intent.getStringExtra("requestID");
        String bookID = intent.getStringExtra("bookID");
        String requesterId = intent.getStringExtra("requesterID");

        Request request = Request.getOrCreate(requestId);
        Book book  = Book.getOrCreate(bookID);
        User user = User.getOrCreate(requesterId);
        request.setRequester(user);
        request.setBook(book);

        //get longitude and latitude
        LatLng destinationLatLng = place.getLatLng();
        assert destinationLatLng != null;
        double longitude = destinationLatLng.longitude;
        double latitude = destinationLatLng.latitude;
        Log.e(TAG, "setRequestLocation: " + longitude+ " "+ latitude);
        Location location = new Location(place.getAddress(),latitude,longitude);
        request.setLocation(location);
        //store in firebase
        request.setStatus(RequestStatus.ACCEPTED);
        request.store().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mConfirm.show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this,"Could not accept request",Toast.LENGTH_SHORT).show();
            }
        });

        //go back to main activity
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(MapsActivity.this,MainActivity.class);
                startActivity(back);
            }
        });
    }
}