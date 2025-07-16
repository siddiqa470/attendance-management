package com.example.studentmanagementsystem;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "AttendanceDB.db";
    public static final int DB_VERSION = 4; // Increased to force recreation

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, email TEXT, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE teachers (id INTEGER PRIMARY KEY, user_id INTEGER, name TEXT)");
        db.execSQL("CREATE TABLE subjects (id INTEGER PRIMARY KEY, teacher_id INTEGER, subject_name TEXT)");
        db.execSQL("CREATE TABLE classes (id INTEGER PRIMARY KEY, teacher_id INTEGER, class_name TEXT)");
        db.execSQL("CREATE TABLE students (id INTEGER PRIMARY KEY, user_id INTEGER, name TEXT, roll_no TEXT, department TEXT)");
        db.execSQL("CREATE TABLE attendance (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER, class_name TEXT, subject_name TEXT, timestamp DATETIME)");

        // Insert users
        db.execSQL("INSERT INTO users VALUES " +
                "(1, 'siddiqa@abc.com', '1234', 'teacher')," +
                "(2, 'uzma@gmail.com', '7890', 'student')," +
                "(3, 'aisha@abc.com', '5678', 'teacher')," +
                "(4, 'rahul@gmail.com', '1111', 'student')," +
                "(5, 'aamir@gmail.com', '2222', 'student')," +
                "(7, 'zeeshan@gmail.com', '4444', 'student')");

        // Teachers
        db.execSQL("INSERT INTO teachers VALUES (1, 1, 'Ms. Siddiqa'), (2, 3, 'Ms. Aisha')");

        // Subjects
        db.execSQL("INSERT INTO subjects VALUES (1, 1, 'Java'), (2, 1, 'Python'), (3, 2, 'C++'), (4, 2, 'DBMS')");

        // Classes
        db.execSQL("INSERT INTO classes VALUES (1, 1, 'FYIT-A'), (2, 1, 'FYIT-B'), (3, 2, 'SYIT-A'), (4, 2, 'TYIT-B')");

        // Students
        db.execSQL("INSERT INTO students VALUES " +
                "(1, 2, 'Uzma Shaikh', 'FYIT2301', 'IT')," +
                "(2, 4, 'Rahul Patil', 'FYIT2302', 'IT')," +
                "(3, 5, 'Aamir Khan', 'FYIT2303', 'IT')," +
                "(4, 6, 'Fariha Qureshi', 'FYIT2304', 'IT')," +
                "(5, 7, 'Zeeshan Ali', 'FYIT2305', 'IT')");

        // Sample attendance for all 5 students (randomized)
        for (int i = 7; i <= 15; i++) {
            db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (1, 'FYIT-A', 'Java', '2025-07-" + i + " 09:00:00')");
            db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (2, 'FYIT-A', 'Java', '2025-07-" + i + " 09:15:00')");
            db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (3, 'FYIT-A', 'Java', '2025-07-" + i + " 09:30:00')");
            db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (4, 'FYIT-A', 'Java', '2025-07-" + i + " 09:45:00')");
            db.execSQL("INSERT INTO attendance (student_id, class_name, subject_name, timestamp) VALUES (5, 'FYIT-A', 'Java', '2025-07-" + i + " 10:00:00')");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS teachers");
        db.execSQL("DROP TABLE IF EXISTS subjects");
        db.execSQL("DROP TABLE IF EXISTS classes");
        db.execSQL("DROP TABLE IF EXISTS students");
        db.execSQL("DROP TABLE IF EXISTS attendance");
        onCreate(db);
    }
}
