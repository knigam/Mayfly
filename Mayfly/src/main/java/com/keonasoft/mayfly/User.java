package com.keonasoft.mayfly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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

    private String email;
    private User() {
    }

    public String setEmail(String email){
        ourInstance.email = email;

        return ourInstance.email;
    }

    public String getEmail(){
        return ourInstance.email;
    }

    public void signOut(Context context){
        final String URI= context.getString(R.string.conn) + context.getString(R.string.sign_out);

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
                    /*final SharedPreferences prefs = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_EMAIL, "");
                    editor.commit();
                    User.getInstance().signOut();*/
                    ourInstance.email = null;
                } else {
                }
            }
        }.execute((Void) null);
    }

}
