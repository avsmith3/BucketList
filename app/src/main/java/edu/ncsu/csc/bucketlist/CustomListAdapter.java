package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fitness.data.Bucket;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListAdapter extends ArrayAdapter<BucketBean> {

    private final Activity context;
    private final HashMap<String, ArrayList<Integer>> hashMap;
    private final ArrayList<BucketBean> buckets;
    private boolean inEditMode;
    private DBHelper mydb;

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

        TextView titleText = (TextView) rowView.findViewById(R.id.list_item_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_item_img);
        EditText editableText = (EditText) rowView.findViewById(R.id.list_item_edit_name);
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


        return rowView;
    }
}