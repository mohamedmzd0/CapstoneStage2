package com.example.mohamedabdelaziz.marketstore;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Details extends AppCompatActivity {
    float scall = 0;
    int index = 0;
    RecyclerView recyclerView;
    private ArrayList<ProductDataTypes> Allproductarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        index = getIntent().getIntExtra("index", 0);
        Log.d("index_of_post", "" + index);
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        Allproductarray = new DBHelper(getApplicationContext()).RestoreProducts();
        FirebaseStorage.getInstance().getReference(Allproductarray.get(index).image)
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
        ((TextView) findViewById(R.id.desc)).setText(Allproductarray.get(index).desc);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scall < 3) {
                    scall++;
                } else
                    scall = 1;
                imageView.setScaleX(scall);
                imageView.setScaleY(scall);
            }
        });
        ((TextView) findViewById(R.id.price)).setText(Allproductarray.get(index).price + " $$");
        recyclerView = (RecyclerView) findViewById(R.id.comments_recycleview);
        recyclerView.setAdapter(new Recycleadapter());
    }

    class Recycleadapter extends RecyclerView.Adapter<RecycleHolder> {
        @Override
        public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_item, null);
            RecycleHolder holder = new RecycleHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecycleHolder holder, int p) {
            int position = Allproductarray.get(index).comment.size() - p - 1;
            holder.tv1.setText(Allproductarray.get(index).comment.get(position).name);
            holder.tv2.setText(Allproductarray.get(index).comment.get(position).comment);
        }

        @Override
        public int getItemCount() {
            return Allproductarray.get(index).comment.size();
        }
    }

    class RecycleHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2;

        public RecycleHolder(View itemView) {
            super(itemView);
            tv1 = (TextView) itemView.findViewById(R.id.tv);
            tv2 = (TextView) itemView.findViewById(R.id.tv2);
        }
    }

}
