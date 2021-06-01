package com.wut.identitycreator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataDBHandler implements Serializable {

    DataDBHelper dbHelper;

    //CALIB, USER, PATTERN, UUID
    public Map<String,String>  settings;

    public ArrayList<String> patterns;
    public ArrayList<String> users;

    public DataDBHandler(Context ctx) throws IOException {
        dbHelper = new DataDBHelper(ctx);
        settings = new HashMap<>();
        setApplicationStatus();

        getUsers();
        getPatterns();
    }

    public void setApplicationStatus(){

        Cursor cursor = dbHelper.db.query(
                DataDBSchema.Config.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            settings.put(cursor.getString(cursor.getColumnIndex(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME)),
                        cursor.getString(cursor.getColumnIndex(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE)));
        }
        cursor.close();
    }

    public void addAndSetCalib(String newCalib){
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Calibration.COLUMN_NAME_OPTION, newCalib);
        dbHelper.db.insert(DataDBSchema.Calibration.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "CALIB");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, newCalib);
        dbHelper.db.update(DataDBSchema.Config.TABLE_NAME, values,DataDBSchema.Config.COLUMN_NAME_PARAM_NAME+"=\"CALIB\"",null);

        settings.put("CALIB",newCalib);
    }


    public void getUsers(){
        users = new ArrayList<>();
        Cursor cursor = dbHelper.db.query(
                DataDBSchema.User.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            users.add(cursor.getString(cursor.getColumnIndex(DataDBSchema.User.COLUMN_NAME_USER)));
        }
        cursor.close();
    }

    public void getPatterns(){
        patterns = new ArrayList<>();
        Cursor cursor = dbHelper.db.query(
                DataDBSchema.Pattern.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            patterns.add(cursor.getString(cursor.getColumnIndex(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE)));
        }
        cursor.close();
    }

    public void setConfigPattern(int idx){
        settings.put("PATTERN",patterns.get(idx));

        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "PATTERN");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, patterns.get(idx));
        dbHelper.db.update(DataDBSchema.Config.TABLE_NAME, values,DataDBSchema.Config.COLUMN_NAME_PARAM_NAME+"=\"PATTERN\"",null);
    }


}
