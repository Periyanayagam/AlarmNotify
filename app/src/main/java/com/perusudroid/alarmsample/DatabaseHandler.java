package com.perusudroid.alarmsample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alarmDB";
    private static final String TABLE_ALARMS = "alarmtable";
    private static final String TABLE_COMPLETED_ALARMS = "completedalarmtable";
    private static final String ALARM_ID = "id";
    private static final String OLD_ALARM_ID = "oldid";
    private static final String ALARM_NAME = "name";
    private static final String ALARM_HOUR = "hour";
    private static final String ALARM_MIN = "minute";
    private static final String ALARM_ENABLED = "enabled";
    private static final String ALARM_COMPLETED = "alarmcompleted";
    private static final String ALARM_DAY = "day";
    private static final String ALARM_MONTH = "month";
    private static final String ALARM_YEAR = "year";
    private static final String ALARM_DAY_OF_WEEK = "dayofweek";
    private static final String ALARM_REPEAT = "repeat";
    private static final String ALARM_TYPE = "type";
    private static final String ALARM_TONE = "tone";
    private static final String ALARM_TONE_URL = "toneurl";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "("
                + ALARM_ID + " INTEGER PRIMARY KEY ," + ALARM_NAME + " TEXT,"
                + ALARM_HOUR + " INTEGER," + ALARM_MIN + " INTEGER," + ALARM_ENABLED + " INTEGER," +
                ALARM_REPEAT + " TEXT," + ALARM_TYPE + " TEXT," + ALARM_TONE + " TEXT," +
                ALARM_TONE_URL + " TEXT," + ALARM_COMPLETED + " INTEGER," + ALARM_DAY + " INTEGER," +
                ALARM_MONTH + " INTEGER," + ALARM_YEAR + " INTEGER," + ALARM_DAY_OF_WEEK + " INTEGER" + ")";
        db.execSQL(CREATE_ALARMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        // Create tables again
        onCreate(db);
    }

    public int getId() {
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int id = 1;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);

            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return id;
    }


    public ArrayList<AlarmModel> getAllAlarmModels() {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE " + ALARM_TYPE + " = 'a'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));
                Log.e("id=", "=" + cursor.getInt(0));
                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }


    // Adding new alarmobj
    public void addAlarmModel(AlarmModel alarmobj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmobj.getName()); // AlarmModel Name
        values.put(ALARM_HOUR, alarmobj.getHour()); // AlarmModel Phone
        values.put(ALARM_MIN, alarmobj.getMinute());
        values.put(ALARM_ENABLED, alarmobj.isEnabled());
        values.put(ALARM_REPEAT, alarmobj.getRepeat());
        values.put(ALARM_TYPE, alarmobj.getType());
        values.put(ALARM_TONE, alarmobj.getTone());
        values.put(ALARM_TONE_URL, alarmobj.getToneurl());
        values.put(ALARM_COMPLETED, alarmobj.getIsCompleted());
        values.put(ALARM_DAY, alarmobj.getDay());
        values.put(ALARM_MONTH, alarmobj.getMonth());
        values.put(ALARM_YEAR, alarmobj.getYear());
        values.put(ALARM_DAY_OF_WEEK, alarmobj.getDayofweek());
        // Inserting Row
        db.insert(TABLE_ALARMS, null, values);
        db.close(); // Closing database connection
    }
}
