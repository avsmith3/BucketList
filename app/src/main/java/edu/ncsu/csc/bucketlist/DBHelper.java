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
                        "(id integer primary key autoincrement, googleplusid text, facebookid text)"
        );
        db.execSQL(
                "create table buckets " +
                        "(id integer primary key autoincrement, userid integer, name text, image text)"
        );
        db.execSQL(
                "create table bucketentries " +
                        "(id integer primary key autoincrement, name text, latitude real, longitude real, comment text, rating integer, visited integer, infoTitle text, infoSnippet text)"
        );
        db.execSQL(
                "create table bucketentryassociations " +
                        "(bucketid integer, entryid integer)"
        );
        db.disableWriteAheadLogging();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS buckets");
        db.execSQL("DROP TABLE IF EXISTS bucketentries");
        db.execSQL("DROP TABLE IF EXISTS bucketentryassociations");
        onCreate(db);
    }

/* USERS */

    public long addUser(String googleplusid, String facebookid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("googleplusid", googleplusid);
        contentValues.put("facebookid", facebookid);

        return db.insert("users", null, contentValues);
    }

    public UserBean getUser(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from users where id = ?", new String[] { Long.toString(id) });
        res.moveToFirst();
        if (res.isAfterLast()) return null;
        UserBean bean = new UserBean();
        bean.id = res.getLong(res.getColumnIndex("id"));
        bean.googleplusid = res.getString(res.getColumnIndex("googleplusid"));
        bean.facebookid = res.getString(res.getColumnIndex("facebookid"));
        res.close();
        return bean;
    }

    public UserBean getUserFromGooglePlus(String googleplusid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from users where googleplusid = ?", new String[] { googleplusid });
        res.moveToFirst();
        if (res.isAfterLast()) return null;
        UserBean bean = new UserBean();
        bean.id = res.getLong(res.getColumnIndex("id"));
        bean.googleplusid = res.getString(res.getColumnIndex("googleplusid"));
        bean.facebookid = res.getString(res.getColumnIndex("facebookid"));
        res.close();
        return bean;
    }

    public UserBean getUserFromFacebook(String facebookid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from users where facebookid = ?", new String[] { facebookid });
        res.moveToFirst();
        if (res.isAfterLast()) return null;
        UserBean bean = new UserBean();
        bean.id = res.getLong(res.getColumnIndex("id"));
        bean.googleplusid = res.getString(res.getColumnIndex("googleplusid"));
        bean.facebookid = res.getString(res.getColumnIndex("facebookid"));
        res.close();
        return bean;
    }

/* BUCKETS */

    /**
     * @return The bucket id, or -1 on failure.
     */
    public long addBucket(long userid, String name, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("userid", userid);
        contentValues.put("name", name);
        contentValues.put("image", image);

        return db.insert("buckets", null, contentValues);
    }

    /**
     * @return The number of rows affected. (Hint: zero is error, bucket did not exist.)
     */
    public int updateBucket(long id, String name, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (name != null) contentValues.put("name", name);
        if (image != null) contentValues.put("image", image);
        return db.update("buckets", contentValues, "id = ? ", new String[] { Long.toString(id) });
    }

    /**
     * @return The number of rows affected. (Hint: zero is error, bucket did not exist.)
     */
    public int deleteBucket(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("buckets",
                "id = ? ",
                new String[] { Long.toString(id) });
    }

    public BucketBean getBucket(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from buckets where id = ?", new String[] { Long.toString(id) });
        res.moveToFirst();
        if (res.isAfterLast()) return null;
        BucketBean bean = new BucketBean();
        bean.id = res.getLong(res.getColumnIndex("id"));
        bean.name = res.getString(res.getColumnIndex("name"));
        bean.image = res.getString(res.getColumnIndex("image"));
        res.close();
        return bean;
    }

    /**
     * @return The list of buckets.
     */
    public ArrayList<BucketBean> getAllBucketsForUser(long userid) {
        ArrayList<BucketBean> array_list = new ArrayList<BucketBean>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from buckets where userid=" + userid, null );
        res.moveToFirst();
        while(res.isAfterLast() == false){
            BucketBean bean = new BucketBean();
            bean.id = res.getLong(res.getColumnIndex("id"));
            bean.name = res.getString(res.getColumnIndex("name"));
            bean.image = res.getString(res.getColumnIndex("image"));
            array_list.add(bean);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

/* BUCKET ENTRIES */

    /**
     * @return The entry id, or -1 on failure.
     */
    public long addEntry(String name, double latitude, double longitude, String comment, int rating,
                         int visited, String infoTitle, String infoSnippet) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("comment", comment);
        contentValues.put("rating", rating);
        contentValues.put("visited", visited);
        contentValues.put("infoTitle", infoTitle);
        contentValues.put("infoSnippet", infoSnippet);

        return db.insert("bucketentries", null, contentValues);
    }

    public int updateEntryName(long id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        return db.update("bucketentries", contentValues, "id = ? ", new String[] { Long.toString(id) });
    }

    public int updateEntryCheckBox(long id, int visited) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("visited", visited);
        return db.update("bucketentries", contentValues, "id = ? ", new String[] { Long.toString(id) });
    }

    public int updateEntryRating(long id, int rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rating", rating);
        int result;

        db.beginTransaction();
        result = db.update("bucketentries", contentValues, "id = ? ", new String[] { Long.toString(id) });
        db.setTransactionSuccessful();
        db.endTransaction();

        return result;
    }

    public int updateEntryComment(long id, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("comment", comment);
        return db.update("bucketentries", contentValues, "id = ? ", new String[] { Long.toString(id) });
    }

    /**
     * @return The number of rows affected. (Hint: zero is error, bucket did not exist.)
     */
    public int deleteEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bucketentries",
                "id = ? ",
                new String[] { Long.toString(id) });
    }

    public EntryBean getEntry(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from bucketentries where id = ?", new String[] { Long.toString(id) });
        res.moveToFirst();
        if (res.isAfterLast()) return null;
        EntryBean bean = new EntryBean();
        bean.id = res.getLong(res.getColumnIndex("id"));
        bean.name = res.getString(res.getColumnIndex("name"));
        bean.latitude = res.getDouble(res.getColumnIndex("latitude"));
        bean.longitude = res.getDouble(res.getColumnIndex("longitude"));
        bean.comment = res.getString(res.getColumnIndex("comment"));
        bean.rating = res.getInt(res.getColumnIndex("rating"));
        bean.visited = res.getInt(res.getColumnIndex("visited"));
        bean.infoTitle = res.getString(res.getColumnIndex("infoTitle"));
        bean.infoSnippet = res.getString(res.getColumnIndex("infoSnippet"));
        res.close();
        return bean;
    }

    /**
     * @return The list of entries.
     */
    public ArrayList<EntryBean> getEntriesFor(long bucketid) {
        ArrayList<EntryBean> array_list = new ArrayList<EntryBean>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select * from bucketentries left join bucketentryassociations where bucketentryassociations.bucketid = ? and bucketentryassociations.entryid = bucketentries.id", new String[] { Long.toString(bucketid) });
        res.moveToFirst();
        while(res.isAfterLast() == false){
            EntryBean bean = new EntryBean();
            bean.id = res.getLong(res.getColumnIndex("id"));
            bean.name = res.getString(res.getColumnIndex("name"));
            bean.latitude = res.getDouble(res.getColumnIndex("latitude"));
            bean.longitude = res.getDouble(res.getColumnIndex("longitude"));
            bean.comment = res.getString(res.getColumnIndex("comment"));
            bean.rating = res.getInt(res.getColumnIndex("rating"));
            bean.visited = res.getInt(res.getColumnIndex("visited"));
            bean.infoTitle = res.getString(res.getColumnIndex("infoTitle"));
            bean.infoSnippet = res.getString(res.getColumnIndex("infoSnippet"));
            array_list.add(bean);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    /**
     * @return The list of buckets.
     */
    public ArrayList<EntryBean> getTopEntriesForUser(long userid, int n) {
        ArrayList<EntryBean> array_list = new ArrayList<EntryBean>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "select bucketentries.* from bucketentries left join bucketentryassociations left join buckets where bucketentries.visited != 0 and bucketentryassociations.bucketid = buckets.id and buckets.userid = ? and bucketentryassociations.entryid = bucketentries.id ORDER BY bucketentries.rating DESC LIMIT ?", new String[] { Long.toString(userid), Integer.toString(n) });
        res.moveToFirst();
        while(res.isAfterLast() == false){
            EntryBean bean = new EntryBean();
            bean.id = res.getLong(res.getColumnIndex("id"));
            bean.name = res.getString(res.getColumnIndex("name"));
            bean.latitude = res.getDouble(res.getColumnIndex("latitude"));
            bean.longitude = res.getDouble(res.getColumnIndex("longitude"));
            bean.comment = res.getString(res.getColumnIndex("comment"));
            bean.rating = res.getInt(res.getColumnIndex("rating"));
            bean.visited = res.getInt(res.getColumnIndex("visited"));
            bean.infoTitle = res.getString(res.getColumnIndex("infoTitle"));
            bean.infoSnippet = res.getString(res.getColumnIndex("infoSnippet"));
            array_list.add(bean);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

/* BUCKET ENTRY ASSOCIATIONS */

    public void addToBucket(long entryid, long bucketid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("entryid", entryid);
        contentValues.put("bucketid", bucketid);

        db.insert("bucketentryassociations", null, contentValues);
    }

    public int removeFromBucket(long entryid, long bucketid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bucketentryassociations",
                "entryid = ? and bucketid = ? ",
                new String[] { Long.toString(entryid), Long.toString(bucketid) });
    }

    public int removeAllFromBucket(long bucketid) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bucketentryassociations",
                "bucketid = ? ",
                new String[] { Long.toString(bucketid) });
    }

}
