package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ProductActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText desc, price;
    TextView num_of_charc;
    int charchters = 50;
    ImageView gallaryImage;
    ArrayList<Recylce_dataype> data;
    String date, time;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        int temp = getWindowManager().getDefaultDisplay().getWidth();
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), ((int) temp / 150)));
        recyclerView.getLayoutParams().height = (temp / 150) * 40;
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(new Recycleviewadapter());
        desc = (EditText) findViewById(R.id.desc);
        price = (EditText) findViewById(R.id.price);
        gallaryImage = (ImageView) findViewById(R.id.gallaryImage);
        data = new ArrayList<>();
        data.add(new Recylce_dataype("Cars", true));
        data.add(new Recylce_dataype("Phones", false));
        data.add(new Recylce_dataype("Pcs", false));
        data.add(new Recylce_dataype("Houses", false));
        data.add(new Recylce_dataype("Other", false));
        num_of_charc = (TextView) findViewById(R.id.numb_char);
        gallaryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                num_of_charc.setText((charchters - s.length()) + "/50");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Date d = new Date();
        date = d.getDate() + " / " + d.getMonth() + " / " + ("" + (d.getYear())).replaceFirst("1", "20");
        time = d.getSeconds() + " : " + d.getMinutes() + " : " + d.getHours();
    }

    private void upload_product_data(String imageName) {
        String type = "other";
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).selected) {
                type = data.get(i).text;
            }
        }
        ArrayList<Followers> f = new ArrayList<>();
        f.add(new Followers("", ""));
        ArrayList<Comments> c = new ArrayList<>();
        c.add(new Comments("", getString(R.string.start), ""));

        ProductDataTypes productDataTypes = new ProductDataTypes(c, date, desc.getText().toString(), f, imageName,
                getApplicationContext().getSharedPreferences("Logged_data", MODE_PRIVATE).getString("url", "non"),
                getApplicationContext().getSharedPreferences("Logged_data", MODE_PRIVATE).getString("name", "non"),
                getApplicationContext().getSharedPreferences("Logged_data", MODE_PRIVATE).getString("email", "non").replace("@gmail.com", "")
                , price.getText().toString()
                , time, type);
        try {
            FirebaseDatabase fireDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = fireDatabase.getReference("products");
            String temp = (productDataTypes.ownerid + "-" + productDataTypes.date + productDataTypes.time).trim()
                    .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", "");
            Log.d("", temp);
            myRef.child(temp)
                    .setValue(productDataTypes);
            finish();
        } catch (Exception e) {
        }
    }

    public void check_valid_inputs() {
        if (desc.getText().toString().isEmpty()) {
            desc.setError(getString(R.string.desc));
            return;
        }
        if (price.getText().toString().isEmpty()) {
            price.setError(getString(R.string.price));
            return;
        }
        uploadImage();
    }

    public void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/png");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pic)), PICK_IMAGE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send) {
            if (new NetworkCheck().isOnline(getApplicationContext()))
                check_valid_inputs();
            else
                Toast.makeText(this, R.string.connection, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setgroud(int position) {
        for (int i = 0; i < data.size(); i++) {
            if (i == position && data.get(position).selected == false) {
                data.get(i).selected = true;
            } else
                data.get(i).selected = false;
        }
        recyclerView.setAdapter(new Recycleviewadapter());
    }

    public void uploadImage() {
        desc.setEnabled(false);
        price.setEnabled(false);
        recyclerView.setEnabled(false);
        Toast.makeText(this, R.string.start_up, Toast.LENGTH_SHORT).show();
        gallaryImage.setDrawingCacheEnabled(true);
        gallaryImage.buildDrawingCache();
        Bitmap bitmap = gallaryImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        final String postID = getApplicationContext().getSharedPreferences("Logged_data", MODE_PRIVATE).getString("email", "non").replace("@gmail.com", "") +
                "-" + (date + time).trim()
                .replaceAll(":", "").replaceAll("/", "").replaceAll("\\s", "");
        UploadTask uploadTask = FirebaseStorage.getInstance().getReference(postID).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ProductActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                upload_product_data(postID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                gallaryImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Recycleviewadapter extends RecyclerView.Adapter<RecycleHolder> {
        @Override
        public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_product_item, null);
            RecycleHolder holder = new RecycleHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecycleHolder holder, final int position) {
            holder.radioButton.setText(data.get(position).text);
            holder.radioButton.setChecked(data.get(position).selected);
            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setgroud(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    class RecycleHolder extends RecyclerView.ViewHolder {
        View view;
        RadioButton radioButton;

        public RecycleHolder(View itemView) {
            super(itemView);
            view = itemView;
            radioButton = (RadioButton) view.findViewById(R.id.grid_button);
        }
    }

    class Recylce_dataype {
        String text;
        boolean selected;

        Recylce_dataype(String text, boolean selected) {
            this.text = text;
            this.selected = selected;
        }
    }
}
