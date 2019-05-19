package com.example.exspendables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DatabaseHandler extends SQLiteOpenHelper {

    //private static final String table_name = "pin_db";
    public static final int version = 1;
    //private static final String col1 = "pin_val";

    public DatabaseHandler(Context context) {
        super(context, "PIN", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE PIN (userpin INTEGER PRIMARY KEY)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PIN" );
        onCreate(db);
    }

    public boolean addData(String pin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userpin",pin);

        long result = db.insert("PIN",null,contentValues);

        if(result == -1){
            Log.d(TAG, "PIN added to DB success");
            return false;
        }else{
            Log.d(TAG, "PIN not added to DB failure");
            return true;
        }
    }

    public Cursor getPinData(){

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM PIN";
        Cursor data = db.rawQuery(query,null);
        return data;

    }

    public boolean modifyData(String newPin, String oldPin){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userpin",newPin);
        int result = db.update("PIN",contentValues,"userpin = " + oldPin,null);

        if(result > 0){
            return true;
        }
        else
        {
            return false;
        }
    }
}
