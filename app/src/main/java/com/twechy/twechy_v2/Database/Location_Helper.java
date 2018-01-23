package com.twechy.twechy_v2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Location_Helper extends SQLiteOpenHelper {

    private static final String[] TABLE_LOCATION_KEYS =
            {"project_id", "location_name", "longitude", "latitude", "location_description", "time", "image"};
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 11;
    // Database Name
    private static final String LOCATION_MANAGER = "location_manager";
    // Database Name
    private static final String MARKERS_MANAGER = "markers_manager";
    private final String KEY_PROJECT_ID = "project_id";
    private final String KEY_LOCATION_NAME = "location_name";
    private final String KEY_LOCATION_DESC = "location_description";
    private final String KEY_LATITUDE = "latitude";
    private final String KEY_LONGITUDE = "longitude";
    private final String KEY_TIME = "time";
    private final String KEY_ALTITUDE = "altitude";
    private final String KEY_BEARING = "bearing";
    private final String KEY_SPEED = "speed";
    private final String KEY_IMAGE = "image";
    // Location table name
    private final String LOCATION_TABLE = "locations";
    // Markers Table Column Names
    private final String MARKER_ID = "marker_id";
    private final String MARKER_NAME = "name";
    private final String MARKER_LATITUDE = "latitude";
    private final String MARKER_LONGITUDE = "longitude";
    // Markers Table Name
    private final String MARKERS_TABLE = "markers";
    private final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + LOCATION_TABLE + "("
            + KEY_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LOCATION_NAME + " TEXT," + KEY_LOCATION_DESC + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_TIME + " TEXT," + KEY_IMAGE + " BLOB" + ");";

    private final String CREATE_MARKERS_TABLE = "CREATE TABLE " +
            "" + MARKERS_TABLE + "("
            + MARKER_ID + " INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + MARKER_NAME + " TEXT NOT NULL ," + MARKER_LATITUDE + " DOUBLE NOT NULL," + MARKER_LONGITUDE + " DOUBLE NOT NULL " + ");";

    // Location Table Columns names
    private long id;
    private SQLiteDatabase database;

    public Location_Helper(Context context) {
        super(context, LOCATION_MANAGER, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MARKERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MARKERS_TABLE);
        onCreate(db);
    }

    //add LocationModel to Database
    public boolean addLocation(LocationModel locationModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //values.put(KEY_PROJECT_ID, locationModel.getProjectId());
        values.put(KEY_LOCATION_NAME, locationModel.getLocationName());
        values.put(KEY_LOCATION_DESC, locationModel.getLocationDescription());
        values.put(KEY_LATITUDE, locationModel.getLatitude());
        values.put(KEY_LONGITUDE, locationModel.getLongitude());
        values.put(KEY_TIME, locationModel.getTime());
        values.put(KEY_IMAGE, locationModel.getImage());
        /*values.put(KEY_ALTITUDE, locationModel.getAltitude());
        values.put(KEY_BEARING, locationModel.getBearing());
        values.put(KEY_SPEED, locationModel.getSpeed());
        values.put(KEY_TIME, locationModel.getTime());*/
        // Inserting Row
        long result = db.insert(LOCATION_TABLE, null, values);

        db.close(); // Closing database connection
        Log.i("DatabaseSet", String.valueOf(result));
        return result != -1;
    }

    //add LocationModel to Database
    public boolean addMarker(MarkerModel marker) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MARKER_NAME, marker.getMarkerName());
        values.put(MARKER_LONGITUDE, marker.getMarkerLongitude());
        values.put(MARKER_LATITUDE, marker.getMarkerLatitude());
        // Inserting Row
        long result = db.insert(MARKERS_TABLE, null, values);

        db.close(); // Closing database connection
        Log.i("DatabaseSet", String.valueOf(result));
        return result != -1;
    }

    // Getting All LocationModel
    public List<LocationModel> locationFromDB() {

        List<LocationModel> locationModelList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LOCATION_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();

                locationModel.setProjectId(c.getInt(0));
                locationModel.setLocationName(c.getString(1));
                locationModel.setLatitude(c.getDouble(2));
                locationModel.setLongitude(c.getDouble(3));
                locationModel.setAltitude(c.getDouble(4));
                locationModel.setBearing(c.getFloat(5));
                locationModel.setSpeed(c.getFloat(6));
                locationModel.setAccuracy(c.getFloat(7));

                // Adding contact to list
                locationModelList.add(locationModel);
            } while (c.moveToNext());
        }
        c.close();
        // return LocationModel List
        return locationModelList;
    }

    protected long saveBitmap(SQLiteDatabase database, Bitmap bmp) {
        int size = bmp.getRowBytes() * bmp.getHeight();
        ByteBuffer b = ByteBuffer.allocate(size);
        bmp.copyPixelsToBuffer(b);
        byte[] bytes = new byte[size];
        b.get(bytes, 0, bytes.length);
        ContentValues cv = new ContentValues();
        cv.put(KEY_IMAGE, bytes);
        this.id = database.insert(LOCATION_TABLE, null, cv);

        return id;
    }

    // Getting All LocationModel
    public List<LocationModel> locationToListView() {

        List<LocationModel> locationModelList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LOCATION_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();

                locationModel.setLocationName(c.getString(1));
                locationModel.setLocationDescription(c.getString(2));
                locationModel.setLatitude(c.getDouble(3));
                locationModel.setLongitude(c.getDouble(4));
                locationModel.setTime(c.getFloat(5));
                locationModel.setImage(c.getBlob(6));

                // Adding contact to list
                locationModelList.add(locationModel);
            } while (c.moveToNext());
        }
        c.close();

        // return LocationModel List
        return locationModelList;
    }

    public List<MarkerModel> getAllMarker() {
        List<MarkerModel> result = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MARKERS_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                MarkerModel markerModel = new MarkerModel();

                markerModel.setMarkerName(c.getString(1));
                markerModel.setMarkerLatitude(c.getDouble(2));
                markerModel.setMarkerLongitude(c.getDouble(3));

                // Adding contact to list
                result.add(markerModel);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return result;
    }


    public LocationModel showLocationFromDb(String name) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(LOCATION_TABLE, TABLE_LOCATION_KEYS, KEY_LOCATION_NAME + "=?",
                new String[]{String.valueOf((name))}, null, null, null, null);
/*
        String selectQuery = String.format("SELECT * FROM %s WHERE location_name = '%s'", LOCATION_TABLE, name);

        Cursor cursor = db.rawQuery(selectQuery, null);
*/
        if (cursor != null)
            cursor.moveToFirst();

        assert cursor != null;

        //myLocationModel = new LocationModel(name, lng, lat, time, description, image);

        LocationModel location = new LocationModel
                (cursor.getString(1), Double.parseDouble(cursor.getString(2)),
                        Double.parseDouble(cursor.getString(3)), cursor.getString(4), cursor.getLong(5), cursor.getBlob(6));

        // return Location
        cursor.close();
        return location;

    }

    // Deleting single location
    public boolean deleteLocation(String locationName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int location = db.delete(LOCATION_TABLE, KEY_LOCATION_NAME + " = ?",
                new String[]{String.valueOf(locationName)});
        return location != -1;
    }


}
