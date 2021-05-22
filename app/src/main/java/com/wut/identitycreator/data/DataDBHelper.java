package com.wut.identitycreator.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;

public class DataDBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "identity_data.db";

    private SQLiteDatabase db;

    public DataDBHelper(Context context) throws IOException {
        super(context, DB_NAME , null, 1);
        db = getWritableDatabase();
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
        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "CALIB");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "-1"); //Has to be overwritten by user
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "USER");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "DEFAULT");
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, "PATTERN");
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, "1-4-7-8-9-6-3");
        db.insert(DataDBSchema.Config.TABLE_NAME, null, values);

        //Default user
        values = new ContentValues();
        values.put(DataDBSchema.User.COLUMN_NAME_USER, "DEFAULT");
        db.insert(DataDBSchema.User.TABLE_NAME, null, values);

        //Default pattern
        values = new ContentValues();
        values.put(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE, "1-4-7-8-9-6-3");
        db.insert(DataDBSchema.Pattern.TABLE_NAME, null, values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
