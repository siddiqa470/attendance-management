package com.example.studentmanagementsystem;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TeacherActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    int userId;
    int teacherId;
    TextView teacherNameTextView, generatedLinkText;
    Spinner classSpinner, subjectSpinner;
    Button generateLinkButton, copyLinkButton;

    ArrayList<String> classList = new ArrayList<>();
    ArrayList<String> subjectList = new ArrayList<>();

    String selectedClass = "", selectedSubject = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        teacherNameTextView = findViewById(R.id.teacherName);
        generatedLinkText = findViewById(R.id.generatedLinkText);
        classSpinner = findViewById(R.id.classSpinner);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        generateLinkButton = findViewById(R.id.generateLinkButton);
        copyLinkButton = findViewById(R.id.copyLinkButton);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);

        if (userId != -1) {
            loadTeacherData(userId);
        }

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass = classList.get(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubject = subjectList.get(position);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        generateLinkButton.setOnClickListener(v -> {
            if (selectedClass.isEmpty() || selectedSubject.isEmpty()) {
                Toast.makeText(this, "Please select class and subject", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String encodedClass = URLEncoder.encode(selectedClass, "UTF-8");
                    String encodedSubject = URLEncoder.encode(selectedSubject, "UTF-8");

                    String link = "attendapp://teacher?class=" + encodedClass + "&subject=" + encodedSubject;
                    generatedLinkText.setText("📋 Generated Link: " + link);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Encoding error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        copyLinkButton.setOnClickListener(v -> {
            String link = generatedLinkText.getText().toString().replace("📋 Generated Link: ", "");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Attendance Link", link);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTeacherData(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get teacher info
        Cursor teacherCursor = db.rawQuery("SELECT id, name FROM teachers WHERE user_id = ?", new String[]{String.valueOf(userId)});
        if (teacherCursor.moveToFirst()) {
            teacherId = teacherCursor.getInt(0);
            String name = teacherCursor.getString(1);
            teacherNameTextView.setText(name);
        }
        teacherCursor.close();

        // Load subjects
        subjectList.clear();
        Cursor subjectCursor = db.rawQuery("SELECT subject_name FROM subjects WHERE teacher_id = ?", new String[]{String.valueOf(teacherId)});
        while (subjectCursor.moveToNext()) {
            subjectList.add(subjectCursor.getString(0));
        }
        subjectCursor.close();
        subjectSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, subjectList));

        // Load classes
        classList.clear();
        Cursor classCursor = db.rawQuery("SELECT class_name FROM classes WHERE teacher_id = ?", new String[]{String.valueOf(teacherId)});
        while (classCursor.moveToNext()) {
            classList.add(classCursor.getString(0));
        }
        classCursor.close();
        classSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classList));

        db.close();
    }
}
