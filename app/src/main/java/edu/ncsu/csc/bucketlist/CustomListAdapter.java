package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomListAdapter extends ArrayAdapter<BucketBean> {

    private final Activity context;
    private final HashMap<String, ArrayList<Integer>> hashMap;
    private final ArrayList<BucketBean> buckets;

    public CustomListAdapter(Activity context, ArrayList<BucketBean> buckets, HashMap<String, ArrayList<Integer>> hashMap)
    {
        super(context, R.layout.bucket_listview_layout, buckets);

        this.context = context;
        this.hashMap = hashMap;
        this.buckets = buckets;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.bucket_listview_layout, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        BucketBean bucket = buckets.get(position);
        txtTitle.setText(bucket.name);
        imageView.setImageResource(hashMap.get(bucket.image).get(1));

        return rowView;
    }
}