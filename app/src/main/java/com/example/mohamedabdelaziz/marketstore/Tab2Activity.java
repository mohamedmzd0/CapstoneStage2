package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mohamed Abd Elaziz on 8/7/2017.
 */

public class Tab2Activity extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Comments> commentsArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab2, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.notification_recycleview);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DBHelper db = new DBHelper(getContext());
        SQLiteDatabase database = db.getReadableDatabase();
        commentsArrayList = db.GetComments();
        recyclerView.setAdapter(new adpater());
    }

    class adpater extends RecyclerView.Adapter<myholder> {

        @Override
        public myholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row_item, null);
            myholder holder = new myholder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(myholder holder, int p) {
            final int position = commentsArrayList.size() - p - 1;
            holder.name.setText(commentsArrayList.get(position).name);
            holder.message.setText(commentsArrayList.get(position).comment);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.putExtra("user", "other");
                    intent.putExtra("postid", commentsArrayList.get(position).id);
                    startActivity(intent);
                }
            });
            holder.message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentsArrayList.size();
        }
    }

    class myholder extends RecyclerView.ViewHolder {

        TextView name, message;
        View view;

        public myholder(View itemView) {
            super(itemView);
            view = itemView;
            name = (TextView) itemView.findViewById(R.id.tv);
            message = (TextView) itemView.findViewById(R.id.tv2);
        }
    }
}
