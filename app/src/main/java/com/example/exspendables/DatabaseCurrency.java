package com.example.exspendables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DatabaseCurrency extends SQLiteOpenHelper {
    public DatabaseCurrency(Context context) {
        super(context, "CURRENCY", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE CURRENCY (unit TEXT PRIMARY KEY)" ;
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS CURRENCY" );
        onCreate(db);
    }

    public boolean addData(String currencyCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("unit", currencyCode);

        long result = db.insert("CURRENCY",null,contentValues);

        if(result == -1){
            Log.d(TAG, "Currency added to DB successfully");
            return false;
        }else{
            Log.d(TAG, "Currency not added to DB");
            return true;
        }
    }

    public boolean modifyData(String new_unit, String old_unit){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("unit",new_unit);
        int result = db.update("CURRENCY",contentValues,"unit = " + old_unit,null);

        if(result > 0){
            return true;
        }
        else
        {
            return false;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CURRENCY",null);
        return cursor;
    }
}
