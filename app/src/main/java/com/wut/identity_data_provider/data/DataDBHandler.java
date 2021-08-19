package com.wut.identity_data_provider.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class used to handle connection with SQLite and store app settings and configuration.
 */
public class DataDBHandler implements Serializable {

    /**
     * The constant SETTING_CALIBRATION.
     */
    public final static String SETTING_CALIBRATION = "CALIBRATION";
    /**
     * The constant SETTING_UUID.
     */
    public final static String SETTING_UUID = "UUID";
    /**
     * The constant SETTING_USER.
     */
    public final static String SETTING_USER = "USER";
    /**
     * The constant SETTING_PATTERN.
     */
    public final static String SETTING_PATTERN = "PATTERN";
    private static DataDBHelper mDBHelper;

    /**
     * The M settings.
     */
    public final Map<String,String> mSettings; //CALIBRATION, USER, PATTERN, UUID


    /**
     * The M patterns.
     */
    public ArrayList<String> mPatterns;
    /**
     * The M users.
     */
    public ArrayList<String> mUsers;

    /**
     * Constructor for DB Handler, setting up DB handler, settings and configuration.
     *
     * @param ctx Context of the application.
     */
    public DataDBHandler(Context ctx) {
        mDBHelper = new DataDBHelper(ctx);
        mSettings = new HashMap<>();
        setApplicationStatus();

        getUsers();
        getPatterns();
    }

    /**
     * Set the last saved status of the application, stored in the SQLite DB.
     */
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

    /**
     * Set the calibration setting and add it to the DB.
     *
     * @param newCalibration String storing the calibration height.
     */
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


    /**
     * Set the list of all users from DB.
     */
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

    /**
     * Add new user to DB and set it as the current one.
     *
     * @param user the user
     */
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


    /**
     * Set the list of all available patterns from DB.
     */
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

    /**
     * Set the currently selected pattern and save it as the configuration setting.
     *
     * @param idx Index of the selected pattern within the configuration list.
     */
    public void setConfigPattern(int idx) {
        mSettings.put(SETTING_PATTERN, mPatterns.get(idx));

        ContentValues values = new ContentValues();
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_NAME, SETTING_PATTERN);
        values.put(DataDBSchema.Config.COLUMN_NAME_PARAM_VALUE, mPatterns.get(idx));
        mDBHelper.db.update(DataDBSchema.Config.TABLE_NAME, values, DataDBSchema.Config.COLUMN_NAME_PARAM_NAME + "=\"" + SETTING_PATTERN + "\"", null);
    }

    /**
     * Return the number of completed tests.
     *
     * @return Returns integer containing number of completed tests for the currently selected setting.
     */
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


    /**
     * Method used for saving data entry to DB.
     *
     * @param dataEntry String containing compressed JSON with entry data.
     */
    public void addDataEntry(String dataEntry) {
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_CALIBRATION, mSettings.get(SETTING_CALIBRATION));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_USER, mSettings.get(SETTING_USER));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_PATTERN, mSettings.get(SETTING_PATTERN));
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_DATA, dataEntry);
        mDBHelper.db.insert(DataDBSchema.DataEntry.TABLE_NAME, null, values);
    }

    /**
     * Select the calibration method which has completed the basic tests and has the most of them.
     *
     * @param set Only set the calibration if the flag is set to true. Otherwise ignore it.
     * @return Returns the calibration setting with the most data entries.
     */
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

    /**
     * Returns the number of given entries of data for given calibration.
     *
     * @param calibrationOption Given calibration option.
     * @return Returns number of given entries for the option.
     */
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

    /**
     * Adds new pattern to the DB for later availability and then sets it up as the currently selected option.
     *
     * @param pattern Array of integers describing pattern order.
     */
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

    /**
     * Returns entries ready to be sent to Cloud DB.
     *
     * @return Return a hashmap of IDs and their respective compressed data entries - only the ones          that have not been sent yet and were already completed.
     */
    public static HashMap<Integer,String> getReadyDataEntries() {
        HashMap<Integer,String> entries = new HashMap<>();
        Cursor cursor = mDBHelper.db.rawQuery(
                new StringBuilder().append("select _id,data from data_entry\n")
                        .append("where (user,calibration,pattern) in (select user,calibration,pattern from data_entry\n")
                        .append("group by user,calibration,pattern\n")
                        .append("having count(*)>=20)")
                        .append("and status=0").toString(), null
        );
        while (cursor.moveToNext()) {
            entries.put(cursor.getInt(cursor.getColumnIndex(DataDBSchema.DataEntry.COLUMN_NAME_ID)),
                    cursor.getString(cursor.getColumnIndex(DataDBSchema.DataEntry.COLUMN_NAME_DATA)));
        }
        cursor.close();
        return entries;
    }

    /**
     * Updates the status of a single data entry.
     *
     * @param ID ID of data entry to update as successfully uploaded.
     */
    public static void updateUploadedStatuses(String ID) {
        ContentValues values = new ContentValues();
        values.put(DataDBSchema.DataEntry.COLUMN_NAME_STATUS, "1");
        mDBHelper.db.update(DataDBSchema.DataEntry.TABLE_NAME, values, DataDBSchema.DataEntry.COLUMN_NAME_ID + "=\"" + ID + "\"", null);
    }
}
