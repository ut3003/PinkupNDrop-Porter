package com.bestpractices.learning.atul.pinkupndrop.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bestpractices.learning.atul.pinkupndrop.GetDataService;
import com.bestpractices.learning.atul.pinkupndrop.GetUsersData;
import com.bestpractices.learning.atul.pinkupndrop.GetVehicles;
import com.bestpractices.learning.atul.pinkupndrop.GetVehiclesData;
import com.bestpractices.learning.atul.pinkupndrop.R;
import com.bestpractices.learning.atul.pinkupndrop.RetrofitClientInstance;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "MainActivity";
    private final Integer PICKUP = 1;
    private final Integer DROP = 2;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> fields;

    private GoogleMap mMap;
    private View mMapView;
    private MarkerOptions markerOptions;
    private LocationManager locationManager;
    private LocationProvider locationProvider;

    private Context mContext;

    private Integer locationRequset;
    private EditText pickUp;
    private EditText drop;
    private ImageView pin_image;
    private TextView costText;
    private TextView etaText;
    private TextView bookText;
    private LinearLayout layout;

    private GetUsersData getUserData;
    private GetVehicles getVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        costText = findViewById(R.id.cost);
        etaText = findViewById(R.id.eta);
        bookText = findViewById(R.id.book_button);
        mContext = getApplicationContext();
        locationRequset = PICKUP;
        layout = findViewById(R.id.label);
        getUserData = new GetUsersData();
        getVehicles = new GetVehicles();
        checkLocationPermission();

//        Initialize
        pickUp = (EditText) findViewById(R.id.pickUp);
        drop = (EditText) findViewById(R.id.drop);
        pin_image = findViewById(R.id.static_pin);

        markerOptions = new MarkerOptions();

        pickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationRequset = PICKUP;
                Log.i(TAG, "Pickup clicked");
                getLocation();
            }
        });

        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationRequset = DROP;
                Log.i(TAG, "Drop clicked");
                getLocation();
            }
        });

        // Initialize Places.
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 20 seconds
                getUserServiceability();
                handler.postDelayed(this, 20000);
            }
        }, 20000);
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location")
                        .setMessage("Please give location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        Log.i(TAG, "Permission Granted");

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    private void getLocation() {

        // Set the fields to specify which types of place data to
// return after the user has made a selection.
        fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void updateLocation(Place place) {
        String name = (String) place.getName();
        LatLng latLng = place.getLatLng();

        markerOptions.position(latLng);
        markerOptions.title(name);

        setAddress(name, markerOptions);
    }

    private void setAddress(String address, MarkerOptions markerOptions) {
        Log.d(TAG, "setAddress: " + address + ", "  + locationRequset);
        if (locationRequset == PICKUP) {
            pickUp.setText(address);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            layout.setVisibility(View.GONE);
            getCostAndEta();
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            drop.setText(address);
        }
    }

    //Updating Cost & ETA

    private void getCostAndEta() {

        GetVehiclesData vehiclesData = RetrofitClientInstance.getRetrofitInstance().create(GetVehiclesData.class);
        getVehicles.getVehiclesData(vehiclesData);
        setCostToLabel(getVehicles.getCost());
        setETAToLabel(getVehicles.getETA());

    }

    private void getUserServiceability(){
        Log.d(TAG, "getUserServiceability: ");
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        getUserData.getUserData(service);
        setStopService(getUserData.getStopService());
    }


    private void setStopService(boolean isAvailable) {
        Log.d(TAG, "setStopService: ");
        TextView textView = findViewById(R.id.set_stop);
        if(!isAvailable){
            layout.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Your account is blocked!");
        }else{
            textView.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }

    }


    private void setETAToLabel(int eta) {
        layout.setVisibility(View.VISIBLE);
        etaText.setText(String.valueOf(eta) + " min. | ");
    }

    private void setCostToLabel(int cost) {
        layout.setVisibility(View.VISIBLE);
        costText.setText(String.valueOf(cost) +" Rs. | " );
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;
        if (isNetworkEnabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                Double lat = location.getLatitude();
                Double lng = location.getLongitude();
                LatLng latLng = new LatLng(lat, lng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }


        }
        mMap.setMyLocationEnabled(true);

        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();

        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);

        if (mMap != null) {
            mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {

                    // TODO Auto-generated method stub

//                    mMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title(""));
                }
            });

        }

        moveMapManually();
    }

    private void moveMapManually() {

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
//                Log.d(TAG, "onCameraMoveStarted: " + i);
//                mDragTimer.start();
//                mTimerIsRunning = true;

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (mMap != null) {
                    mMap.clear();
                }

                LatLng mPosition = mMap.getCameraPosition().target;
                Float mZoom = mMap.getCameraPosition().zoom;

                getAddress(mPosition.latitude, mPosition.longitude);

            }
        });
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            String add = "";

            if (addresses != null && addresses.size() > 0) {
                Address obj = addresses.get(0);
                add = obj.getAddressLine(0);

                LatLng latLng = new LatLng(lat, lng);

                markerOptions.position(latLng);
                markerOptions.title(add);

                setAddress(add, markerOptions);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());

                updateLocation(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User Cancelled");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
