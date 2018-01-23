package com.twechy.twechy_v2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Weather_Helper extends SQLiteOpenHelper {


    private static final String[] TABLE_LOCATION_KEYS =
            {"project_id", "locationName", "pressure", "humidity", "sunrise", "sunset", "conditionTitle", "currentTemp"
                    , "conditionIcon"};

    // All Static variables
    // Database Version

    private static final int DATABASE_VERSION = 1;

    // Database Name

    private static final String DATABASE_NAME = "weather_manager";

    // Contacts Table Columns names

    private final String KEY_PROJECT_ID = "project_id";
    private final String KEY_LOCATION_NAME = "location_name";
    private final String KEY_PRESSURE = "pressure";
    private final String KEY_HUMIDITY = "humidity";
    private final String KEY_SUNRISE = "sunrise";
    private final String KEY_SUNSET = "sunset";
    private final String KEY_CONDITION_TITLE = "conditionTitle";
    private final String KEY_CURRENT_TEMP = "currentTemp";

    // Contacts table name

    private final String WEATHER_TABLE = "weather";

    private final String CREATE_WEATHER_TABLE =
            "CREATE TABLE " + WEATHER_TABLE + "("
            + KEY_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LOCATION_NAME + " TEXT," + KEY_PRESSURE + " TEXT," +
                    "" + KEY_HUMIDITY + " TEXT," + KEY_SUNRISE + " TEXT," + KEY_SUNSET + " TEXT," + KEY_CONDITION_TITLE + " TEXT," +
            KEY_CURRENT_TEMP + " TEXT," + ");";

    private SQLiteDatabase database;

    public Weather_Helper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + WEATHER_TABLE);
        onCreate(db);
    }

    //add LocationModel to Database
    public boolean addLocation(WeatherModel weatherModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_PROJECT_ID, weatherModel.getId());
        values.put(KEY_LOCATION_NAME, weatherModel.getLocationName());
        values.put(KEY_PRESSURE, weatherModel.getPressure());
        values.put(KEY_HUMIDITY, weatherModel.getHumidity());
        values.put(KEY_SUNRISE, weatherModel.getSunrise());
        values.put(KEY_SUNSET, weatherModel.getSunset());
        values.put(KEY_CONDITION_TITLE, weatherModel.getConditionTitle());
        values.put(KEY_CURRENT_TEMP, weatherModel.getCurrentTemp());

        // Inserting Row
        long result = db.insert(WEATHER_TABLE, null, values);
        db.close(); // Closing database connection

        return result != -1;
    }

}
