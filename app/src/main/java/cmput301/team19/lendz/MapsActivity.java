package cmput301.team19.lendz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;
    private int backToViewBook = 0;
    private Place place;
    private double latitude;
    private double longitude;

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
        confirmLocation(mConfirm);


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
            //initialize places Api
            //initMap();
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
                            if(currentLocation == null){
                                Toast.makeText(MapsActivity.this, "no location", Toast.LENGTH_SHORT).show();
                            }else{
                                LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                                moveCamera(latLng,DEFAULT_ZOOM,"My Location");
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();
                                dialogBox( "your current location","Pickup Location");

                            }

                        }else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get devices loaction", Toast.LENGTH_SHORT).show();
                            //use cell towers to get appromimate location

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
            place = Autocomplete.getPlaceFromIntent(data);
            //set address on EditText
            mSearchText.setText(place.getAddress());
            //setRequestLocation(place);
            moveCamera(place.getLatLng(),DEFAULT_ZOOM,"Pick Up location");
            mConfirm.show();
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this,status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * this will get the location of the requested book
     * @param place
     * this is the desired place the user selects
     */
    private void getRequestLocation(final Place place)
    {
        Intent intent = getIntent();
        String requestId = intent.getStringExtra("requestID");
        final String bookID = intent.getStringExtra("bookID");
        String requesterId = intent.getStringExtra("requesterID");

        Request request = Request.getOrCreate(requestId);
        Book book  = Book.getOrCreate(bookID);
        User user = User.getOrCreate(requesterId);
        request.setRequester(user);
        request.setBook(book);


//        get longitude and latitude
        LatLng destinationLatLng = place.getLatLng();
        assert destinationLatLng != null;
        double longitude = destinationLatLng.longitude;
        double latitude = destinationLatLng.latitude;
        Log.e(TAG, "setRequestLocation: " + longitude+ " "+ latitude);
        Location location = new Location(place.getAddress(),latitude,longitude);

        //check if intent has borrow value
        if(intent.hasExtra("borrow")){
            mConfirm.show();
            backToViewBook = 1;
        }else{
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
        }


    }

    //go back to main activity

    /**
     * this methods sets the location of the requested book
     * @param mConfirm
     * this is the button the user clicks to confirm the location searched
     */

    private void confirmLocation(final FloatingActionButton mConfirm){
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if(backToViewBook == 1){
                    if(intent.hasExtra("bookId")){
                        String bid = intent.getStringExtra("bookId");
                        //hide searchbar here
                        //hide check btn here
                        findViewById(R.id.relLayout1).setVisibility(View.GONE);
                        mConfirm.setVisibility(View.INVISIBLE);


                        //open fragment
                        Fragment fragment = ViewBookFragment.newInstance(bid);

                        Bundle bundle = new Bundle();
                        bundle.putInt("locationSet",1);
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().add(R.id.mapLayout, fragment)
                                .commit();

                    }

                }else{
                    Intent back = new Intent(MapsActivity.this,MainActivity.class);
                    getRequestLocation(place);
                    startActivity(back);
                }
            }
        });
    }
    public void dialogBox(String message, final String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle(title);

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        final Address address;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                address = addresses.get(0);
                final String addressName = addresses.get(0).getAddressLine(0);
                builder.setMessage(Html.fromHtml("Do you want to use "+ "<b><i>"+ addressName+"</i></b>"+" as the pickup location?"))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //SHOW CONFIRM BUTTON
                                Location location = new Location(addressName,latitude,longitude);
                                Intent intent = getIntent();
                                String requestId = intent.getStringExtra("requestID");
                                final String bookID = intent.getStringExtra("bookID");
                                String requesterId = intent.getStringExtra("requesterID");

                                Request request = Request.getOrCreate(requestId);
                                Book book  = Book.getOrCreate(bookID);
                                User user = User.getOrCreate(requesterId);
                                request.setRequester(user);
                                request.setBook(book);
                                request.setLocation(location);
                                //store in firebase
                                request.setStatus(RequestStatus.ACCEPTED);
                                request.store().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent back = new Intent(MapsActivity.this,MainActivity.class);
                                        startActivity(back);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MapsActivity.this,"Could not accept request",Toast.LENGTH_SHORT).show();
                                    }
                                });



                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}

