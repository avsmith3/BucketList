package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class TopBuckets extends ActionBarActivity {
    private DBHelper mydb;
    private long dbUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_buckets);

        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        //String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        //Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        ArrayList<EntryBean> topPlaces  = new ArrayList<EntryBean>();
        // Add places with 5 star rating to top buckets
        ArrayList<BucketBean> userBuckets = mydb.getAllBucketsForUser(dbUserId);
        // for each bucket
        for (int i = 0; i < userBuckets.size(); i++) {
            BucketBean bucket = userBuckets.get(i);
            ArrayList<EntryBean> bucketEntries = mydb.getEntriesFor(bucket.id);
            for (int j = 0; j < bucketEntries.size(); j++) {
                EntryBean entry = bucketEntries.get(j);
                if (entry.rating == 5) {
                    topPlaces.add(entry);
                }

            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, topPlaces);

        ListView topPlacesList = (ListView)findViewById(R.id.topBucketListView);
        topPlacesList.setAdapter(arrayAdapter);

        topPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                EntryBean entry = (EntryBean)arg0.getItemAtPosition(arg2);
                Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.PlaceActivity.class);
                intent.putExtra("DB_USER_ID", dbUserId);
                intent.putExtra("ENTRY_ID", entry.id);
                startActivity(intent);
            }
        });

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_buckets, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
