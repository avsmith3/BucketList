package edu.ncsu.csc.bucketlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class PlaceActivity extends ActionBarActivity {
    private DBHelper mydb;
    private long dbUserId;
    private long entryId;
    private EntryBean entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        entryId = getIntent().getLongExtra("ENTRY_ID", -1);
        entry = mydb.getEntry(entryId);
        String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        TextView placeTitle = (TextView) findViewById(R.id.place_title);
        placeTitle.setText(entry.infoTitle);
        TextView placeSnippet = (TextView) findViewById(R.id.place_snippet);
        placeSnippet.setText(entry.infoSnippet);
        int userRating = entry.rating;
        RatingBar ratingBar = (RatingBar) findViewById(R.id.place_rating);
        ratingBar.setRating(userRating);
        TextView placeComment = (TextView) findViewById(R.id.place_comment);
        placeComment.setText(entry.comment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place, menu);
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
