package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView username, email, phone, work, address;
    ImageView profile;
    LinearLayout call_layout;
    Display display;
    int screen_width;
    RecyclerView recyclerView;
    String owner;
    DBHelper helper;
    String gmail;
    private ArrayList<ProductDataTypes> productDataTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        address = (TextView) findViewById(R.id.address);
        work = (TextView) findViewById(R.id.work);
        recyclerView = (RecyclerView) findViewById(R.id.ProfileRecycleview);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        display = getWindowManager().getDefaultDisplay();
        screen_width = display.getWidth();
        helper = new DBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        call_layout = (LinearLayout) findViewById(R.id.call_layout);
        call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
        profile = (ImageView) findViewById(R.id.profile_image);
        if ((getIntent().getStringExtra("user")).equals("mine")) {
            username.setText(getIntent().getStringExtra("name"));
            phone.setText(getIntent().getStringExtra("phone"));
            gmail = getIntent().getStringExtra("email");
            email.setText(gmail);
            work.setText(getIntent().getStringExtra("work"));
            address.setText(getIntent().getStringExtra("address"));
            owner = getIntent().getStringExtra("owner");
            get_this_user_activities();
        } else {
            getUserData(getIntent().getStringExtra("postid"));
        }
        try {
            Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("url").replace("s96-c/photo.jpg", "s" + screen_width + "-c/photo.jpg")).into(profile);
        } catch (Exception e) {
        }
        ((ScrollView) findViewById(R.id.mainScroll)).smoothScrollTo(0, 0);
    }


    public void get_this_user_activities() {
        productDataTypes = new ArrayList<>();
        ArrayList<ProductDataTypes> Allproductarray = new DBHelper(getApplicationContext()).RestoreProducts();
        for (int i = 0; i < Allproductarray.size(); i++) {
            if (Allproductarray.get(i).ownerid.equals(gmail.replace("@gmail.com", "")))
                productDataTypes.add(Allproductarray.get(i));
        }
        recyclerView.setAdapter(new Recycleadapter());
    }

    private void getUserData(String postid) {
        StringBuffer userID = new StringBuffer();
        if (postid == null)
            return;
        char[] charArray = postid.toCharArray();
        int i = 0;
        while (charArray[i] != '-' && i < charArray.length) {
            userID.append(charArray[i]);
            i++;
        }
        Toast.makeText(this, "" + userID.toString(), Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference("users").child(userID.toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    UserDataTypes user = dataSnapshot.getValue(UserDataTypes.class);
                    username.setText(user.getName());
                    phone.setText(user.getPhone());
                    email.setText(user.getEmail());
                    work.setText(user.getWork());
                    address.setText(user.getAddress());
                    owner = user.getEmail().replace("@gmail.com", "");
                    gmail = owner;
                    get_this_user_activities();
                    try {
                        Picasso.with(getApplicationContext()).load(user.ProfileImage.replace("s96-c/photo.jpg", "s" + screen_width + "-c/photo.jpg")).into(profile);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void call() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone.getText()));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
    }

    public void finishActivity(View view) {
        finish();
    }

    class Recycleadapter extends RecyclerView.Adapter<RecycleHolder> {
        @Override
        public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycleview, null);
            RecycleHolder holder = new RecycleHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecycleHolder holder, int p) {
            final int position = productDataTypes.size() - p - 1;
            if (productDataTypes.get(position).ownerid.equals(owner)) {
                holder.name.setText(productDataTypes.get(position).ownerName);
                holder.date.setText(productDataTypes.get(position).date + " , " + productDataTypes.get(position).time);
                holder.price.setText(productDataTypes.get(position).price + " $$");

                Picasso.with(getApplicationContext()).load(productDataTypes.get(position).ownerImage).into(holder.circleImageView);
                FirebaseStorage.getInstance().getReference(productDataTypes.get(position).image)
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.with(getApplicationContext()).load(uri).into(holder.imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });


                final int x = position;
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(ProfileActivity.this, holder.imageView, "mytest");
                        Intent intent = new Intent(getApplicationContext(), Details.class);
                        intent.putExtra("index", x);
                        startActivity(intent, compat.toBundle());

                    }
                });
                holder.comment.setText(productDataTypes.get(position).comment.size() + getString(R.string.comments));
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), AddCommnet.class);
                        intent.putExtra("id", productDataTypes.get(position).ownerid);
                        intent.putExtra("time", productDataTypes.get(position).time);
                        intent.putExtra("date", productDataTypes.get(position).date);
                        startActivity(intent);
                    }
                });
                if (helper.exists(productDataTypes.get(position).ownerid, productDataTypes.get(position).date
                        , productDataTypes.get(position).time)) {
                    holder.follow.setText(R.string.following);
                } else {
                    holder.follow.setText(R.string.follow);
                }

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.follow.getText().equals("follow")) {
                            helper.insert_fav(productDataTypes.get(position).ownerid, productDataTypes.get(position).date
                                    , productDataTypes.get(position).time);
                            holder.follow.setText(R.string.following);
                        }
                        StoreServices.GetID();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return productDataTypes.size();
        }
    }

    class RecycleHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CircleImageView circleImageView;
        TextView name, date, price;
        Button comment, follow;

        public RecycleHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_home);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profile_image);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.date);
            price = (TextView) itemView.findViewById(R.id.price);
            comment = (Button) itemView.findViewById(R.id.comment);
            follow = (Button) itemView.findViewById(R.id.follow);
        }
    }
}
