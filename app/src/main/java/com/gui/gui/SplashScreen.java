package com.gui.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gui.gui.flight_status.FlightMainActivity;
import com.gui.gui.merriam.MerriamMainActivity;
import com.gui.gui.news_feed.NFMainActivity;
import com.gui.gui.ny_times.NYMainActivity;

public class SplashScreen extends Activity {

    Button btnDictionary, btnNewsFeed, btnFlightTracker, btnNYTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        btnDictionary = findViewById(R.id.btnDictionary);
        btnNewsFeed = findViewById(R.id.btnNewsFeed);
        btnFlightTracker = findViewById(R.id.btnFlightTracker);
        btnNYTimes = findViewById(R.id.btnNYTimes);

        btnDictionary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, MerriamMainActivity.class);
                startActivity(intent);
            }
        });
        btnNewsFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, NFMainActivity.class);
                startActivity(intent);
            }
        });
        btnFlightTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, FlightMainActivity.class);
                startActivity(intent);
            }
        });
        btnNYTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, NYMainActivity.class);
                startActivity(intent);
            }
        });

    }
}
