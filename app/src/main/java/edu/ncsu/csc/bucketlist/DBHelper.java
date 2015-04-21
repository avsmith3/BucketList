package edu.ncsu.csc.bucketlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "BucketLists.db";

    public DBHelper(Context context){
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(
                "create table users " +
                        "(id integer primary key autoincrement, googleplusid text)"
        );
        db.execSQL(
                "create table buckets " +
                        "(id integer primary key autoincrement, userid integer, name text, image text)"
        );
        db.execSQL(
                "create table bucketentries " +
                        "(id integer primary key autoincrement, name text, latitude text, longitude text, comment text, rating integer, visited boolean)"
        );
        db.execSQL(
                "create table bucketentryassociations " +
                        "(bucketid integer, entryid integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS buckets");
        db.execSQL("DROP TABLE IF EXISTS bucketentries");
        db.execSQL("DROP TABLE IF EXISTS bucketentryassociations");
        onCreate(db);
    }

    /**
     * @return The bucket id, or -1 on failure.
     */
    public long addBucket(int userid, String name, String image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("userid", latitude);
        contentValues.put("name", name);
        contentValues.put("image", image);

        return db.insert("buckets", null, contentValues);
    }

    /**
     * @return The number of rows affected. (Hint: zero is error, bucket did not exist.)
     */
    public int updateBucket(long id, String name, String image)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (name != null) contentValues.put("name", name);
        if (image != null) contentValues.put("image", image);
        return db.update("buckets", contentValues, "id = ? ", new String[] { Long.toString(id) });
    }

    /**
     * @return The number of rows affected. (Hint: zero is error, bucket did not exist.)
     */
    public int deleteBucket(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("buckets",
                "id = ? ",
                new String[] { Long.toString(id) });
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from buckets where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BUCKETS_TABLE_NAME);
        return numRows;
    }

    public ArrayList<BucketBean> getAllBucketsForUser(long userid)
    {
        ArrayList<BucketBean> array_list = new ArrayList();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from buckets where userid=" + userid, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            BucketBean bean = new BucketBean();
            bean.id = res.getLong(res.getColumnIndex("id"));
            bean.name = res.getString(res.getColumnIndex("name"));
            bean.image = res.getString(res.getColumnIndex("image"));
            array_list.add(bean);
            res.moveToNext();
        }
        return array_list;
    }
}
