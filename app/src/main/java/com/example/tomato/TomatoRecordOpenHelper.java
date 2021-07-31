package com.example.tomato;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class TomatoRecordOpenHelper extends SQLiteOpenHelper {
    final static String TAG="tomatoRecordDB";
    public TomatoRecordOpenHelper(@Nullable Context context) {
        super(context, "tomatoRecord", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("drop table if exists tomatoRecord");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }

        String CREATE_SQL = "create table timeRecord ("
                +" _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +" COUNT INTEGER DEFAULT 0, "
                +" RDATE DATE DEFAULT (datetime('now','localtime')))";

        try {
            db.execSQL(CREATE_SQL);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in CREATE_SQL", ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists tomatoRecord; ");
        onCreate(db);
    }

    public void createTable() {
        SQLiteDatabase db = getWritableDatabase();

        String CREATE_SQL = "create table tomatoRecord ("
                +" _ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +" COUNT INTEGER DEFAULT 0, "
                +" RDATE DATE DEFAULT (datetime('now','localtime')))";

        try {
            db.execSQL(CREATE_SQL);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in CREATE_SQL", ex);
        }
    }

    public void dropTable() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL("drop table if exists tomatoRecord");
        } catch (Exception ex) {
            Log.e(TAG, "Exception in DROP_SQL", ex);
        }
    }
}