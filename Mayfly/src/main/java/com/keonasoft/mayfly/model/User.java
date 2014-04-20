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
    public boolean signOut(final String URI){
        JSONObject result = HttpHelper.httpDelete(URI);
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
    public void cacheFriends(Context appContext){
        String URI = appContext.getString(R.string.conn) + appContext.getString(R.string.friends);
        JSONObject result = HttpHelper.httpGet(URI);
        final String FILENAME = appContext.getString(R.string.friends_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);

        JSONArray friends = null;
        try {
            friends = result.getJSONArray("friends");
        } catch (JSONException e) {
            throw new MyException(appContext, e);
        }

        BufferedWriter buf = null;
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
    public Map<String, Integer> getFriends(Context appContext){
        final String FILENAME = appContext.getString(R.string.friends_cache);
        File file = new File(appContext.getCacheDir(), FILENAME);
        String friends = new String();
        JSONArray friendsJson = new JSONArray();
        Map<String, Integer> map = new HashMap<String, Integer>();
        BufferedReader buf = null;

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

            String friendName = null;
            int friendId = 0;
            try {
                JSONObject friend = friendsJson.getJSONObject(i);
                friendId = friend.getInt("id");
                friendName = friend.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                cacheFriends(appContext);
                return getFriends(appContext);
            }
            map.put(friendName, friendId);
        }

        return map;
    }
}
