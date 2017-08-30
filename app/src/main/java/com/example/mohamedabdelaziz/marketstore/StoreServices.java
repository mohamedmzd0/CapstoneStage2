package com.example.mohamedabdelaziz.marketstore;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Mohamed Abd Elaziz on 8/7/2017.
 */

public class StoreServices extends IntentService {

    public static Boolean service_started = false;
    public static Context context = null;
    static DBHelper db;

    public StoreServices() {
        super("StoreServices");
    }

    public static void GetID() {
        db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        ArrayList<String> ids = db.RestoreFavs();


        for (int i = 0; i < ids.size(); i++) {
            MonitorFavoritesProducts(ids.get(i));
        }
    }

    public static void MonitorFavoritesProducts(final String id) {
        FirebaseDatabase.getInstance().getReference("products").child(id)
                .child("comment").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    try {
                        Comments c = dataSnapshot.getValue(Comments.class);
                        if (!c.id.equals(context.getSharedPreferences("Logged_data", MODE_PRIVATE).getString("email", "").replace("@gmail.com", "")))
                            if ((db.AddComment(id, id + c.comment, c.name, c.comment)) > 0)
                                NotifyUser(c.name);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void NotifyUser(String title) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.ic_action_name)
                .setWhen(System.currentTimeMillis())
                .setContentText(title)
                .setContentTitle(" comment on post you follow")
                .setSound(notification);
        Intent intent = new Intent(context, TabActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1000, builder.build());
    }

    public static boolean read_firebase_posts() {
        try {
            db = new DBHelper(context);
            SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mPostReference = mDatabase.child("products");
            mPostReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot != null) {
                        ProductDataTypes types = dataSnapshot.getValue(ProductDataTypes.class);
                        db.AddProduct(types);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        int x = super.onStartCommand(intent, flags, startId);
        GetID();
        return x;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
