package edu.ncsu.csc.bucketlist;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlaceListAdapter extends ArrayAdapter<EntryBean> {

    private final Activity context;
    private final ArrayList<EntryBean> entries;
    private long bucketId;
    private boolean inEditMode;
    private DBHelper mydb;

    public PlaceListAdapter(Activity context, ArrayList<EntryBean> entries, long bucketId)
    {
        super(context, R.layout.displaybucket_listview, entries);

        this.context = context;
        this.entries = entries;
        this.bucketId = bucketId;
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
        visitedBox.setTag(position);
        TextView place = (TextView) rowView.findViewById(R.id.list_item_place);
        EditText editableText = (EditText) rowView.findViewById(R.id.list_item_place_edit);
        editableText.setTag(position);
        ImageButton deletePlaceBtn = (ImageButton) rowView.findViewById(R.id.deletePlaceBtn);
        deletePlaceBtn.setTag(position);

        EntryBean entry = entries.get(position);
        place.setText(entry.name);
        editableText.setText(entry.name);

        if (entry.visited == 0) {
           visitedBox.setChecked(false);
        } else if (entry.visited == 1) {
            visitedBox.setChecked(true);
        }

        if (inEditMode) {
            place.setVisibility(View.GONE);
            editableText.setVisibility(View.VISIBLE);
            deletePlaceBtn.setVisibility(View.VISIBLE);
        } else {
            place.setVisibility(View.VISIBLE);
            editableText.setVisibility(View.GONE);
            deletePlaceBtn.setVisibility(View.GONE);
        }

        editableText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    int index = (Integer) v.getTag();
                    Log.i("App", "Edit Text pos:" + index);
                    EntryBean currentEntry = entries.get(index);
                    String edit = ((EditText) v).getText().toString().trim();
                    if (!edit.equals("")) {
                        mydb.updateEntryName(currentEntry.id, edit);
                    }
                }
                return false;
            }
        });

        visitedBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox box = (CheckBox)v;
                int index = (Integer)v.getTag();
                Log.i("App", "Checkbox clicked Position:" + index);
                EntryBean currentEntry = entries.get(index);
                if (box.isChecked()) {
                    mydb.updateEntryCheckBox(currentEntry.id, 1);
                } else {
                    mydb.updateEntryCheckBox(currentEntry.id, 0);
                    // remove rating and comment if place  was previously checked
                    mydb.updateEntryRating(currentEntry.id, 0);
                    mydb.updateEntryComment(currentEntry.id, "");
                }
            }
        });

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
                // remove association
                mydb.removeFromBucket(currentEntry.id, bucketId);
                // remove item from listview
                entries.remove(index);
                notifyDataSetChanged();
            }

        });

        return rowView;
    }
}