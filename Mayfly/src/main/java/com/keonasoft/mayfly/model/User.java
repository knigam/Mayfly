package com.keonasoft.mayfly.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.activity.AppActivity;
import com.keonasoft.mayfly.helper.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kushal on 3/5/14.
 */
public class User {
    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private final String USER_EMAIL = "user_email";
    private final String USER_ID = "user_id";
    private int id;
    private String email;
    private User() {
    }

    public String setEmail(String email, Context appContext){
        this.email = email;
        final SharedPreferences prefs = appContext.getSharedPreferences(appContext.getString(R.string.package_name), appContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_EMAIL, email);
        editor.commit();

        return ourInstance.email;
    }

    public String getEmail(){
        return this.email;
    }

    public int setId(int id, Context appContext){
        this.id = id;
        final SharedPreferences prefs = appContext.getSharedPreferences(appContext.getString(R.string.package_name), appContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(USER_ID, id);
        editor.commit();

        return ourInstance.id;
    }

    public int getId() { return this.id; }

    /**
     * This will only sign out user from backend. This method should only be called through
     * an async task, and local user data must be cleared after the task executes.
     * @param URI
     * @return
     */
    public boolean signOut(final String URI) throws MyException{
        JSONObject result = null;
        try {
            result = HttpHelper.httpDelete(URI);
        } catch (Exception e) {
            throw new MyException(e);
        }
        try {
            if(result.getString("success").equals("true")) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * receives a json containing an array of friends of the user
     * and saves a string representing that array to a cache file
     * @param appContext
     */
    public void cacheFriends(Context appContext) throws MyException{
        String URI = appContext.getString(R.string.conn) + appContext.getString(R.string.friends_show);
        JSONObject result = null;
        try {
            result = HttpHelper.httpGet(URI);
        } catch (Exception e) {
            throw  new MyException(appContext, e);
        }
        final String FILENAME = appContext.getString(R.string.friends_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);

        JSONArray friends;
        try {
            friends = result.getJSONArray("friends");
        } catch (JSONException e) {
            throw new MyException(appContext, e);
        }

        BufferedWriter buf;
        try {
            buf = new BufferedWriter(new FileWriter(file));
            buf.write(friends.toString());
            buf.close();
        } catch (IOException e) {
            throw new MyException(appContext, e);
        }
    }

    /**
     * Takes the cached, stringified json representing the user's friends
     * and returns a map between friend's ids and names.
     * @return
     */
    public Map<Integer, String> getFriends(Context appContext) throws MyException{
        final String FILENAME = appContext.getString(R.string.friends_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);
        String friends;
        JSONArray friendsJson;
        Map<Integer, String> map = new HashMap<Integer, String>();
        BufferedReader buf;

        try {
            buf = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cacheFriends(appContext);
            return getFriends(appContext);
        }

        try {
            friends = buf.readLine();
            buf.close();
        } catch (IOException e) {
            throw new MyException(appContext, e);
        }

        try {
            friendsJson = new JSONArray(friends.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            cacheFriends(appContext);
            return getFriends(appContext);
        }

        for(int i = 0; i < friendsJson.length(); i++){

            String friendName;
            int friendId;
            try {
                JSONObject friend = friendsJson.getJSONObject(i);
                friendId = friend.getInt("id");
                friendName = friend.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                cacheFriends(appContext);
                return getFriends(appContext);
            }
            map.put(friendId, friendName);
        }

        return map;
    }

    /**
     * receives a json containing an array of all the events the user is invited to
     * and saves a string representing that array to a cache file
     * @param appContext
     */
    public void cacheEvents(Context appContext) throws MyException{
        String URI = appContext.getString(R.string.conn) + appContext.getString(R.string.events_show);
        JSONObject result = null;
        try {
            result = HttpHelper.httpGet(URI);
        } catch (Exception e) {
            throw new MyException(appContext, e);
        }
        final String FILENAME = appContext.getString(R.string.events_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);

        JSONArray events;
        try {
            events = result.getJSONArray("events");
        } catch (JSONException e) {
            throw new MyException(appContext, e);
        }

        BufferedWriter buf;
        try {
            buf = new BufferedWriter(new FileWriter(file));
            buf.write(events.toString());
            buf.close();
        } catch (IOException e) {
            throw new MyException(appContext, e);
        }
    }

    /**
     * Takes the cached, stringified json representing the user's events
     * and returns a map between event ids and names.
     * @return
     */
    public Map<Integer, String> getEvents(Context appContext, boolean attending, boolean creator) throws MyException{
        final String FILENAME = appContext.getString(R.string.events_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);
        String friends = new String();
        JSONArray eventsJson;
        Map<Integer, String> map = new HashMap<Integer, String>();
        BufferedReader buf;

        try {
            buf = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            cacheEvents(appContext);
            return getEvents(appContext, attending, creator);
        }

        try {
            friends = buf.readLine();
            buf.close();
        } catch (IOException e) {
            throw new MyException(appContext, e);
        }

        try {
            eventsJson = new JSONArray(friends.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            cacheEvents(appContext);
            return getEvents(appContext, attending, creator);
        }

        for(int i = 0; i < eventsJson.length(); i++){

            String eventName;
            int eventId;
            boolean add = true;
            boolean eAttending;
            boolean eCreator;
            try {
                JSONObject event = eventsJson.getJSONObject(i);
                eventId = event.getInt("id");
                eventName = event.getString("name");
                eAttending = event.getBoolean("attending");
                eCreator = event.getBoolean("creator");
            } catch (JSONException e) {
                e.printStackTrace();
                cacheEvents(appContext);
                return getEvents(appContext, attending, creator);
            }
            if(attending && !eAttending)
                add = false;
            if(creator && !eCreator)
                add = false;
            if(add)
                map.put(eventId, eventName);
        }

        return map;
    }
}
