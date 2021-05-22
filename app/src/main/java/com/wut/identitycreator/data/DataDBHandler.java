package com.wut.identitycreator.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DataDBHandler implements Serializable {

    DataDBHelper handler;

    public Map<String,String>  settings;

    public DataDBHandler(Context ctx) throws IOException {
        handler = new DataDBHelper(ctx);
        settings = new HashMap<>();
        setApplicationStatus();
    }

    public void setApplicationStatus(){

        Cursor cursor = handler.db.query(
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

    public void getCalibrationOptions(){

    }

    public void getUsers(){

    }

    public void getPatterns(){

    }


}
