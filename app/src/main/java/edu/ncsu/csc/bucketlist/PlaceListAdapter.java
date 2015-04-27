package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaceListAdapter extends ArrayAdapter<EntryBean> {

    private final Activity context;
    private final ArrayList<EntryBean> entries;
    private boolean inEditMode;
    private DBHelper mydb;

    public PlaceListAdapter(Activity context, ArrayList<EntryBean> entries)
    {
        super(context, R.layout.displaybucket_listview, entries);

        this.context = context;
        this.entries = entries;
        inEditMode = false;
        mydb = new DBHelper(getContext());
    }

    public void setMode(boolean mode) {
        inEditMode = mode;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.displaybucket_listview, null, true);

        CheckBox visitedBox = (CheckBox) rowView.findViewById(R.id.visited_checkBox);
        TextView place = (TextView) rowView.findViewById(R.id.list_item_place);
        ImageButton deletePlaceBtn = (ImageButton) rowView.findViewById(R.id.deletePlaceBtn);
        deletePlaceBtn.setTag(position);

        EntryBean entry = entries.get(position);
        place.setText(entry.name);
        if (entry.visited == 0) {
           visitedBox.setChecked(false);
        } else if (entry.visited == 1) {
            visitedBox.setChecked(true);
        }

        if (inEditMode) {
            deletePlaceBtn.setVisibility(View.VISIBLE);
        } else {
            deletePlaceBtn.setVisibility(View.GONE);
        }

        deletePlaceBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int index = (Integer)v.getTag();
                Log.i("App", "Delete Button clicked Position:" + index);
                EntryBean currentEntry = entries.get(index);
                // remove item from database
                mydb.deleteEntry(currentEntry.id);
                // remove item from listview
                entries.remove(index);
                notifyDataSetChanged();
            }

        });

        return rowView;
    }
}