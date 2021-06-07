package com.wut.identity_data_provider.data;

import android.provider.BaseColumns;

public class DataDBSchema {

    private DataDBSchema() {
    }

    public static class Config implements BaseColumns {
        public static final String TABLE_NAME = "CONFIG";

        public static final String COLUMN_NAME_PARAM_NAME = "PARAM_NAME";
        public static final String COLUMN_NAME_PARAM_VALUE = "PARAM_VALUE";

        public static final String SQL_CREATE_CONFIG = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_PARAM_NAME + " TEXT PRIMARY KEY NOT NULL" + ", " +
                COLUMN_NAME_PARAM_VALUE + " TEXT NOT NULL" +
                ");";
    }

    public static class Calibration implements BaseColumns {
        public static final String TABLE_NAME = "CALIBRATION";

        public static final String COLUMN_NAME_OPTION = "OPTION";

        public static final String SQL_CREATE_CALIBRATION = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_OPTION + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    public static class User implements BaseColumns {
        public static final String TABLE_NAME = "USER";

        public static final String COLUMN_NAME_USER = "USER";

        public static final String SQL_CREATE_USERS = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_USER + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    public static class Pattern implements BaseColumns {
        public static final String TABLE_NAME = "PATTERN";

        public static final String COLUMN_NAME_SEQUENCE = "SEQUENCE";

        public static final String SQL_CREATE_PATTERN = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_SEQUENCE + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    public static class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "DATA_ENTRY";

        public static final String COLUMN_NAME_ID = "_ID";
        public static final String COLUMN_NAME_CALIBRATION = "CALIBRATION";
        public static final String COLUMN_NAME_USER = "USER";
        public static final String COLUMN_NAME_PATTERN = "PATTERN";
        public static final String COLUMN_NAME_DATA = "DATA";
        public static final String COLUMN_NAME_STATUS = "STATUS"; // 0 = NEW, 1 = SYNCED

        public static final String SQL_CREATE_DATA_ENTRY = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" + ", " +
                COLUMN_NAME_CALIBRATION + " TEXT NOT NULL" + ", " +
                COLUMN_NAME_USER + " TEXT NOT NULL" + ", " +
                COLUMN_NAME_PATTERN + " TEXT NOT NULL" + ", " +
                COLUMN_NAME_DATA + " TEXT NOT NULL" + ", " +
                COLUMN_NAME_STATUS + " INTEGER NOT NULL DEFAULT 0" + ", " +
                "FOREIGN KEY(" + COLUMN_NAME_CALIBRATION + ") REFERENCES " + Calibration.TABLE_NAME + "(" + Calibration.COLUMN_NAME_OPTION + ")" + ", " +
                "FOREIGN KEY(" + COLUMN_NAME_USER + ") REFERENCES " + User.TABLE_NAME + "(" + User.COLUMN_NAME_USER + ")" + ", " +
                "FOREIGN KEY(" + COLUMN_NAME_PATTERN + ") REFERENCES " + Pattern.TABLE_NAME + "(" + Pattern.COLUMN_NAME_SEQUENCE + ")" +
                ");";
    }


}
