package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class HomePage extends MainActivity {

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        setContentView(R.layout.activity_home_page);

        // Get the message from the intent
        Intent intent = getIntent();
        String userName = intent.getStringExtra(USER_NAME);
        String userEmail = intent.getStringExtra(USER_EMAIL);

        if (userName != null) {
            String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + userName + "!";
            Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d("Debug","Connection failed says HomePage");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Debug","Connected --from HomePage");
    }

    /** Called when the user clicks the Explore Map button */
    public void exploreMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void addBucket(View v)
    {
        Intent intent = new Intent(this,NewBucket.class);
        startActivity(intent);
    }
    public void showBuckets(View v)
    {
        Intent intent = new Intent(this,MyBuckets.class);
        startActivity(intent);
    }

    /**
     * Sign-out from Google+
     */
    public void signOutFromGplus(View v) {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
            Log.d("Debug", "Disconnected --from HomePage");
            Toast.makeText(this, "User is disconnected! says HomePage", Toast.LENGTH_LONG).show();
        }
    }
}
