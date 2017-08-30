package com.example.mohamedabdelaziz.marketstore;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

public class AddCommnet extends AppCompatActivity {

    String id, time, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_commnet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id = getIntent().getStringExtra("id");
        time = getIntent().getStringExtra("time");
        date = getIntent().getStringExtra("date");
        Log.d("id", id);
        Log.d("time", time);
        Log.d("date", date);
        findViewById(R.id.butt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addcomment();
            }
        });
    }

    private void addcomment() {
        FirebaseDatabase.getInstance().getReference("products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    ProductDataTypes types = dataSnapshot.getValue(ProductDataTypes.class);
                    if (types.ownerid.equals(id) && types.time.equals(time) && types.date.equals(date)) {
                        String temp1 = ((EditText) findViewById(R.id.comm)).getText().toString();
                        if (temp1.isEmpty())
                            return;
                        Comments c = new Comments(getSharedPreferences("Logged_data", MODE_PRIVATE).getString("email", "").replace("@gmail.com", "")
                                , getSharedPreferences("Logged_data", MODE_PRIVATE).getString("name", ""), temp1);
                        String temp = dataSnapshot.getKey();
                        FirebaseDatabase.getInstance().getReference("products").child(temp).child("comment").child(
                                types.comment.size() + "").setValue(c);
                        finish();
                        if (new NetworkCheck().isOnline(getApplicationContext()))
                            Toast.makeText(AddCommnet.this, R.string.added, Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddCommnet.this, R.string.will_add, Toast.LENGTH_SHORT).show();
                        return;
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


}
