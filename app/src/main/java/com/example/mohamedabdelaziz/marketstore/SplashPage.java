package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);
        NetworkCheck networkCheck = new NetworkCheck();
        if (networkCheck.isOnline(getApplicationContext())) {
            if (!StoreServices.service_started || StoreServices.context == null) {
                startService(new Intent(getApplicationContext(), StoreServices.class));
                StoreServices.context = getApplicationContext();
            }
        }
        StoreServices.read_firebase_posts();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("Logged_data", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("logged", false)) {
                    startActivity(new Intent(getApplicationContext(), TabActivity.class));
                } else
                    startActivity(new Intent(getApplicationContext(), ApplicationIntro.class));

                finish();
            }
        }, 1500);
    }
}
