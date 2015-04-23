package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyBuckets extends ActionBarActivity {

    private DBHelper mydb;
    private ListView obj;
    private long dbUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_buckets);

        mydb = new DBHelper(this);

        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        ArrayList array_list = mydb.getAllBucketsForUser(dbUserId);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,array_list);

        obj = (ListView)findViewById(R.id.listView1);
        obj.setAdapter(arrayAdapter);

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        super.onOptionsItemSelected(item);


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }*/
}
