package com.example.studentmanagementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Check if user already logged in
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String role = prefs.getString("role", null);

        // If logged in, redirect immediately
        if (userId != -1 && role != null) {
            Intent intent;
            if (role.equals("teacher")) {
                intent = new Intent(this, TeacherActivity.class);
                intent.putExtra("userId", userId);
            } else {
                intent = new Intent(this, StudentActivity.class);

                // 🔄 If came from deep link, pass those extras
                boolean fromDeeplink = getIntent().getBooleanExtra("from_deeplink", false);
                String className = getIntent().getStringExtra("class_name");
                String subjectName = getIntent().getStringExtra("subject_name");

                if (fromDeeplink) {
                    intent.putExtra("mark_attendance", true);
                    intent.putExtra("class_name", className);
                    intent.putExtra("subject_name", subjectName);
                }
            }

            startActivity(intent);
            finish();
            return;
        }
        // 🔐 Login UI
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT id, role FROM users WHERE email = ? AND password = ?",
                    new String[]{email, password}
            );

            if (cursor.moveToFirst()) {
                int userIdFromDB = cursor.getInt(0);
                String roleFromDB = cursor.getString(1);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("user_id", userIdFromDB);
                editor.putString("role", roleFromDB);
                editor.apply();

                Intent nextIntent;
                if (roleFromDB.equals("teacher")) {
                    nextIntent = new Intent(this, TeacherActivity.class);
                    nextIntent.putExtra("userId", userIdFromDB);
                } else {
                    nextIntent = new Intent(this, StudentActivity.class);

                    // 🔄 Forward deep link data
                    boolean fromDeeplink = getIntent().getBooleanExtra("from_deeplink", false);
                    String className = getIntent().getStringExtra("class_name");
                    String subjectName = getIntent().getStringExtra("subject_name");

                    if (fromDeeplink) {
                        nextIntent.putExtra("mark_attendance", true);
                        nextIntent.putExtra("class_name", className);
                        nextIntent.putExtra("subject_name", subjectName);
                    }
                }

                startActivity(nextIntent);
                finish();

            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }

            cursor.close();
            db.close();
        });
    }
}
