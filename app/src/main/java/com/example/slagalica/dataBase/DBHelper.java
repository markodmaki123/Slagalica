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


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Kreiranje tabele za korisnike
        String createTableQuery = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT, username TEXT, password TEXT)";
        String createTableQuestions = "CREATE TABLE questions (id INTEGER PRIMARY KEY AUTOINCREMENT, question TEXT, answer1 TEXT, answer2 TEXT, answer3 TEXT, answer4 TEXT, correct_answer TEXT)";
        String createTableSteps = "CREATE TABLE steps (id INTEGER PRIMARY KEY AUTOINCREMENT, step1 TEXT, step2 TEXT, step3 TEXT, step4 TEXT, step5 TEXT, step6 TEXT, step7 TEXT, correct_answer TEXT)";
        String createTableAssociations = "CREATE TABLE associations (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "column11 TEXT, column12 TEXT, column13 TEXT, column14 TEXT, columnAnswer1 TEXT," +
                "column21 TEXT, column22 TEXT, column23 TEXT, column24 TEXT, columnAnswer2 TEXT," +
                "column31 TEXT, column32 TEXT, column33 TEXT, column34 TEXT, columnAnswer3 TEXT," +
                "column41 TEXT, column42 TEXT, column43 TEXT, column44 TEXT, columnAnswer4 TEXT, correct_answer TEXT)";
        db.execSQL(createTableAssociations);
        db.execSQL(createTableQuery);
        db.execSQL(createTableSteps);
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

    public void insertSteps(String step1, String step2, String step3, String step4, String step5,String step6, String step7, String correctAnswer) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("step1", step1);
        values.put("step2", step2);
        values.put("step3", step3);
        values.put("step4", step4);
        values.put("step5", step5);
        values.put("step6", step6);
        values.put("step7", step7);
        values.put("correct_answer", correctAnswer);

        db.insert("steps", null, values);
    }

    public void insertAssociations(String column11, String column12, String column13, String column14, String columnAnswer1,
                                   String column21, String column22, String column23, String column24, String columnAnswer2,
                                   String column31, String column32, String column33, String column34, String columnAnswer3,
                                   String column41, String column42, String column43, String column44, String columnAnswer4, String correctAnswer) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("column11", column11);
        values.put("column12", column12);
        values.put("column13", column13);
        values.put("column14", column14);
        values.put("columnAnswer1", columnAnswer1);

        values.put("column21", column21);
        values.put("column22", column22);
        values.put("column23", column23);
        values.put("column24", column24);
        values.put("columnAnswer2", columnAnswer2);

        values.put("column31", column31);
        values.put("column32", column32);
        values.put("column33", column33);
        values.put("column34", column34);
        values.put("columnAnswer3", columnAnswer3);

        values.put("column41", column41);
        values.put("column42", column42);
        values.put("column43", column43);
        values.put("column44", column44);
        values.put("columnAnswer4", columnAnswer4);

        values.put("correct_answer", correctAnswer);

        db.insert("associations", null, values);
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

    @SuppressLint("Range")
    public String[] getStep(int stepId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] stepDetails = new String[8]; // Assuming you have 6 columns in your question table

        // Query the database to retrieve the question and its details based on the questionId
        String query = "SELECT * FROM steps WHERE id = " + stepId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Retrieve the question details from the cursor
            stepDetails[0] = cursor.getString(cursor.getColumnIndex("step1"));
            stepDetails[1] = cursor.getString(cursor.getColumnIndex("step2"));
            stepDetails[2] = cursor.getString(cursor.getColumnIndex("step3"));
            stepDetails[3] = cursor.getString(cursor.getColumnIndex("step4"));
            stepDetails[4] = cursor.getString(cursor.getColumnIndex("step5"));
            stepDetails[5] = cursor.getString(cursor.getColumnIndex("step6"));
            stepDetails[6] = cursor.getString(cursor.getColumnIndex("step7"));
            stepDetails[7] = cursor.getString(cursor.getColumnIndex("correct_answer"));
        }

        cursor.close();
        db.close();

        return stepDetails;
    }

    @SuppressLint("Range")
    public String[] getAssociation(int associoationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] stepDetails = new String[21]; // Assuming you have 6 columns in your question table

        // Query the database to retrieve the question and its details based on the questionId
        String query = "SELECT * FROM associations WHERE id = " + associoationId;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            // Retrieve the question details from the cursor
            stepDetails[0] = cursor.getString(cursor.getColumnIndex("column11"));
            stepDetails[1] = cursor.getString(cursor.getColumnIndex("column12"));
            stepDetails[2] = cursor.getString(cursor.getColumnIndex("column13"));
            stepDetails[3] = cursor.getString(cursor.getColumnIndex("column14"));
            stepDetails[4] = cursor.getString(cursor.getColumnIndex("columnAnswer1"));
            stepDetails[5] = cursor.getString(cursor.getColumnIndex("column21"));
            stepDetails[6] = cursor.getString(cursor.getColumnIndex("column22"));
            stepDetails[7] = cursor.getString(cursor.getColumnIndex("column23"));
            stepDetails[8] = cursor.getString(cursor.getColumnIndex("column24"));
            stepDetails[9] = cursor.getString(cursor.getColumnIndex("columnAnswer2"));
            stepDetails[10] = cursor.getString(cursor.getColumnIndex("column31"));
            stepDetails[11] = cursor.getString(cursor.getColumnIndex("column32"));
            stepDetails[12] = cursor.getString(cursor.getColumnIndex("column33"));
            stepDetails[13] = cursor.getString(cursor.getColumnIndex("column34"));
            stepDetails[14] = cursor.getString(cursor.getColumnIndex("columnAnswer3"));
            stepDetails[15] = cursor.getString(cursor.getColumnIndex("column41"));
            stepDetails[16] = cursor.getString(cursor.getColumnIndex("column42"));
            stepDetails[17] = cursor.getString(cursor.getColumnIndex("column43"));
            stepDetails[18] = cursor.getString(cursor.getColumnIndex("column44"));
            stepDetails[19] = cursor.getString(cursor.getColumnIndex("columnAnswer4"));
            stepDetails[20] = cursor.getString(cursor.getColumnIndex("correct_answer"));
        }

        cursor.close();
        db.close();

        return stepDetails;
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