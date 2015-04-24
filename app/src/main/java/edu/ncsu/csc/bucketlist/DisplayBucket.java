package edu.ncsu.csc.bucketlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class DisplayBucket extends ActionBarActivity {
    private DBHelper mydb;
    private long dbUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bucket);

        mydb = new DBHelper(this);


        Bundle extras = getIntent().getExtras();
        dbUserId = extras.getLong("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        if (extras != null) {
            long value = extras.getLong("id");
            if (value > 0) {
                BucketBean bucket = mydb.getBucket(value);

                setTitle(bucket.name);

                ArrayList<EntryBean> entries = mydb.getEntriesFor(value);
                ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, entries);

                ListView list = (ListView)findViewById(R.id.bucketList);
                list.setAdapter(arrayAdapter);


            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_bucket, menu);
        return true;
    }

}
