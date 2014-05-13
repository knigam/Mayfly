package com.keonasoft.mayfly.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.fragment.AttendingEventsFragment;
import com.keonasoft.mayfly.fragment.EventsFragment;
import com.keonasoft.mayfly.fragment.FriendsFragment;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.fragment.MyEventsFragment;
import com.keonasoft.mayfly.fragment.NavigationDrawerFragment;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.spec.ECField;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

;

public class AppActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    Context context;
    String regid;
    GoogleCloudMessaging gcm;
    private static final String TAG = "AppActivity";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String USER_EMAIL = "user_email";
    String SENDER_ID = "230395939091";
    TextView mDisplay;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);
        if (regid.isEmpty()) {
            registerInBackground();
        }
        setContentView(R.layout.activity_app);
        
        recacheEvents();
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        System.out.println(regid);
    }

    @Override
    protected void onResume(){
        super.onResume();
        recacheEvents();
    }

    private void recacheEvents(){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params){
                try {
                    User.getInstance().cacheEvents(context);
                } catch (MyException e) {
                    return false;
                }
                return true;
            }
            @Override
            protected void onPostExecute(Boolean success){
                if(!success)
                    Toast.makeText(AppActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        }.execute(null, null, null);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        final String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            deleteRegistrationIDFromBackend(registrationId);
            return "";
        }
/*        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params){
                boolean success = sendRegistrationIdToBackend(registrationId);
                return success;
            }
            protected void onPostExecute(final Boolean success) {
                if (!success) {
                    signUserOut();
                }
            }
        }.execute(null, null, null);*/
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(AppActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend(regid);

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                System.out.println(msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private boolean sendRegistrationIdToBackend(String registrationID) {
        JSONObject result;
        String uri = getString(R.string.conn) + getString(R.string.devices_create);
        Map<String, String> map = new HashMap<String, String>();
        map.put("reg_id", registrationID);
        map.put("type", "Android");

        JSONObject json = HttpHelper.jsonBuilder(map);
        JSONObject userJson = new JSONObject();
        try {
            userJson.put("device", json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            result = HttpHelper.httpPost(uri, userJson);
        } catch (Exception e) {
            return false;
        }
        try {
            String success = result.getString("success");
            if(success.equals("true")){
                Log.i(TAG, "Registration Successfully sent to backend");
                return true;
            }
            else if (success.equals("false")){
                Log.i(TAG, "Registration NOT Successfully sent to backend");
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Removes an outdated registration ID in the case that the app was updated and the key must be replaced
     */
    private void deleteRegistrationIDFromBackend(final String registrationID){
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params){
                String uri = getString(R.string.conn) + getString(R.string.devices_destroy);
                Map<String, String> map = new HashMap<String, String>();
                map.put("reg_id", registrationID);

                JSONObject json = HttpHelper.jsonBuilder(map);
                JSONObject userJson = new JSONObject();
                try {
                    userJson.put("device", json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject result = null;
                try {
                    result = HttpHelper.httpPost(uri, userJson);
                } catch (Exception e) {
                    return false;
                }
                try {
                    String success = result.getString("success");
                    if(success.equals("true")){
                        Log.i(TAG, "Registration Successfully deleted from backend");
                        return true;
                    }
                    else if (success.equals("false")){
                        Log.i(TAG, "Registration NOT Successfully deleted from backend");
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }.execute(null, null, null);
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, EventsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MyEventsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AttendingEventsFragment.newInstance(position + 1))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FriendsFragment.newInstance(position + 1))
                        .commit();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.app, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout){
            Session session = Session.getActiveSession();
            deleteRegistrationIDFromBackend(regid);
            signUserOut();
        }
        if (id == R.id.action_new_event){
            createNewEvent();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method starts the new event activity
     */
    private void createNewEvent(){
        Intent intent = new Intent(AppActivity.this, NewEventActivity.class);
        startActivity(intent);
    }

    /**
     * This method calls the User's sign out method and clears user data if the user is successfully
     * signed out from the backend. The app then switches to the main activity.
     */
    private void signUserOut(){
        final String URI= getString(R.string.conn) + getString(R.string.sign_out);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                try {
                    return User.getInstance().signOut(URI);
                } catch (MyException e) {
                    return false;
                }
            }
            protected void onPostExecute(final Boolean success) {
                if (success) {
                    // Clear shared prefs
                    final SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.package_name), context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(USER_EMAIL, "");
                    editor.commit();
                    User.getInstance().setEmail(null, context);

                    // Clear cache files
                    String friendsCacheFileName = getString(R.string.friends_cache);
                    String eventsCacheFileName = getString(R.string.events_cache);
                    File file = new File(context.getCacheDir(), friendsCacheFileName);
                    file.delete();
                    file = new File(context.getCacheDir(), eventsCacheFileName);
                    file.delete();

                    //Exiting activity and switching to main activity
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute((Void) null);
    }
}
