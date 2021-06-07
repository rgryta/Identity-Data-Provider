package com.wut.identity_data_provider.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DataDBHandler implements Serializable {

    public final static String SETTING_CALIBRATION = "CALIBRATION";
    public final static String SETTING_UUID = "UUID";
    public final static String SETTING_USER = "USER";
    public final static String SETTING_PATTERN = "PATTERN";

    public final Map<String, String> mSettings; //CALIBRATION, USER, PATTERN, UUID

    private final DataDBHelper mDBHelper;

    public ArrayList<String> mPatterns;
    public ArrayList<String> mUsers;

    public DataDBHandler(Context ctx) {
        mDBHelper = new DataDBHelper(ctx);
        mSettings = new HashMap<>();
        setApplicationStatus();

        getUsers();
        getPatterns();
    }

    public void setApplicationStatus() {

        Cursor cursor = mDBHelper.db.query(
                DataDBSchema.Config.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            mSettings.put(cursor.getString(cursor.getColumnIndex(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME)),
                    cursor.getString(cursor.getColumnIndex(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE)));
        }
        cursor.close();
    }

    public void addAndSetCalibration(String newCalibration) {
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Calibration.COLUMN_NAME_OPTION, newCalibration);
        mDBHelper.db.insert(DataDBSchema.Calibration.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, SETTING_CALIBRATION);
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, newCalibration);
        mDBHelper.db.update(DataDBSchema.Config.TABLE_NAME, values, DataDBSchema.Config.COLUMN_NAME_PARAM_NAME + "=\"" + SETTING_CALIBRATION + "\"", null);

        mSettings.put(SETTING_CALIBRATION, newCalibration);
    }


    public void getUsers() {
        mUsers = new ArrayList<>();
        Cursor cursor = mDBHelper.db.query(
                DataDBSchema.User.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            mUsers.add(cursor.getString(cursor.getColumnIndex(DataDBSchema.User.COLUMN_NAME_USER)));
        }
        cursor.close();
    }

    public void addAndSetConfigUser(String user) {
        ContentValues values;

        int idx = mUsers.indexOf(user);
        if (idx != -1) {
            mSettings.put(SETTING_USER, mUsers.get(idx));
        } else {
            mUsers.add(user);
            mSettings.put(SETTING_USER, user);
            values = new ContentValues();
            values.put(DataDBSchema.User.COLUMN_NAME_USER, user);
            mDBHelper.db.insert(DataDBSchema.User.TABLE_NAME, null, values);
        }

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, SETTING_USER);
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, user);
        mDBHelper.db.update(DataDBSchema.Config.TABLE_NAME, values, DataDBSchema.Config.COLUMN_NAME_PARAM_NAME + "=\"" + SETTING_USER + "\"", null);
    }

    public void getPatterns() {
        mPatterns = new ArrayList<>();
        Cursor cursor = mDBHelper.db.query(
                DataDBSchema.Pattern.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        while (cursor.moveToNext()) {
            mPatterns.add(cursor.getString(cursor.getColumnIndex(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE)));
        }
        cursor.close();
    }

    public void setConfigPattern(int idx) {
        mSettings.put(SETTING_PATTERN, mPatterns.get(idx));

        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, SETTING_PATTERN);
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, mPatterns.get(idx));
        mDBHelper.db.update(DataDBSchema.Config.TABLE_NAME, values, DataDBSchema.Config.COLUMN_NAME_PARAM_NAME + "=\"" + SETTING_PATTERN + "\"", null);
    }

    public int completedTests() {
        Cursor cursor = mDBHelper.db.query(
                DataDBSchema.DataEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DataDBSchema.DataEntry.COLUMN_NAME_CALIBRATION + "=? AND " +
                        DataDBSchema.DataEntry.COLUMN_NAME_USER + "=? AND " +
                        DataDBSchema.DataEntry.COLUMN_NAME_PATTERN + "=?",              // The columns for the WHERE clause
                new String[]{mSettings.get(SETTING_CALIBRATION), mSettings.get(SETTING_USER), mSettings.get(SETTING_PATTERN)},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void addDataEntry(String dataEntry) {
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_CALIBRATION, mSettings.get(SETTING_CALIBRATION));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_USER, mSettings.get(SETTING_USER));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_PATTERN, mSettings.get(SETTING_PATTERN));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_DATA, dataEntry);
        mDBHelper.db.insert(DataDBSchema.DataEntry.TABLE_NAME, null, values);
    }

    public int checkProgressAndSetBestCalibration(boolean set) {
        Cursor cursor = mDBHelper.db.rawQuery(
                "select calibration from data_entry\n" +
                        "where calibration in (select calibration from (select user,calibration,pattern,count(*) from data_entry\n" +
                        "group by user,calibration,pattern\n" +
                        "having count(*)>=20)\n" +// over 20 entries, for tests set to 1 or so
                        "group by user,calibration\n" +
                        "having count(*)>=3)\n" +// over 3 different patterns (main ones)
                        "group by calibration\n" +
                        "having max(calibration);", null
        );
        int selectedCalibration = -1;
        while (cursor.moveToNext()) selectedCalibration = cursor.getInt(0);
        cursor.close();
        if ((selectedCalibration != -1) && (set))
            addAndSetCalibration(String.valueOf(selectedCalibration));
        return selectedCalibration;
    }

    public int completedTestsForCalibration(String calibrationOption) {
        Cursor cursor = mDBHelper.db.query(
                DataDBSchema.DataEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DataDBSchema.DataEntry.COLUMN_NAME_CALIBRATION + "=?",              // The columns for the WHERE clause
                new String[]{calibrationOption},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void addAndSetNewPattern(ArrayList<Integer> pattern) {
        String stringPattern = pattern.stream().map(Object::toString)
                .collect(Collectors.joining("-"));
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Pattern.COLUMN_NAME_SEQUENCE, stringPattern);
        mDBHelper.db.insert(DataDBSchema.Pattern.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, SETTING_PATTERN);
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, stringPattern);
        mDBHelper.db.update(DataDBSchema.Config.TABLE_NAME, values, DataDBSchema.Config.COLUMN_NAME_PARAM_NAME + "=\"" + SETTING_PATTERN + "\"", null);

        mDBHelper.close();
    }
}
