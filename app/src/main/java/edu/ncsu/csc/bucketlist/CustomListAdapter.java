package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListAdapter extends ArrayAdapter<BucketBean> {

    private final Activity context;
    private final HashMap<String, ArrayList<Integer>> hashMap;
    private final ArrayList<BucketBean> buckets;
    private boolean inEditMode;
    private DBHelper mydb;
    private String previousBucketName;

    public CustomListAdapter(Activity context, ArrayList<BucketBean> buckets, HashMap<String, ArrayList<Integer>> hashMap)
    {
        super(context, R.layout.bucket_listview_layout, buckets);

        this.context = context;
        this.hashMap = hashMap;
        this.buckets = buckets;
        inEditMode = false;
        mydb = new DBHelper(getContext());
    }

    public void setMode(boolean mode) {
        inEditMode = mode;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.bucket_listview_layout, null, true);

        final TextView titleText = (TextView) rowView.findViewById(R.id.list_item_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_img);
        EditText editableText = (EditText) rowView.findViewById(R.id.list_item_edit_name);
        editableText.setTag(position);
        ImageButton deleteBucketBtn = (ImageButton) rowView.findViewById(R.id.deleteBucketBtn);
        deleteBucketBtn.setTag(position);

        BucketBean bucket = buckets.get(position);
        titleText.setText(bucket.name);
        editableText.setText(bucket.name);

        if (inEditMode) {
            titleText.setVisibility(View.GONE);
            editableText.setVisibility(View.VISIBLE);
            deleteBucketBtn.setVisibility(View.VISIBLE);
        } else {
            titleText.setVisibility(View.VISIBLE);
            editableText.setVisibility(View.GONE);
            deleteBucketBtn.setVisibility(View.GONE);
        }

        imageView.setImageResource(hashMap.get(bucket.image).get(1));

        deleteBucketBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int index = (Integer)v.getTag();
                Log.i("App", "Delete Button clicked Position:" + index);
                BucketBean currentBucket = buckets.get(index);
                // remove item from database
                // delete all entries in bucket
                ArrayList<EntryBean> entries = mydb.getEntriesFor(currentBucket.id);
                for (int i = 0; i < entries.size(); i++) {
                    EntryBean entry = (EntryBean) entries.get(i);
                    mydb.deleteEntry(entry.id);
                }
                // delete all bucketentryassociations for bucket
                mydb.removeAllFromBucket(currentBucket.id);
                // delete bucket
                mydb.deleteBucket(currentBucket.id);

                // remove item from listview
                buckets.remove(index);
                notifyDataSetChanged();
            }

        });


        editableText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    int index = (Integer) v.getTag();
                    Log.i("App", "Edit Text pos:" + index);
                        BucketBean currentBucket = buckets.get(index);
                        String edit = ((EditText) v).getText().toString();
                        if (!edit.trim().equals("")) {
                            mydb.updateBucket(currentBucket.id, edit, currentBucket.image);
                        }
                }
                return false;
            }
        });
/*
        editableText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                previousBucketName = ((EditText) v).getText().toString();
                Log.i("App", "Previous bucket name:" + previousBucketName);
                return false;
            }
        });

       editableText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                int index = (Integer) v.getTag();
                Log.i("App", "Edit Text pos:" + index);
                if(!hasFocus) {
                    BucketBean currentBucket = buckets.get(index);
                    String edit = ((EditText) v).getText().toString();
                    if (!edit.trim().equals("")) {
                        mydb.updateBucket(currentBucket.id, edit, currentBucket.image);
                        //Do not do notifyDataSetChanged(); here - it will screw with editText focus and mess up keyboard
                    } else {
                        mydb.updateBucket(currentBucket.id, previousBucketName, currentBucket.image);
                    }
                }
            }
        });
*/
        return rowView;
    }
}