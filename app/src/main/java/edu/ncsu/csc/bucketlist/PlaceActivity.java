package edu.ncsu.csc.bucketlist;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.PlusShare;


public class PlaceActivity extends ActionBarActivity {
    private DBHelper mydb;
    private long dbUserId;
    private long entryId;
    private EntryBean entry;
    private MenuItem editMenuItem;
    private MenuItem doneMenuItem;
    private boolean inEditMode;
    private TextView placeComment;
    private EditText commentEdit;

    String postText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            inEditMode = savedInstanceState.getBoolean("MODE");
        } else {
            inEditMode = false;
        }

        mydb = new DBHelper(this);
        dbUserId = getIntent().getLongExtra("DB_USER_ID", -1);
        entryId = getIntent().getLongExtra("ENTRY_ID", -1);
        entry = mydb.getEntry(entryId);
        setTitle(entry.name);

        //String welcomeTxt = getResources().getString(R.string.welcomeText) + ", " + dbUserId + "!";
        //Toast.makeText(this, welcomeTxt, Toast.LENGTH_LONG).show();

        TextView placeTitle = (TextView) findViewById(R.id.place_title);
        placeTitle.setText(entry.infoTitle);

        TextView placeSnippet = (TextView) findViewById(R.id.place_snippet);
        placeSnippet.setText(entry.infoSnippet);

        int userRating = entry.rating;
        RatingBar ratingBar = (RatingBar) findViewById(R.id.place_rating);
        ratingBar.setRating(userRating);

        placeComment = (TextView) findViewById(R.id.place_comment);
        commentEdit = (EditText) findViewById(R.id.place_comment_edit);

        if (entry.comment.equals("")) {
            placeComment.setText("To leave a review, press edit button.");
            commentEdit.setHint("Enter your review here.");
        } else {
            placeComment.setText(entry.comment);
            commentEdit.setText(entry.comment);
        }

        if(inEditMode) {
            commentEdit.setVisibility(View.VISIBLE);
            placeComment.setVisibility(View.GONE);
        } else {
            placeComment.setVisibility(View.VISIBLE);
            commentEdit.setVisibility(View.GONE);
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (entry.visited == 1) {
                    mydb.updateEntryRating(entryId, (int) rating);
                    entry = mydb.getEntry(entryId);
                } else if (entry.visited == 0) {
                    ratingBar.setRating(0);
                    Toast.makeText(getApplicationContext(), "Please mark place as visited before rating.", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Manage how the share image button handles things
        Button shareMediaButton = (Button)findViewById(R.id.share_image_button);
        shareMediaButton.setOnClickListener(new android.view.View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*, video/*");
                startActivityForResult(photoPicker,1);
            }
        });


     /*   //Set the text which will be shared on G+
        postText = new String("I just visited ");
        postText += placeTitle.getText();
        postText += ("! \nMy Review: ");
        postText += placeComment.getText();
        postText += (" \nI'll give it ");
        switch ((int)ratingBar.getRating())
        {
            case 0:
                postText += ("0/5");
                break;
            case 1:
                postText+= ("1/5");
                break;
            case 2:
                postText+=("2/5");
                break;
            case 3:
                postText+=("3/5");
                break;
            case 4:
                postText+=("4/5");
                break;
            case 5:
                postText+=("5/5");
                break;
            default:
                break;
        }
        postText+=("\n#BucketList");

        final String pt = new String(postText);
        /*

        Button shareplacebutton = (Button) findViewById(R.id.share_place_button);
        shareplacebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch the google Plus share dialog
                Intent shareIntent = new PlusShare.Builder(getApplicationContext())
                        .setType("text/plain")
                        .setText(pt)
                        .getIntent();

                startActivityForResult(shareIntent, 0);

            }
        });
        */
    }

    public void share(View view)
    {
        EntryBean eb1 = new EntryBean();
        eb1 = mydb.getEntry(entryId);
        String post= new String("I just visited ");
        post+= eb1.toString();
        post+= ("!\n");
        post+= eb1.comment.toString();
        post+=("\nI'll give it ");
        int rating = eb1.rating;
        switch (rating)
        {
            case 1:
                post+=("1/5");
                break;
            case 2:
                post+=("2/5");
                break;
            case 3:
                post+=("3/5");
                break;
            case 4:
                post+=("4/5");
                break;
            case 5:
                post+=("5/5");
                break;
            default:
                post+=("0/5");
                break;
        }
        post+= ("\n#BucketList");
        final String poster = new String(post);
        Button share_place_button = (Button)findViewById(R.id.share_place_button);
        share_place_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch the google Plus share dialog
                Intent shareIntent = new PlusShare.Builder(getApplicationContext())
                        .setType("text/plain")
                        .setText(poster)
                        .getIntent();

                startActivityForResult(shareIntent, 0);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place, menu);

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

        // allow user to edit their place rating and comment
        if ( id == R.id.action_edit_place ) {

            if (entry.visited == 1) {
                inEditMode = true;
                editMenuItem.setVisible(false);
                doneMenuItem.setVisible(true);

                commentEdit.setVisibility(View.VISIBLE);
                placeComment.setVisibility(View.GONE);
            } else if (entry.visited == 0) {
                Toast.makeText(getApplicationContext(), "Please mark place as visited before entering review.", Toast.LENGTH_LONG).show();
            }

            return true;

        } else if ( id == R.id.action_edit_place_done ) {

            inEditMode = false;

            // change back to normal view
            editMenuItem.setVisible(true);
            doneMenuItem.setVisible(false);

            // hide soft keyboard if user pressed done without making keyboard go away
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);

            placeComment.setText(commentEdit.getText());
            mydb.updateEntryComment(entryId, commentEdit.getText().toString().trim());
            entry = mydb.getEntry(entryId);
            if (entry.comment.equals("")) {
                placeComment.setText("To leave a review, press the edit button.");
                commentEdit.setHint("Enter your review here.");
            }
            commentEdit.setVisibility(View.GONE);
            placeComment.setVisibility(View.VISIBLE);

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

    protected void onActivityResult(int requestCode,int resultCode,Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);

        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Uri selectedImage = intent.getData();
                ContentResolver cr = this.getContentResolver();
                String mime = cr.getType(selectedImage);
                final String pt = postText;

                Intent shareIntent = new PlusShare.Builder(getApplicationContext())
                        .setType("text/plain")
                        .setText(pt)
                        .addStream(selectedImage)
                        .setType(mime)
                        .getIntent();

                startActivityForResult(shareIntent,2);
            }

        }
    }

}
