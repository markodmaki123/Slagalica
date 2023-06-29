package com.example.slagalica.dataBase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.slagalica.R;

import java.io.ByteArrayOutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 1;


    // Definišite tabelu i kolone
    private static final String TABLE_QUESTIONS = "questions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_ANSWER1 = "answer1";
    private static final String COLUMN_ANSWER2 = "answer2";
    private static final String COLUMN_ANSWER3 = "answer3";
    private static final String COLUMN_ANSWER4 = "answer4";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // SQL upit za kreiranje tabele
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_QUESTIONS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_QUESTION + " TEXT,"
            + COLUMN_ANSWER1 + " TEXT,"
            + COLUMN_ANSWER2 + " TEXT,"
            + COLUMN_ANSWER3 + " TEXT,"
            + COLUMN_ANSWER4 + " TEXT,"
            + COLUMN_CORRECT_ANSWER + " INTEGER"
            + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS questions");
        // Kreiranje tabele za korisnike
        String createTableQuery = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, username TEXT, password TEXT)";
        String createTableQuestions = "CREATE TABLE questions (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer1 TEXT, answer2 TEXT, answer3 TEXT, answer4 TEXT, correct_answer TEXT)";
        db.execSQL(createTableQuery);
        //db.execSQL(CREATE_TABLE_QUESTIONS);
        db.execSQL(createTableQuestions);

    }
    public void insertUser(String email,String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("username", username);
        values.put("password", password);
        db.insert("users", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementirajte ovu metodu ako želite da ažurirate bazu podataka
    }

    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"username"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);
        boolean loginSuccessful = (cursor.getCount() > 0);
        cursor.close();
        return loginSuccessful;
    }

    public void insertQuestion(String question, String answer1, String answer2, String answer3, String answer4, int correctAnswer) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER1, answer1);
        values.put(COLUMN_ANSWER2, answer2);
        values.put(COLUMN_ANSWER3, answer3);
        values.put(COLUMN_ANSWER4, answer4);
        values.put(COLUMN_CORRECT_ANSWER, correctAnswer);

        db.insert(TABLE_QUESTIONS, null, values);
    }

    @SuppressLint("Range")
    public String[] getQuestion(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] questionDetails = new String[6]; // Assuming you have 6 columns in your question table

        // Query the database to retrieve the question and its details based on the questionId
        String query = "SELECT * FROM questions WHERE id = " + questionId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Retrieve the question details from the cursor
            questionDetails[0] = cursor.getString(cursor.getColumnIndex("question"));
            questionDetails[1] = cursor.getString(cursor.getColumnIndex("answer1"));
            questionDetails[2] = cursor.getString(cursor.getColumnIndex("answer2"));
            questionDetails[3] = cursor.getString(cursor.getColumnIndex("answer3"));
            questionDetails[4] = cursor.getString(cursor.getColumnIndex("answer4"));
            questionDetails[5] = cursor.getString(cursor.getColumnIndex("correct_answer"));
        }

        cursor.close();
        db.close();

        return questionDetails;
    }

    public Cursor getProfileCursor(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                "email",
                "username"
        };

        String selection = "username = ?";
        String[] selectionArgs = { username };

        return db.query(
                "users",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }


}