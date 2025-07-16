package com.example.studentmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    private static final int SPLASH_TIME = 4000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        // Delay and go to Login page
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME);
    }
}
