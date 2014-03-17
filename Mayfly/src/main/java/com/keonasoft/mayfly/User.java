package com.keonasoft.mayfly;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

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

    private String USER_EMAIL = "user_email";
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
