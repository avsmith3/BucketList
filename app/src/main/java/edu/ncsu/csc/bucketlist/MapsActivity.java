package edu.ncsu.csc.bucketlist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import android.widget.AutoCompleteTextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMyLocationChangeListener, OnItemClickListener {

    private static final String TAG = "BucketList App";

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAeb-71G4ZxW9cqvBn1ICiHiBDyQj71C4c";

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
    private ImageMap imageMap;
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

        //***setOnQueryTextListener***
        /*searchBox = (SearchView)findViewById(R.id.searchBox);
        searchBox.setBackgroundColor(Color.WHITE);
        searchBox.getBackground().setAlpha(205);
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
        });*/

        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));
        autoCompView.setOnItemClickListener(this);


        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        //String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        //Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        listLayout = (LinearLayout) findViewById(R.id.list_layout);
        imageMap = new ImageMap();
        ArrayList<BucketBean> buckets = mydb.getAllBucketsForUser(dbUserId);
        BucketListAdapter listAdapter = new BucketListAdapter(MapsActivity.this, buckets, imageMap.getHashMap());

        list = (ListView) findViewById(R.id.mapsListView);
        list.setAdapter(listAdapter);

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
                    clickedMarker.setIcon(BitmapDescriptorFactory.fromResource(imageMap.getHashMap().get(bucket.image).get(0)));
                }
            }
        });

     }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Test: Autocomplete: onitemclick", Toast.LENGTH_LONG).show();
    }

    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        Toast.makeText(getApplicationContext(), "Test: Autocomplete: autocomplete", Toast.LENGTH_LONG).show();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {

            Toast.makeText(this, "Test: Autocomplete: StringBuilder", Toast.LENGTH_LONG).show();

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Toast.makeText(this, "Test: Autocomplete: Error processing Place", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Toast.makeText(this, "Test: Autocomplete: Error connecting to Place", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        }
        finally {
            if (conn != null) {
                conn.disconnect();
                Toast.makeText(this, "Test: Autocomplete:  conn.disconnect();", Toast.LENGTH_LONG).show();
            }
        }

        try {

            Toast.makeText(this, "Test: Autocomplete: JSONObject", Toast.LENGTH_LONG).show();

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cant process JSON results", e);
            Toast.makeText(this, "Test: Autocomplete: Cant process JSON results", Toast.LENGTH_LONG).show();
        }

        return resultList;
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
            //Nik: Choosing not to display a negative toast
            //Toast.makeText(this,"No location detected", Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(MapsActivity.this, "got clicked", Toast.LENGTH_SHORT).show(); //do some stuff
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
            //Nik: Not commenting as this makes sense to dsplay toast
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
                int bucketIcon = imageMap.getHashMap().get(bucket.image).get(0);
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

    //nested class object which implements the auto complete function
    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
