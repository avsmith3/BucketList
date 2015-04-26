package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private MenuItem editItem;
    private MenuItem doneItem;
    private CustomListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buckets);

        mydb = new DBHelper(this);

        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        imageMap = new ImageMap();
        ArrayList<BucketBean> buckets = mydb.getAllBucketsForUser(dbUserId);
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

        editItem = menu.findItem(R.id.action_edit_bucket);
        doneItem = menu.findItem(R.id.action_edit_done);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        super.onOptionsItemSelected(item);
        TextView listItemName = (TextView) findViewById(R.id.list_item_name);
        EditText listItemEdit = (EditText) findViewById(R.id.list_item_edit_name);
        ImageButton deleteBucketBtn = (ImageButton) findViewById(R.id.deleteBucketBtn);
        obj.invalidate();


        // single edit action for deleting or editing bucket name
        if ( id == R.id.action_edit_bucket ) {
           editItem.setVisible(false);
           doneItem.setVisible(true);

           listAdapter.setMode(true);
           listAdapter.notifyDataSetChanged();
           return true;
        } else if ( id == R.id.action_edit_done ) {
            // change back to normal view
            editItem.setVisible(true);
            doneItem.setVisible(false);

            listAdapter.setMode(false);
            listAdapter.notifyDataSetChanged();
            return true;
        }
        //TODO: What to do if user does not press done after editing?

        return super.onOptionsItemSelected(item);
    }

}
