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
    private static String userName, userEmail, googleUserId;
    private long dbUserId;

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
        userName = intent.getStringExtra(USER_NAME);
        userEmail = intent.getStringExtra(USER_EMAIL);
        googleUserId = intent.getStringExtra(GOOGLE_USER_ID);
        dbUserId = intent.getLongExtra(DB_USER_ID, -1);

        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + userName + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        /*
        if (userName != null) {
            String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + userName + "!";
            Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();
        }
        else {
            //Try to fetch the user name and email from static variables in MainActivity (works but not nice)?
            //or logout ?
        }*/
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
        Log.d("Debug", "Connection failed");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("Debug", "Connected");
    }

    /** Called when the user clicks the Explore Map button */
    public void exploreMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("DB_USER_ID", dbUserId);
        startActivity(intent);
    }

    public void addNewBucket(View v)
    {
        Intent intent = new Intent(this, NewBucket.class);
        intent.putExtra("DB_USER_ID", dbUserId);
        startActivity(intent);
    }

    public void showMyBuckets(View v)
    {
        Intent intent = new Intent(this, MyBuckets.class);
        intent.putExtra("DB_USER_ID", dbUserId);
        startActivity(intent);
    }

    public void showTopBuckets(View v)
    {
        Intent intent = new Intent(this, TopBuckets.class);
        intent.putExtra("DB_USER_ID", dbUserId);
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
            Log.d("Debug", "Disconnected");
            Toast.makeText(this, "User is disconnected!", Toast.LENGTH_LONG).show();
        }
    }
}
