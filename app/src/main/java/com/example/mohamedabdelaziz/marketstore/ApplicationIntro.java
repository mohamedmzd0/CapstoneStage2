package com.example.mohamedabdelaziz.marketstore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;


public class ApplicationIntro extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("Sell Your Pieces", "Easily At Home", R.drawable.car, Color.parseColor("#783393")));
        addSlide(AppIntroFragment.newInstance("Communicate With Others", "Just One Click", R.drawable.smartphone, Color.parseColor("#783393")));
        addSlide(AppIntroFragment.newInstance("Let's Try", "", R.drawable.target, Color.parseColor("#783393")));
        setBarColor(getResources().getColor(R.color.colorPrimary));
        setSeparatorColor(Color.parseColor("#2196F3"));
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        goto_main();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goto_main();
    }

    private void goto_main() {
        //startActivity(new Intent(getApplicationContext(),TabActivity.class));
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
