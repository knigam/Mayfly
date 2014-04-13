package com.keonasoft.mayfly.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.sql.SQLClientInfoException;

/**
 * Created by kushal on 4/12/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    //Database Name
    private static final String DATABASE_NAME = "MayflyDB";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // SQL statement to create friends table
        String CREATE_FRIEND_TABLE = "CREATE TABLE friends ( " +
                "friendshipId INTEGER PRIMARY KEY, " +
                "userId INTEGER, "+
                "groupId INTEGER, "+
                "friendName TEXT, "+
                "userName TEXT, "+
                "groupName TEXT )";
        // SQL statement to create events table
        String CREATE_EVENT_TABLE = "CREATE TABLE events ( " +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, "+
                "description TEXT, "+
                "time NUMERIC, "+
                "location TEXT, "+
                "min INTEGER, "+
                "max INTEGER, "+
                "attending INTEGER, "+
                "master INTEGER )";
        // SQL statement to create attending_users table
        String CREATE_ATTENDING_USER_TABLE = "CREATE TABLE attending_users ( " +
                "userId INTEGER, "+
                "eventId INTEGER, "+
                "userName TEXT )";

        // SQL statements to create tables
        db.execSQL(CREATE_FRIEND_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_ATTENDING_USER_TABLE);

        JSONObject friendships = HttpHelper.httpGet("www.mymayfly.com/friendships");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS friends");
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS attending_users");

        // re-create tables for db
        this.onCreate(db);
    }


    /**
     * Drops the current SQLite database
     * this should be called when a user logs out
     * @param context
     */
    public void dropSQLiteDatabase(Context context){
        this.close();
        context.deleteDatabase(DATABASE_NAME);
    }
}
