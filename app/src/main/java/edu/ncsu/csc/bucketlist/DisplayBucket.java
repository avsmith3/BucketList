package edu.ncsu.csc.bucketlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DisplayBucket extends ActionBarActivity {
    private DBHelper mydb;
    private long dbUserId;
    private ImageMap imageMap;
    private MenuItem editMenuItem;
    private MenuItem doneMenuItem;
    private PlaceListAdapter listAdapter;
    private ArrayList<EntryBean> entries;
    private boolean inEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bucket);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            inEditMode = savedInstanceState.getBoolean("MODE");
        } else {
            inEditMode = false;
        }

        mydb = new DBHelper(this);
        imageMap = new ImageMap();

        Bundle extras = getIntent().getExtras();
        dbUserId = extras.getLong("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        if (extras != null) {
            long bucketId = extras.getLong("id");
            if (bucketId > 0) {
                BucketBean bucket = mydb.getBucket(bucketId);

                setTitle("  " + bucket.name);

                ActionBar actionBar = getSupportActionBar();
                actionBar.setLogo(imageMap.getHashMap().get(bucket.image).get(0));
                actionBar.setDisplayUseLogoEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);

                entries = mydb.getEntriesFor(bucketId);
                listAdapter = new PlaceListAdapter(this, entries, bucketId);
                listAdapter.setMode(inEditMode);

                ListView list = (ListView)findViewById(R.id.placesList);
                list.setAdapter(listAdapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_bucket, menu);
        if (!inEditMode) {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
        } else {
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
        }

        editMenuItem = menu.findItem(R.id.action_edit_place);
        doneMenuItem = menu.findItem(R.id.action_edit_place_done);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        super.onOptionsItemSelected(item);


        // single edit action for deleting or editing bucket name
        if ( id == R.id.action_edit_place ) {

            inEditMode = true;
            editMenuItem.setVisible(false);
            doneMenuItem.setVisible(true);

            listAdapter.setMode(inEditMode);
            listAdapter.notifyDataSetChanged();
            return true;

        } else if ( id == R.id.action_edit_place_done ) {

            inEditMode = false;

            // change back to normal view
            editMenuItem.setVisible(true);
            doneMenuItem.setVisible(false);

            listAdapter.setMode(inEditMode);
            listAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save state
        savedInstanceState.putBoolean("MODE", inEditMode);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
