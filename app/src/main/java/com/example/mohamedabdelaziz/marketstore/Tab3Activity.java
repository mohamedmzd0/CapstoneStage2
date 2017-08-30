package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Mohamed Abd Elaziz on 8/7/2017.
 */

public class Tab3Activity extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tab3, container, false);
        view.findViewById(R.id.Profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myprofile();
            }
        });
        view.findViewById(R.id.Write_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProductActivity.class));
            }
        });

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signOut()) {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("logged", false);
                    editor.commit();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    getActivity().finish();
                } else
                    Toast.makeText(getContext(), R.string.cantup, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void myprofile() {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra("user", "mine");
        intent.putExtra("name", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("name", "non"));
        intent.putExtra("phone", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("phone", "phone not found"));
        intent.putExtra("email", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("email", "emil not found"));
        intent.putExtra("url", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("url", ""));
        intent.putExtra("work", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("work", "non"));
        intent.putExtra("address", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("address", "non"));
        intent.putExtra("owner", getContext().getSharedPreferences("Logged_data", getContext().MODE_PRIVATE).getString("email", "emil not found").replace("@gmail.com", ""));
        startActivity(intent);

    }

    private boolean signOut() {
        try {
            FirebaseAuth.getInstance().signOut();
            return true;
        } catch (Exception e) {
        }
        return false;
    }
}