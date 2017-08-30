package com.example.mohamedabdelaziz.marketstore;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mohamed Abd Elaziz on 8/7/2017.
 */

public class Tab1Activity extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    DBHelper helper;
    private SearchView searchView;
    private ArrayList<ProductDataTypes> Allproductarray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.homeRecycleview);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setHasFixedSize(false);
        searchView = (SearchView) view.findViewById(R.id.search_vw);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        ArrayList<Followers> f = new ArrayList<>();
        ArrayList<Comments> c = new ArrayList<>();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ProductActivity.class));
            }
        });
        helper = new DBHelper(getContext());
        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
        if (FirebaseDatabase.getInstance() == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            DatabaseReference products = FirebaseDatabase.getInstance().getReference("products");
            products.keepSynced(true);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty())
                    new SearchAsync().execute(newText);
                else
                    recyclerView.setAdapter(new Recycleadapter(Allproductarray));
                return false;
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        view_stored_data();
    }

    private void view_stored_data() {
        DBHelper db = new DBHelper(getContext());
        try {
            Allproductarray = db.RestoreProducts();
            recyclerView.setAdapter(new Recycleadapter(Allproductarray));
        } catch (Exception e) {
        }
    }

    class Recycleadapter extends RecyclerView.Adapter<RecycleHolder> {
        ArrayList<ProductDataTypes> arrayList;

        public Recycleadapter(ArrayList<ProductDataTypes> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_recycleview, null);
            RecycleHolder holder = new RecycleHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecycleHolder holder, int p) {
            final int position = arrayList.size() - p - 1;
            if(p==arrayList.size()-1)
                progressBar.setVisibility(View.INVISIBLE);
            if (helper.exists(arrayList.get(position).ownerid, arrayList.get(position).date
                    , arrayList.get(position).time)) {
                holder.follow.setText(R.string.following);
            } else {
                holder.follow.setText(R.string.follow);
            }


            holder.follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.follow.getText().equals("follow")) {
                        helper.insert_fav(arrayList.get(position).ownerid, arrayList.get(position).date
                                , arrayList.get(position).time);
                        holder.follow.setText(R.string.following);
                    }
                    StoreServices.GetID();
                }
            });

            holder.name.setText(arrayList.get(position).ownerName);
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AddCommnet.class);
                    intent.putExtra("id", arrayList.get(position).ownerid);
                    intent.putExtra("time", arrayList.get(position).time);
                    intent.putExtra("date", arrayList.get(position).date);
                    startActivity(intent);
                }
            });
            holder.date.setText(arrayList.get(position).date + " , " + arrayList.get(position).time);
            holder.price.setText(arrayList.get(position).price + " $$");

            Picasso.with(getContext()).load(arrayList.get(position).ownerImage).into(holder.circleImageView);

            FirebaseStorage.getInstance().getReference(arrayList.get(position).image)
                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext()).load(uri).into(holder.imageView);
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
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), holder.imageView, "mytest");
                    Intent intent = new Intent(getContext(), Details.class);
                    intent.putExtra("index", x);
                    getContext().startActivity(intent, compat.toBundle());
                }
            });
        }

        @Override
        public int getItemCount() {
            if(arrayList.size()==0) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), R.string.nodata, Toast.LENGTH_SHORT).show();
            }
            return arrayList.size();
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

    class SearchAsync extends AsyncTask<String, Void, Void> {
        ArrayList<ProductDataTypes> temp = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            for (int i = 0; i < Allproductarray.size(); i++) {
                if (Allproductarray.get(i).ownerName.contains(params[0]) || Allproductarray.get(i).ownerid.contains(params[0]) ||
                        Allproductarray.get(i).type.contains(params[0]) || Allproductarray.get(i).price.contains(params[0])) {
                    temp.add(Allproductarray.get(i));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(new Recycleadapter(temp));
        }
    }

}
