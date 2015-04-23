package edu.ncsu.csc.bucketlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;


public class NewBucket extends ActionBarActivity {

    private DBHelper mydb;
    private long dbUserId;
    private String imageTag;
    private ImageView userBucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bucket);

        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();
        userBucket = (ImageView) findViewById(R.id.userBucket);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bucket, menu);
        return true;
    }


    // Enforce that an image has been selected as well as a name typed in
    public void createButtonClick(View view) {
        EditText editText = (EditText) findViewById(R.id.bucketName);
        String bucketName = editText.getText().toString().trim();
        if (bucketName.equals("") && imageTag == null) {
            Toast.makeText(getApplicationContext(), "Please select a bucket image and enter a bucket name.", Toast.LENGTH_LONG).show();
        } else if (bucketName.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a bucket name.", Toast.LENGTH_LONG).show();
        } else if (imageTag == null) {
            Toast.makeText(getApplicationContext(), "Please select a bucket image.", Toast.LENGTH_LONG).show();
        } else {
            long bucketId = mydb.addBucket(dbUserId, bucketName, imageTag);
            if (bucketId != -1) {
                Toast.makeText(getApplicationContext(), "New Bucket Created!", Toast.LENGTH_SHORT).show();
                editText.setText("");
                userBucket.setVisibility(View.INVISIBLE);

            } else {
                Toast.makeText(getApplicationContext(), "Couldn't create new bucket!", Toast.LENGTH_SHORT).show();
            }
        }
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

    public void showBucketImage(View view) {

        userBucket.setVisibility(View.VISIBLE);
        imageTag = (String)view.getTag();
        if (view.getTag().equals("art")) {
            userBucket.setImageResource(R.drawable.art);

        } else if (view.getTag().equals("entertainment")) {
            userBucket.setImageResource(R.drawable.entertainment);

        } else if (view.getTag().equals("food")) {
            userBucket.setImageResource(R.drawable.food);

        } else if (view.getTag().equals("kid")) {
            userBucket.setImageResource(R.drawable.kid);

        } else if (view.getTag().equals("parks")) {
            userBucket.setImageResource(R.drawable.parks);

        } else if (view.getTag().equals("shopping")) {
            userBucket.setImageResource(R.drawable.shopping);

        } else if (view.getTag().equals("sports")) {
            userBucket.setImageResource(R.drawable.sports);

        } else if (view.getTag().equals("standard")) {
            userBucket.setImageResource(R.drawable.standard);
        }
    }
}