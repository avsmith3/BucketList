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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class DisplayBucket extends ActionBarActivity {
    int from_where_i_am =0;
    private DBHelper mydb;
    TextView name;
    TextView latitude;
    TextView longitude;
    TextView description;
    int id_to_update =0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_bucket);
        name = (TextView)findViewById(R.id.editTextName);
        latitude = (TextView)findViewById(R.id.editTextLat);
        longitude = (TextView)findViewById(R.id.editTextLong);
        description = (TextView)findViewById(R.id.editTextDesc);

        mydb = new DBHelper(this);
        Bundle extras = getIntent().getExtras();

        if(extras!=null)
        {
            int Value = extras.getInt("id");
            if(Value>0)
            {
                Cursor rs = mydb.getData(Value);
                id_to_update = Value;
                rs.moveToFirst();
                String nam = rs.getString(rs.getColumnIndex(DBHelper.BUCKETS_COLUMN_NAME));
                String la = rs.getString(rs.getColumnIndex(DBHelper.BUCKETS_COLUMN_LATITUDE));
                String lo = rs.getString(rs.getColumnIndex(DBHelper.BUCKETS_COLUMN_LONGITUDE));
                String de = rs.getString(rs.getColumnIndex(DBHelper.BUCKETS_COLUMN_DESCRIPTION));

                if(!rs.isClosed())
                {
                    rs.close();
                }

                Button b = (Button)findViewById(R.id.buttonSaveBucket);
                b.setVisibility(View.INVISIBLE);

                name.setText((CharSequence)nam);
                latitude.setText((CharSequence)la);
                longitude.setText((CharSequence)lo);
                description.setText((CharSequence)de);


            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_bucket, menu);
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

        super.onOptionsItemSelected(item);

        switch(id)
        {
            case R.id.Edit_Bucket:
                Button b = (Button)findViewById(R.id.buttonSaveBucket);
                b.setVisibility(View.VISIBLE);
                name.setEnabled(true);
                name.setFocusableInTouchMode(true);
                name.setClickable(true);

                latitude.setEnabled(true);
                latitude.setFocusableInTouchMode(true);
                latitude.setClickable(true);

                longitude.setEnabled(true);
                longitude.setFocusableInTouchMode(true);
                longitude.setClickable(true);

                description.setEnabled(true);
                description.setFocusableInTouchMode(true);
                description.setClickable(true);

                return true;

            case R.id.Delete_Bucket:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.deleteBucket).setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    mydb.deleteBucket(id_to_update);
                    Toast.makeText(getApplicationContext(),"Bucket deleted successfully!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.HomePage.class);
                    startActivity(intent);

                }
            }
            ).setNegativeButton(R.string.no,new DialogInterface.OnClickListener()
                {
                 public void onClick(DialogInterface dialog, int id)
                 {
                     //Do nothing. User has cancelled the action.
                 }
                }
                );

                AlertDialog d = builder.create();
                d.setTitle("Are you sure?");
                d.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);



        }
    }
    public void run(View view)
    {
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            int Value = extras.getInt("id");
            if(Value>0)
            {
                if(mydb.updateBucket(id_to_update, name.getText().toString(),
                        latitude.getText().toString(), longitude.getText().toString(), description.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Bucket Updated!",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.HomePage.class);
                    startActivity(intent);


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Couldn't Update Bucket", Toast.LENGTH_SHORT).show();

                }
            }
            else
            {
                if(mydb.addBucket(name.getText().toString(),
                        latitude.getText().toString(),longitude.getText().toString(),description.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(),"Done!",Toast.LENGTH_SHORT);

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Not Done!",Toast.LENGTH_SHORT);
                }
                Intent intent = new Intent(getApplicationContext(),edu.ncsu.csc.bucketlist.HomePage.class);
                startActivity(intent);
            }
        }
    }
}
