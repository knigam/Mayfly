package com.keonasoft.mayfly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends FragmentActivity {

    private MainFragment mainFragment;
    private static final String TAG = "MainActivity";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final static String USER_EMAIL = "user_email";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        HttpHelper.ourInstance.initialize(getApplicationContext()); //initialize singalton httphelper object

        // Check device for Play Services APK.
        if (checkPlayServices()) {
            if (savedInstanceState == null) {
                // Add the fragment on initial activity setup
                mainFragment = new MainFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, mainFragment)
                        .commit();
            } else {
                // Or set the fragment from restored state info
                mainFragment = (MainFragment) getSupportFragmentManager()
                        .findFragmentById(android.R.id.content);
            }
        }
        else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        //Checks to see if user has data stored in RAM
        if (User.getInstance().getEmail() == null){
            //If no data, try to get data from shared prefs
            final SharedPreferences prefs = context.getSharedPreferences(getString(R.string.package_name), MODE_PRIVATE);
            String mEmail = prefs.getString(USER_EMAIL, "");
            if (!mEmail.isEmpty()) {
                User.getInstance().setEmail(mEmail, context);
            }
            System.out.println(User.getInstance().getEmail() + "YAY!!!");
        }
        //If user now has data in RAM, go on to AppActivity, otherwise continue with MainActivity
        if (User.getInstance().getEmail() != null){
            Intent intent = new Intent(MainActivity.this, AppActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * On click listener for the Mayfly login button
     * @param view
     */
    public void logInOnClick(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Fragment for Facebook login
     */
    public class MainFragment extends Fragment {

        private static final String TAG = "MainFragment";
        private UiLifecycleHelper uiHelper;
        private Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        };

        public MainFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            uiHelper = new UiLifecycleHelper(getActivity(), callback);
            uiHelper.onCreate(savedInstanceState);
        }

        @Override
        public void onResume() {
            super.onResume();

            // For scenarios where the main activity is launched and user
            // session is not null, the session state change notification
            // may not be triggered. Trigger it if it's open/closed.
            Session session = Session.getActiveSession();
            if (session != null &&
                    (session.isOpened() || session.isClosed()) ) {
                onSessionStateChange(session, session.getState(), null);
            }

            uiHelper.onResume();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onPause() {
            super.onPause();
            uiHelper.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            uiHelper.onDestroy();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.main, container, false);

            LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
            authButton.setFragment(this);

            return view;
        }

        private void onSessionStateChange(Session session, SessionState state, Exception exception) {
           if (state.isOpened()) {
                Log.i(TAG, "Logged in...");
               Intent intent = new Intent(getActivity(), AppActivity.class);
               startActivity(intent);
               finish();
               //The session is now logged in
           }
           else if (state.isClosed()) {
                Log.i(TAG, "Logged out...");
           }
        }
    }

}
