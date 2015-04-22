package edu.ncsu.csc.bucketlist;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private static final String TAG = "BucketList App";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    Location lastLocalKnownLocation = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiSettings mUiSettings;

    private RelativeLayout layout;
    private SearchView searchBox;
    private static final float DEFAULTZOOM = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        }

        mapFragment.getMapAsync(this);

        searchBox = new SearchView(MapsActivity.this);
        layout = (RelativeLayout) findViewById(R.id.maps_layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) RadioGroup.LayoutParams.WRAP_CONTENT, (int) RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 15, 0, 0);
        searchBox.setQueryHint("Enter Location");
        searchBox.setBackgroundColor(Color.WHITE);
        searchBox.getBackground().setAlpha(205);

        searchBox.setLayoutParams(params);
        layout.addView(searchBox);


        //***setOnQueryTextListener***
        searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                if (query.trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Enter Location",
                            Toast.LENGTH_SHORT).show();
                } else {
                    geoLocate(searchBox);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                return false;
            }
        });

     }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMyLocationEnabled(true);
        mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        //TODO: not sure how we can do an info window when user drops a pin this way
        mMap.setOnMapLongClickListener(  new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                drawMarker(latLng,"","");
            }
        });

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            lastLocalKnownLocation = mLastLocation;
            // Showing the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            Toast.makeText(this,"No location detected", Toast.LENGTH_LONG).show();
        }

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                likelyPlaces.release();
            }
        });
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);

    }

    // Helpful tutorial used: https://www.youtube.com/watch?v=O5pxlyyyvbw
    public void geoLocate(View v) {
        String location = searchBox.getQuery().toString().trim();
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        List<Address> list = null;
        try {
            list = geo.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            Address addr = list.get(0);
            String title = "";
            if (addr.getMaxAddressLineIndex() > -1) {
               title = addr.getAddressLine(0);
            }
            StringBuilder placeInfo = new StringBuilder("");
            for (int i = 1; i < addr.getMaxAddressLineIndex(); i++) {
                placeInfo.append("\n").append(addr.getAddressLine(i));
            }
            String phone = addr.getPhone();
            String url = addr.getUrl();
            if (phone != null) {
                placeInfo.append("\n").append(phone);
            }
            if (url != null) {
                placeInfo.append("\n").append(url);
            }
            Log.i(TAG, title);
            Log.i(TAG, placeInfo.toString());

            double lat = addr.getLatitude();
            double lng = addr.getLongitude();
            gotoLocation(lat, lng, DEFAULTZOOM);

            drawMarker(new LatLng(lat, lng), title, placeInfo.toString());
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onMapLongClick(LatLng latlng) {
        drawMarker(latlng, "", "");
    }

    private void drawMarker(LatLng currentPosition, String title, String info){
        // Remove any existing markercation locations on the map
        //mMap.clear();
        //LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        //TODO: It appears that I will have to create a custom InfoWindow so that we can have more than two lines
        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title(title)
                .snippet(info)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
               // .draggable(true));

        // To draw a bucket icon on map use .icon(BitmapDescriptorFactory.fromResource(R.drawable.imagename_tiny)) instead

        //TODO: Perhaps we can use OnMarkerDragListener to signal an add to bucket and popup buckets to "drop" pin into
        // Change pin from generic Google pin to bucket pin after added to bucket?
    }

    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    //One of the 2 functions below is redundant, probably the first one
    @Override
    public void onLocationChanged(Location location) {
        if(lastLocalKnownLocation == null) {
            lastLocalKnownLocation = location;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(lastLocalKnownLocation == null) {
            lastLocalKnownLocation = location;
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}
