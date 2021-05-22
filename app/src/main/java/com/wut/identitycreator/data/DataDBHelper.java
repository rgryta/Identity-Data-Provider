package com.wut.identitycreator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.HashMap;

public class DataDBHelper extends SQLiteOpenHelper {


    public static final String DB_NAME = "identity_data.db";

    private static final String CONFIG_TABLE_NAME = "CONFIG";
    private static final String CONFIG_COLUMN_PARAM_NAME = "PARAM_NAME";
    private static final String CONFIG_COLUMN_PARAM_VALUE = "PARAM_VALUE" ;

    private static final String CALIB_TABLE_NAME = "CALIB";
    private static final String CALIB_COLUMN_ID = "ID";
    private static final String CALIB_COLUMN_OPTION = "OPTION";

    private static final String USERS_TABLE_NAME = "USERS";
    private static final String USERS_COLUMN_ID = "ID";

    private static final String PATTERN_TABLE_NAME = "PATTERN";
    private static final String PATTERN_COLUMN_ID = "ID";
    private static final String PATTERN_COLUMN_SEQUENCE = "SEQUENCE";

    private SQLiteDatabase db;

    public DataDBHelper(Context context) throws IOException {
        super(context, DB_NAME , null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //parameters are for currently selected: calibration option, user and pattern
        db.execSQL(
                "create table if not exists " + CONFIG_TABLE_NAME + " (" +
                        CONFIG_COLUMN_PARAM_NAME + " TEXT PRIMARY KEY NOT NULL" + ", "+
                        CONFIG_COLUMN_PARAM_VALUE + " INTEGER NOT NULL" +
                        ");"
        );
        db.execSQL(
                "create table if not exists " + CALIB_TABLE_NAME + " (" +
                        CALIB_COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL" + ", "+
                        CALIB_COLUMN_OPTION + " INTEGER NOT NULL" +
                        ");"
        );

        db.execSQL(
                "create table if not exists " + USERS_TABLE_NAME + " (" +
                        USERS_COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL" +
                        ");"
        );

        db.execSQL(
                "create table if not exists " + PATTERN_TABLE_NAME + " (" +
                        PATTERN_COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL" + ", "+
                        PATTERN_COLUMN_SEQUENCE + " TEXT NOT NULL" +
                        ");"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
