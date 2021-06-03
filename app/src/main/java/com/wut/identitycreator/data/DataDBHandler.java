package com.wut.identitycreator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

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

    public DataDBHandler(Context ctx) {
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
    public void addAndSetConfigUser(String user){
        ContentValues values;

        int idx = users.indexOf(user);
        if (idx!=-1){
            settings.put("USER",users.get(idx));
        }
        else{
            users.add(user);
            settings.put("USER",user);
            values = new ContentValues();
            values.put(DataDBSchema.User.COLUMN_NAME_USER, user);
            dbHelper.db.insert(DataDBSchema.User.TABLE_NAME, null,values);
        }

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "USER");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, user);
        dbHelper.db.update(DataDBSchema.Config.TABLE_NAME, values,DataDBSchema.Config.COLUMN_NAME_PARAM_NAME+"=\"USER\"",null);
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

    public int completedTests(){
        Cursor cursor = dbHelper.db.query(
                DataDBSchema.DataEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DataDBSchema.DataEntry.COLUMN_NAME_CALIB+"=? AND "+
                        DataDBSchema.DataEntry.COLUMN_NAME_USER+"=? AND "+
                        DataDBSchema.DataEntry.COLUMN_NAME_PATTERN+"=?",              // The columns for the WHERE clause
                new String[] {settings.get("CALIB"), settings.get("USER"), settings.get("PATTERN") },          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        int count=cursor.getCount();
        cursor.close();
        return count;
    }

    public void addDataEntry(String dataEntry){
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_CALIB, settings.get("CALIB"));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_USER, settings.get("USER"));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_PATTERN, settings.get("PATTERN"));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_DATA, dataEntry);
        dbHelper.db.insert(DataDBSchema.DataEntry.TABLE_NAME, null,values);
    }

    public int checkProgressAndSetBestCalib(boolean set){
        Cursor cursor = dbHelper.db.rawQuery(
                "select calib from data_entry\n" +
                        "where calib in (select calib from (select user,calib,pattern,count(*) from data_entry\n" +
                        "group by user,calib,pattern\n" +
                        "having count(*)>=20)\n" +//ponad 20 entries, dla testów dać mniej
                        "group by user,calib\n" +
                        "having count(*)>=3)\n" +//na 3 różnych patternach
                        "group by calib\n" +
                        "having max(calib);",null
        );
        int selCalib=-1;
        while (cursor.moveToNext()) selCalib = cursor.getInt(0);
        cursor.close();
        if ((selCalib!=-1)&&(set)) addAndSetCalib(String.valueOf(selCalib));
        return selCalib;
    }

}
