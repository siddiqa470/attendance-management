package com.example.studentmanagementsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StudentActivity extends AppCompatActivity {

    TextView studentName, studentRoll, studentDept;
    GridView calendarGrid;
    ImageView logoutButton;

    ArrayList<String> dateList = new ArrayList<>();
    ArrayList<String> presentDates = new ArrayList<>();
    CalendarAdapter adapter;

    DatabaseHelper dbHelper;
    int userId;
    int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        studentName = findViewById(R.id.studentName);
        studentRoll = findViewById(R.id.studentRoll);
        studentDept = findViewById(R.id.studentDept);
        calendarGrid = findViewById(R.id.calendarGrid);
        logoutButton = findViewById(R.id.logoutButton);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent i = new Intent(this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        if (userId != -1) {
            loadStudentDetails(userId);
            generateDateList();
            loadAttendanceDates();

            adapter = new CalendarAdapter(this, dateList, presentDates);
            calendarGrid.setAdapter(adapter);

            // ✅ Auto mark attendance if deep link hit
            Intent intent = getIntent();
            boolean shouldMark = intent.getBooleanExtra("mark_attendance", false);

            if (shouldMark) {
                String className = intent.getStringExtra("class_name");
                String subjectName = intent.getStringExtra("subject_name");

                markAttendance(studentId, className, subjectName);
                Toast.makeText(this, "✅ Attendance marked for " + subjectName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadStudentDetails(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id, name, roll_no, department FROM students WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            studentId = cursor.getInt(0);
            studentName.setText(cursor.getString(1));
            studentRoll.setText("Roll No: " + cursor.getString(2));
            studentDept.setText("Department: " + cursor.getString(3));
        }

        cursor.close();
        db.close();
    }

    private void generateDateList() {
        dateList.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JULY, 7); // Start from July 7
        Calendar today = Calendar.getInstance();

        while (!calendar.after(today)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateList.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }
    }

    private void loadAttendanceDates() {
        presentDates.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT timestamp FROM attendance WHERE student_id = ?",
                new String[]{String.valueOf(studentId)});

        while (cursor.moveToNext()) {
            String fullTimestamp = cursor.getString(0);
            String datePart = fullTimestamp.split(" ")[0];
            presentDates.add(datePart);
        }

        cursor.close();
        db.close();
    }

    private void markAttendance(int studentId, String className, String subjectName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());

        db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (?, ?, ?, ?)",
                new Object[]{studentId, className, subjectName, timestamp});
        db.close();

        // 🔄 Refresh attendance after marking
        loadAttendanceDates();
        adapter.notifyDataSetChanged();
    }
}
