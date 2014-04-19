package com.keonasoft.mayfly.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.activity.AppActivity;
import com.keonasoft.mayfly.helper.HttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

        try {
            JSONArray friends = result.getJSONArray("friends");
            FileOutputStream fos = appContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(friends.toString().getBytes());
            fos.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes the cached, stringified json representing the user's friends
     * and returns a map between friend's ids and names.
     * @return
     */
    public Map<Integer, String> getFriends(Context appContext){
        final String FILENAME = appContext.getString(R.string.friends_cache);

        Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            FileInputStream fis = appContext.openFileInput(FILENAME);
            StringBuilder friendsJson = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                friendsJson.append((char)ch);
            }

            JSONArray friends = new JSONArray(friendsJson.toString());
            for(int i = 0; i < friends.length(); i++){
                JSONObject friend = friends.getJSONObject(i);
                int friendId = friend.getInt("friend_id");
                String friendName = friend.getString("friend_name");
                map.put(friendId, friendName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
