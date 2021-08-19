package com.wut.identity_data_provider.data;

import android.provider.BaseColumns;


/**
 * Helper class used for storing DDLs for DB schema creation. Each inner class representing different table.
 */
public class DataDBSchema {

    private DataDBSchema() {
    }

    /**
     * Class containing data about CONFIG table.
     */
    public static class Config implements BaseColumns {
        /**
         * The constant TABLE_NAME.
         */
        public static final String TABLE_NAME = "CONFIG";

        /**
         * The constant COLUMN_NAME_PARAM_NAME.
         */
        public static final String COLUMN_NAME_PARAM_NAME = "PARAM_NAME";
        /**
         * The constant COLUMN_NAME_PARAM_VALUE.
         */
        public static final String COLUMN_NAME_PARAM_VALUE = "PARAM_VALUE";

        /**
         * The constant SQL_CREATE_CONFIG.
         */
        public static final String SQL_CREATE_CONFIG = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_PARAM_NAME + " TEXT PRIMARY KEY NOT NULL" + ", " +
                COLUMN_NAME_PARAM_VALUE + " TEXT NOT NULL" +
                ");";
    }

    /**
     * Class containing data about CALIBRATION table.
     */
    public static class Calibration implements BaseColumns {
        /**
         * The constant TABLE_NAME.
         */
        public static final String TABLE_NAME = "CALIBRATION";

        /**
         * The constant COLUMN_NAME_OPTION.
         */
        public static final String COLUMN_NAME_OPTION = "OPTION";

        /**
         * The constant SQL_CREATE_CALIBRATION.
         */
        public static final String SQL_CREATE_CALIBRATION = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_OPTION + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    /**
     * Class containing data about USER table.
     */
    public static class User implements BaseColumns {
        /**
         * The constant TABLE_NAME.
         */
        public static final String TABLE_NAME = "USER";

        /**
         * The constant COLUMN_NAME_USER.
         */
        public static final String COLUMN_NAME_USER = "USER";

        /**
         * The constant SQL_CREATE_USERS.
         */
        public static final String SQL_CREATE_USERS = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_USER + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    /**
     * Class containing data about PATTERN table.
     */
    public static class Pattern implements BaseColumns {
        /**
         * The constant TABLE_NAME.
         */
        public static final String TABLE_NAME = "PATTERN";

        /**
         * The constant COLUMN_NAME_SEQUENCE.
         */
        public static final String COLUMN_NAME_SEQUENCE = "SEQUENCE";

        /**
         * The constant SQL_CREATE_PATTERN.
         */
        public static final String SQL_CREATE_PATTERN = "create table if not exists " + TABLE_NAME + " (" +
                COLUMN_NAME_SEQUENCE + " TEXT PRIMARY KEY NOT NULL" +
                ");";
    }

    /**
     * Class containing data about DATA_ENTRY table.
     */
    public static class DataEntry implements BaseColumns {
        /**
         * The constant TABLE_NAME.
         */
        public static final String TABLE_NAME = "DATA_ENTRY";

        /**
         * The constant COLUMN_NAME_ID.
         */
        public static final String COLUMN_NAME_ID = "_ID";
        /**
         * The constant COLUMN_NAME_CALIBRATION.
         */
        public static final String COLUMN_NAME_CALIBRATION = "CALIBRATION";
        /**
         * The constant COLUMN_NAME_USER.
         */
        public static final String COLUMN_NAME_USER = "USER";
        /**
         * The constant COLUMN_NAME_PATTERN.
         */
        public static final String COLUMN_NAME_PATTERN = "PATTERN";
        /**
         * The constant COLUMN_NAME_DATA.
         */
        public static final String COLUMN_NAME_DATA = "DATA";
        /**
         * The constant COLUMN_NAME_STATUS.
         */
        public static final String COLUMN_NAME_STATUS = "STATUS"; // 0 = NEW, 1 = SYNCED

        /**
         * The constant SQL_CREATE_DATA_ENTRY.
         */
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
