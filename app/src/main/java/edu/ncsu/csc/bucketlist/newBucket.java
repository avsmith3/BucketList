package edu.ncsu.csc.bucketlist;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class newBucket extends ActionBarActivity {

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_bucket);

        mydb = new DBHelper(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_new_bucket, menu);
        return true;
    }


    //TODO: Button click should add the bucket to database
    public void CreateButtonClick(View view){
        /*if(mydb.addBucket(...))
        {
            Toast.makeText(getApplicationContext(),"New Bucket Created!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.HomePage.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Couldn't create new bucket!",Toast.LENGTH_SHORT).show();
        }*/
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
        view.getId();
        Toast.makeText(getApplicationContext(),"View tag :"+view.getTag(),Toast.LENGTH_SHORT).show();
        System.out.println("View tag :"+view.getTag());
    }
}