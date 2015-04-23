package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener {

    private static final String TAG = "BucketList App";

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    private static Location lastLocalKnownLocation = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private UiSettings mUiSettings;

    private SearchView searchBox;
    private static final float DEFAULTZOOM = 15;
    private ArrayList<Marker> mapMarkers = new ArrayList<Marker>();
    private DBHelper mydb;
    private long dbUserId;
    private ListView list;
    private LinearLayout listLayout;
    private Marker clickedMarker;
    private HashMap<String, ArrayList<Integer>> hashMap;
    private Marker lastClicked;

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

        searchBox = (SearchView)findViewById(R.id.searchBox);

        searchBox.setBackgroundColor(Color.WHITE);
        searchBox.getBackground().setAlpha(205);


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

        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        listLayout = (LinearLayout) findViewById(R.id.list_layout);

        ArrayList array_list = mydb.getAllBucketsForUser(dbUserId);
        ArrayAdapter arrayAdapter = new ArrayAdapter(MapsActivity.this, android.R.layout.simple_list_item_1, array_list);

        list = (ListView) findViewById(R.id.mapsListView);
        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                BucketBean bucket = (BucketBean) arg0.getItemAtPosition(arg2);
                String tag = bucket.image;
                if (clickedMarker != null) {
                    long entryId = mydb.addEntry(clickedMarker.getTitle(), clickedMarker.getPosition().latitude,
                            clickedMarker.getPosition().longitude, "", 0, 0, clickedMarker.getTitle(), clickedMarker.getSnippet());
                    if (entryId != -1) {
                        mydb.addToBucket(entryId, bucket.id);
                        Toast.makeText(MapsActivity.this, "Location added to " + bucket.name + " bucket!", Toast.LENGTH_LONG).show();
                    }

                    // Hide list of buckets
                    listLayout.setVisibility(View.GONE);

                    // replace icon of marker to represent bucket
                    clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(hashMap.get(bucket.image).get(0)));
                }
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
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            lastLocalKnownLocation = mLastLocation;
            // Showing the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULTZOOM));
        } else {
            Toast.makeText(this,"No location detected", Toast.LENGTH_LONG).show();
        }

        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (lastClicked != null && lastClicked.equals(marker)) {
                    lastClicked = null;
                    marker.hideInfoWindow();
                    return true;
                } else {
                    lastClicked = marker;
                    return false;
                }
            }
        });

        map.setOnMapLongClickListener(new OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                clickedMarker = null;
                for (Marker marker : mapMarkers) {
                    if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                        Toast.makeText(MapsActivity.this, "got clicked", Toast.LENGTH_SHORT).show(); //do some stuff
                        clickedMarker = marker;
                        break;
                    }
                }
                if (clickedMarker != null) {
                    listLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                // Set title and make it bold
                String title = marker.getTitle();
                TextView titleUi = ((TextView) view.findViewById(R.id.infoTitleTxt));
                if (title != null) {
                    // Spannable string allows us to edit the formatting of the text.
                    SpannableString titleText = new SpannableString(title);
                    titleText.setSpan(new StyleSpan(Typeface.BOLD), 0, titleText.length(), 0);
                    titleUi.setText(titleText);
                } else {
                    titleUi.setText("");
                }

                String snippet = marker.getSnippet();
                TextView snippetUi = (TextView) view.findViewById(R.id.infoSnippetTxt);

                if (snippet != null) {
                    // Setting the snippet
                    snippetUi.setText(snippet);
                } else {
                    snippetUi.setText("");
                }

                // Returning the view containing InfoWindow contents
                return view;

            }
        });

        hashMap = new HashMap<String, ArrayList<Integer>>();
        // add pairs of tag, images
        hashMap.put("art", new ArrayList<Integer>(Arrays.asList(R.drawable.art_tiny, R.drawable.art)));
        hashMap.put("entertainment", new ArrayList<Integer>(Arrays.asList(R.drawable.entertainment_tiny, R.drawable.entertainment)));
        hashMap.put("food", new ArrayList<Integer>(Arrays.asList(R.drawable.food_tiny, R.drawable.food)));
        hashMap.put("kid", new ArrayList<Integer>(Arrays.asList(R.drawable.kid_tiny, R.drawable.kid)));
        hashMap.put("parks", new ArrayList<Integer>(Arrays.asList(R.drawable.parks_tiny, R.drawable.parks)));
        hashMap.put("shopping", new ArrayList<Integer>(Arrays.asList(R.drawable.shopping_tiny, R.drawable.shopping)));
        hashMap.put("sports", new ArrayList<Integer>(Arrays.asList(R.drawable.sports_tiny, R.drawable.sports)));
        hashMap.put("standard", new ArrayList<Integer>(Arrays.asList(R.drawable.standard_tiny, R.drawable.standard)));

        loadMarkers();
/* // Not necessary - just testing it out - provides places near user's current location
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
        });*/
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
                placeInfo.append(addr.getAddressLine(i)).append("\n");
            }
            String phone = addr.getPhone();
            String url = addr.getUrl();
            if (phone != null) {
                placeInfo.append(phone).append("\n");
            }
            if (url != null) {
                placeInfo.append(url).append("\n");
            }
            Log.i(TAG, title);
            Log.i(TAG, placeInfo.toString());
            Log.i(TAG, addr.getFeatureName());

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

    private void drawMarker(LatLng currentPosition, String title, String info){
        // Remove any existing markercation locations on the map
        //mMap.clear();
        //LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        //TODO: It appears that I will have to create a custom InfoWindow so that we can have more than two lines
        Marker newMarker = mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .title(title)
                .snippet(info)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mapMarkers.add(newMarker);

        // To draw a bucket icon on map use .icon(BitmapDescriptorFactory.fromResource(R.drawable.imagename_tiny)) instead

    }

    public void loadMarkers(){
        ArrayList<BucketBean> userBuckets = mydb.getAllBucketsForUser(dbUserId);
            // for each userBucket create markers for each place in bucket using correct image
            for (int i = 0; i < userBuckets.size(); i++) {
                BucketBean bucket = (BucketBean) userBuckets.get(i);
                int bucketIcon = hashMap.get(bucket.image).get(0);
                ArrayList<EntryBean> bucketEntries = mydb.getEntriesFor(bucket.id);

                    for (int j = 0; j < bucketEntries.size(); j++) {
                        // draw markers for places in bucket
                        EntryBean entry = (EntryBean) bucketEntries.get(j);
                        LatLng position = new LatLng(entry.latitude, entry.longitude);
                        mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(entry.infoTitle)
                                .snippet(entry.infoSnippet)
                                .icon(BitmapDescriptorFactory.fromResource(bucketIcon)));

                    }

            }


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
