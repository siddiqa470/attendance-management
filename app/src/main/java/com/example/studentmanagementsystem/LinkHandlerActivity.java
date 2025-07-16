package com.example.studentmanagementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LinkHandlerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔗 Get the deep link URI
        Uri data = getIntent().getData();

        if (data != null && "attendapp".equals(data.getScheme())) {
            String className = data.getQueryParameter("class");
            String subjectName = data.getQueryParameter("subject");

            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int userId = prefs.getInt("user_id", -1);
            String role = prefs.getString("role", null);

            Intent intent;

            if (userId != -1 && "student".equals(role)) {
                // ✅ Already logged in as student → go to StudentActivity and mark attendance
                intent = new Intent(this, StudentActivity.class);
                intent.putExtra("mark_attendance", true);
                intent.putExtra("class_name", className);
                intent.putExtra("subject_name", subjectName);
            } else {
                // 🟡 Not logged in yet → go to LoginActivity
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("from_deeplink", true);
                intent.putExtra("class_name", className);
                intent.putExtra("subject_name", subjectName);
            }

            startActivity(intent);
        }

        finish();
    }
}
