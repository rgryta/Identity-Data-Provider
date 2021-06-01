package com.wut.identitycreator.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class DataDBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "identity_data.db";

    public SQLiteDatabase db;

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    public DataDBHelper(Context context) throws IOException {
        super(context, DB_NAME , null, 1);
        db = getWritableDatabase();

        id(context);

        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "UUID");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, uniqueID);
        db.update(DataDBSchema.Config.TABLE_NAME, values,DataDBSchema.Config.COLUMN_NAME_PARAM_NAME+"=\"UUID\"",null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //parameters are for currently selected: calibration option, user and pattern
        db.execSQL(DataDBSchema.Config.SQL_CREATE_CONFIG);
        db.execSQL(DataDBSchema.Calibration.SQL_CREATE_CALIB);
        db.execSQL(DataDBSchema.User.SQL_CREATE_USERS);
        db.execSQL(DataDBSchema.Pattern.SQL_CREATE_PATTERN);
        db.execSQL(DataDBSchema.DataEntry.SQL_CREATE_DATA_ENTRY);

        ContentValues values;

        //Default parameters
        //Calib
        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "CALIB");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "-1"); //Has to be overwritten by user
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        //User
        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "USER");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "DEFAULT");
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        //Pattern
        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "PATTERN");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "0-3-6-7-8-5-2");
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        //unique UUID for installation instance
        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "UUID");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "");
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        //Default user
        values = new ContentValues();
        values.put(DataDBSchema.User.COLUMN_NAME_USER, "DEFAULT");
        db.insert(DataDBSchema.User.TABLE_NAME, null, values);

        //Default pattern
        values = new ContentValues();
        values.put(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE, "0-3-6-7-8-5-2");
        db.insert(DataDBSchema.Pattern.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE, "0-1-2-5-8");
        db.insert(DataDBSchema.Pattern.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE, "0-1-4-8");
        db.insert(DataDBSchema.Pattern.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }
}
