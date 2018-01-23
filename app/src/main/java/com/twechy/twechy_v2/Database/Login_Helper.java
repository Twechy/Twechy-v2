package com.twechy.twechy_v2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Login_Helper  extends SQLiteOpenHelper {

    // Database Management Settings
    private static final String LOGIN_MANAGER = "location_manager";
    private static final int DATABASE_VERSION = 1;

    // Collection Of DataBase Fields
    private static final String[] TABLE_LOGING_KEYS =
            {"id", "user_name", "password"};

    // DataBase Name
    private final String KEY_USER_ID = "id";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_PASSWORD = "password";

    // DataBase Name
    private final String LOGIN_TABLE = "login";

    public Login_Helper(Context context) {
        super(context, LOGIN_MANAGER, null, DATABASE_VERSION);
    }

    private final String CREATE_LOGIN_TABLE = "CREATE TABLE "+ LOGIN_TABLE +"( "+KEY_USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            KEY_USER_NAME+" TEXT NOT NULL, "+ KEY_PASSWORD +" TEXT NOT NULL "+");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOGIN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
        onCreate(db);
    }

    public boolean addLocation(LoginModel loginModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_USER_NAME, loginModel.getUserName());
        values.put(KEY_PASSWORD, loginModel.getPassword());

        // Inserting Row
        long result = db.insert(LOGIN_TABLE, null, values);

        db.close(); // Closing database connection
        Log.i("DatabaseSet", String.valueOf(result));
        return result != -1;
    }

    public boolean getUser(String email, String password){

        String selectQeury = "select * from "+LOGIN_TABLE +" where "+KEY_USER_NAME+" = "+"'"+email+"'"+
                " and "+KEY_PASSWORD+" = "+"'"+password+"'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQeury, null);

        c.moveToFirst();
        if (c.getCount() > 0) {
            return true;
        }
        c.close();
        return false ;
    }

    public List<LoginModel> LoginToListView() {

        List<LoginModel> loginModelList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + LOGIN_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                LoginModel loginModel = new LoginModel();

                loginModel.setId(c.getInt(0));
                loginModel.setUserName(c.getString(1));
                loginModel.setPassword(c.getString(2));

                // Adding contact to list
                loginModelList.add(loginModel);
            } while (c.moveToNext());
        }
        c.close();

        // return LocationModel List
        return loginModelList;
    }

}
