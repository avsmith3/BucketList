package edu.ncsu.csc.bucketlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyBuckets extends ActionBarActivity {

    private DBHelper mydb;
    private ListView obj;
    private long dbUserId;
    private ImageMap imageMap;
    private MenuItem editMenuItem;
    private MenuItem doneMenuItem;
    private CustomListAdapter listAdapter;
    ArrayList<BucketBean> buckets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buckets);

        mydb = new DBHelper(this);

        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        imageMap = new ImageMap();
        buckets = mydb.getAllBucketsForUser(dbUserId);
        listAdapter = new CustomListAdapter(this, buckets, imageMap.getHashMap());

        obj = (ListView)findViewById(R.id.myBucketsList);
        obj.setAdapter(listAdapter);

        obj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                BucketBean bucket = (BucketBean)arg0.getItemAtPosition(arg2);
                Bundle dataBundle = new Bundle();
                dataBundle.putLong("id", bucket.id);
                dataBundle.putLong("DB_USER_ID", dbUserId);
                Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.DisplayBucket.class);
                intent.putExtras(dataBundle);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_buckets, menu);
        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(false);

        editMenuItem = menu.findItem(R.id.action_edit_bucket);
        doneMenuItem = menu.findItem(R.id.action_edit_done);

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
        if ( id == R.id.action_edit_bucket ) {

           editMenuItem.setVisible(false);
           doneMenuItem.setVisible(true);

           listAdapter.setMode(true);
           listAdapter.notifyDataSetChanged();
           return true;

        } else if ( id == R.id.action_edit_done ) {

            // update bucket list to reflect changes to database
            listAdapter.clear();
            buckets = mydb.getAllBucketsForUser(dbUserId);
            listAdapter.addAll(buckets);

            // hide soft keyboard if user pressed done without making keyboard go away
            EditText listItemEdit = (EditText) findViewById(R.id.list_item_edit_name);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(listItemEdit.getWindowToken(), 0);

            // change back to normal view
            editMenuItem.setVisible(true);
            doneMenuItem.setVisible(false);

            listAdapter.setMode(false);
            listAdapter.notifyDataSetChanged();
            return true;
        }
        //TODO: What to do if user does not press done after editing?
        //TODO: Issue if user tries to name bucket an empty string or just spaces -> Only first letter of old name saved

        return super.onOptionsItemSelected(item);
    }

}
