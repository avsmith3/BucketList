package edu.ncsu.csc.bucketlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;


public class TopBuckets extends ActionBarActivity {
    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_buckets);

        mydb = new DBHelper(this);
        ArrayList<EntryBean> topPlaces  = new ArrayList<EntryBean>();
        // Add places with 5 star rating to top buckets
        ArrayList<BucketBean> userBuckets = mydb.getAllBucketsForUser(0);
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

        ListView topPlacesList = (ListView)findViewById(R.id.listView1);
        topPlacesList.setAdapter(arrayAdapter);
    }


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
}
