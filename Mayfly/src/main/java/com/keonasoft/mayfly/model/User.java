package com.keonasoft.mayfly.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void signOut(Context appContext){
        final String URI= appContext.getString(R.string.conn) + appContext.getString(R.string.sign_out);
        final Context APP_CONTEXT = appContext;

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
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
            protected void onPostExecute(final Boolean success) {
                if (success) {
                    final SharedPreferences prefs = APP_CONTEXT.getSharedPreferences(APP_CONTEXT.getString(R.string.package_name), APP_CONTEXT.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_EMAIL, "");
                    editor.commit();
                    ourInstance.email = null;
                } else {
                }
            }
        }.execute((Void) null);
    }

}
