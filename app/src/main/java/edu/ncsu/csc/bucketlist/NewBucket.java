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
    String imageTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bucket);

        mydb = new DBHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_bucket, menu);
        return true;
    }


    //TODO: Button click should add the bucket to database
    public void createButtonClick(View view){
        long bucketId = mydb.addBucket(0, ((EditText) findViewById(R.id.bucketName)).getText().toString(), imageTag);
        if(bucketId != -1)
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

    public void showBucketImage(View view){
        ImageView userBucket = (ImageView) findViewById(R.id.userBucket);
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


        view.getId();
        Toast.makeText(getApplicationContext(), "View tag :" + view.getTag(), Toast.LENGTH_SHORT).show();
        System.out.println("View tag :"+view.getTag());
    }
}