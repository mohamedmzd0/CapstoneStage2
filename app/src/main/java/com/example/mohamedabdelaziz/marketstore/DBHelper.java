package com.example.mohamedabdelaziz.marketstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Mohamed Abd Elaziz on 8/15/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    final String table = " fav ", postid = " id ";
    final String table1 = "allproducts", image = "image", desc = "desc",
            price = "price", type = "type", ownerid = "onID", ownerImage = "ownIg", ownerName = "ownNm", date = "d", time = "t";
    final String table2 = "comments", postID = "postID", commentID = "commentID", name = "name", comment = "comment";
    Context context;

    public DBHelper(Context context) {
        super(context, "db", null, 2);
        this.context = context;
    }

    ////   the same user can't upload more than one post at the same time , i can use the user id +time as an unique value
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + table + " ( " + postid + " VARCHAR(80) PRIMARY KEY  ) ");
        db.execSQL("CREATE TABLE " + table1 + " ( " + postid + " VARCHAR(60) PRIMARY KEY , " + date + " VARCHAR(30) , " + image + " VARCHAR(100) , " + desc + " VARCHAR(30) , " +
                ownerImage + " VARCHAR(50) , " + ownerName + " VARCHAR(50) , " + ownerid + " VARCHAR(50) , " + price + " VARCHAR(30) , " + time + " VARCHAR(30) , " + type + " VARCHAR(20) ) ");
        db.execSQL("CREATE TABLE " + table2 + " ( " + postID + " VARCHAR(80), " + commentID + " VARCHAR(100) PRIMARY KEY , " + name + " VARCHAR(30) , " + comment + " VARCHAR(100) ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + table);
        db.execSQL("DROP TABLE IF EXISTS " + table2);
        onCreate(db);
    }

    public void AddProduct(ProductDataTypes productDataTypes) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String pstID = (productDataTypes.ownerid + "-" + productDataTypes.date + productDataTypes.time).trim()
                .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", "");
        AddComment(pstID, productDataTypes.comment);
        cv.put(postid, pstID);
        cv.put(date, productDataTypes.date);
        cv.put(desc, productDataTypes.desc);
        cv.put(ownerid, productDataTypes.ownerid);
        cv.put(ownerName, productDataTypes.ownerName);
        cv.put(ownerImage, productDataTypes.ownerImage);
        cv.put(image, productDataTypes.image);
        cv.put(price, productDataTypes.price);
        cv.put(time, productDataTypes.time);
        cv.put(type, productDataTypes.type);
        sqLiteDatabase.insert(table1,null,cv) ;
    }

    public ArrayList RestoreProducts() {
        ArrayList<ProductDataTypes> dataTypes = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table1, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            String pstID = (cursor.getString(cursor.getColumnIndex(ownerid)) + "-" + cursor.getString(cursor.getColumnIndex(date)) + cursor.getString(cursor.getColumnIndex(time))).trim()
                    .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", "");
            ArrayList<Followers> follower = new ArrayList<>();
            ArrayList<Comments> comment = GetComment(pstID);
            dataTypes.add(new ProductDataTypes(comment, cursor.getString(cursor.getColumnIndex(date)), cursor.getString(cursor.getColumnIndex(desc))
                    , follower, cursor.getString(cursor.getColumnIndex(image)), cursor.getString(cursor.getColumnIndex(ownerImage))
                    , cursor.getString(cursor.getColumnIndex(ownerName)), cursor.getString(cursor.getColumnIndex(ownerid))
                    , cursor.getString(cursor.getColumnIndex(price)), cursor.getString(cursor.getColumnIndex(time))
                    , cursor.getString(cursor.getColumnIndex(type))));
            cursor.moveToNext();
        }
        cursor.close();
        return dataTypes;
    }

    public void AddComment(String id, ArrayList<Comments> commentsArrayList) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < commentsArrayList.size(); i++) {
            cv.put(postID, id);
            cv.put(name, commentsArrayList.get(i).name);
            cv.put(commentID, commentsArrayList.get(i).id + commentsArrayList.get(i).comment);
            cv.put(comment, commentsArrayList.get(i).comment);
            long x = sqLiteDatabase.insert(table2, null, cv);
        }
    }


    public ArrayList GetComment(String postid) {
        ArrayList<Comments> commentses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table2 + " WHERE " + postID + " = ? ", new String[]{postid});
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            commentses.add(new Comments(cursor.getString(cursor.getColumnIndex(postID))
                    , cursor.getString(cursor.getColumnIndex(name))
                    , cursor.getString(cursor.getColumnIndex(comment))));
            cursor.moveToNext();
        }
        cursor.close();
        return commentses;
    }

    public ArrayList GetComments() {
        ArrayList<Comments> commentses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table2, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            commentses.add(new Comments(cursor.getString(cursor.getColumnIndex(postID))
                    , cursor.getString(cursor.getColumnIndex(name))
                    , cursor.getString(cursor.getColumnIndex(comment))));
            cursor.moveToNext();
        }
        cursor.close();
        return commentses;
    }

    public long insert_fav(String id, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(postid, (id + "-" + ((date + time).trim()
                .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", ""))));
        long x = db.insert(table, null, values);
        return x;
    }

    public boolean exists(String id, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        cursor.moveToFirst();
        if (cursor.isAfterLast())
            return false;
        else {
            while (cursor.isAfterLast() == false) {
                if (cursor.getString(0).toString().equals((id + "-" + ((date + time).trim()
                        .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", "")))))
                    return true;
                cursor.moveToNext();
            }
        }
        return false;
    }

    public ArrayList RestoreFavs() {
        ArrayList<String> ids = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            ids.add(cursor.getString(0));
            cursor.moveToNext();
        }
        return ids;
    }

    public long AddComment(String id, String commentid, String name, String comment) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(postID, id);
        cv.put(name, name);
        cv.put(commentID, commentid);
        cv.put(comment, comment);
        long x = sqLiteDatabase.insert(table2, null, cv);
        return x;
    }
}