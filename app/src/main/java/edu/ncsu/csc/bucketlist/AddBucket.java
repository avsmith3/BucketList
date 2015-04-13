package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.AddressConstants;


public class AddBucket extends ActionBarActivity {

    private DBHelper mydb;
    TextView name;
    TextView latitude;
    TextView longitude;
    TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bucket);

        name = (TextView)findViewById(R.id.editText_bucketName);
        latitude = (TextView)findViewById(R.id.editText_enterLat);
        longitude = (TextView)findViewById(R.id.editText_enterLong);
        desc = (TextView)findViewById(R.id.editText_enterDesc);

        mydb = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        {

        }

    }

    public void onClick_addBucket(View v)
    {
        //Bundle extras = getIntent().getExtras();
            //int Value = extras.getInt("id");
            if(mydb.addBucket(name.getText().toString(),latitude.getText().toString(),longitude.getText().toString(),desc.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),"New Bucket Created!",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.HomePage.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Couldn't create new bucket!",Toast.LENGTH_SHORT).show();
            }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_bucket, menu);
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
