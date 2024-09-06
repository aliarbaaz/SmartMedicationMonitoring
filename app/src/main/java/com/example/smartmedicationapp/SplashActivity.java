package com.example.smartmedicationapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 4000; // 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView splashImage = findViewById(R.id.splash_image);

        // Load GIF using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.splash_animation) // Replace with your GIF resource
                .into(splashImage);

        // Delay to show splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the delay
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close splash activity
            }
        }, SPLASH_DURATION);
    }
}
