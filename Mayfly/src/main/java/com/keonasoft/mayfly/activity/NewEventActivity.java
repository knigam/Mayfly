package com.keonasoft.mayfly.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.Event;
import com.keonasoft.mayfly.model.User;

import org.json.JSONException;
import org.json.JSONObject;

public class NewEventActivity extends Activity {

    //Values for creating a new event
    private CreateEventTask mEventTask = null;
    private Event mEvent;

    //UI References
    private EditText mNameView;
    private EditText mDescriptionView;
    private TimePicker mStartTimeView;
    private TimePicker mEndTimeView;
    private EditText mLocationView;
    private EditText mMinView;
    private EditText mMaxView;
    private Button submitBtn;
    private ToggleButton openBtn;

    private View mCreateEventView;
    private View mCreateEventStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        mEvent = new Event();
        mNameView = (EditText) findViewById(R.id.newEventNameEditText);
        mDescriptionView = (EditText) findViewById(R.id.newEventDescriptionEditText);
        mStartTimeView = (TimePicker) findViewById(R.id.newEventStartTimePicker);
        mEndTimeView = (TimePicker) findViewById(R.id.newEventEndTimePicker);
        mLocationView = (EditText) findViewById(R.id.newEventLocationEditText);
        mMinView = (EditText) findViewById(R.id.newEventMinEditText);
        mMaxView = (EditText) findViewById(R.id.newEventMaxEditText);
        submitBtn = (Button) findViewById(R.id.newEventSubmitButton);
        openBtn = (ToggleButton) findViewById(R.id.newEventOpenToggleButton);

        mCreateEventView = findViewById(R.id.create_event_scroll_view);
        mCreateEventStatusView = findViewById(R.id.create_event_status);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * Validates the event data entered in form and sets errors where appropriate.
     * If there are no errors, then the progress spinner and event creation tasks
     * are started.
     */
    public void createNewEvent(){
        if (mEventTask != null) {
            return;
        }

        // Reset errors.
        mNameView.setError(null);
        mDescriptionView.setError(null);;
        mLocationView.setError(null);;
        mMinView.setError(null);;
        mMaxView.setError(null);;
        submitBtn.setError(null);;

        boolean cancel = false;
        View focusView = null;

        //Keep track of whether the min and max are given and valid ints
        boolean validMinExists = false;
        boolean validMaxExists = false;

        //Create Event based on values
        mEvent.setName(mNameView.getText().toString());
        mEvent.setDescription(mDescriptionView.getText().toString());
        mEvent.setLocation(mLocationView.getText().toString());
        mEvent.setOpen(openBtn.isChecked());
        mEvent.setStartTime(mStartTimeView.getCurrentHour() + ":" + mStartTimeView.getCurrentMinute());
        mEvent.setEndTime(mEndTimeView.getCurrentHour() + ":" + mEndTimeView.getCurrentMinute());

        //Check to make sure description is less than 255 chars
        if (!TextUtils.isEmpty(mEvent.getDescription()) && mEvent.getDescription().length() > 255) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        //Check for valid min and max values
        if(!TextUtils.isEmpty(mMinView.getText()) && Integer.valueOf(mMinView.getText().toString()) > -1){
            try {
                mEvent.setMin(Integer.valueOf(mMinView.getText().toString()));
                validMinExists = true;
            } catch(NumberFormatException e) {
                mMinView.setError(getString(R.string.error_not_int));
                focusView = mMinView;
                cancel = true;
            }
        }
        else {
            mEvent.setMin(-1);
        }

        if(!TextUtils.isEmpty(mMaxView.getText()) && Integer.valueOf(mMaxView.getText().toString()) > -1) {
            try {
                mEvent.setMax(Integer.valueOf(mMaxView.getText().toString()));
                validMaxExists = true;
            } catch (NumberFormatException e) {
                mMaxView.setError(getString(R.string.error_not_int));
                focusView = mMaxView;
                cancel = true;
            }
        }
        else {
            mEvent.setMax(-1);
        }

        //If both min and max are given and valid ints, make sure min is less than max
        if(validMinExists && validMaxExists){
            if(mEvent.getMax() < mEvent.getMin()){
                mMinView.setError(getString(R.string.error_min_max));
                mMaxView.setError(getString(R.string.error_min_max));
                focusView = mMaxView;
                cancel = true;
            }

        }

        // Check for required location
        if (TextUtils.isEmpty(mEvent.getLocation())) {
            mLocationView.setError(getString(R.string.error_field_required));
            focusView = mLocationView;
            cancel = true;
        }

        //Check for required name
        if (TextUtils.isEmpty(mEvent.getName())) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the event creation event
            showProgress(true);
            mEventTask = new CreateEventTask();
            mEventTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the new event form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateEventStatusView.setVisibility(View.VISIBLE);
            mCreateEventStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCreateEventStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mCreateEventView.setVisibility(View.VISIBLE);
            mCreateEventView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mCreateEventStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCreateEventView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous task used to send the new event info
     * to the backend
     */
    public class CreateEventTask extends AsyncTask<Void, Void, Boolean> {
        final String URI = getString(R.string.conn) + getString(R.string.events_create);
        String message = "Error connecting to network";
        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject json = new JSONObject();
            try {
                json.put("name", mEvent.getName());
                json.put("location", mEvent.getLocation());
                json.put("start_time", mEvent.getStartTime());
                json.put("end_time", mEvent.getEndTime());
                json.put("min", mEvent.getMin());
                json.put("max", mEvent.getMax());
                json.put("description", mEvent.getDescription());
                json.put("open", mEvent.getOpen());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            try {
                json = HttpHelper.httpPost(URI, json);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                if(json.getBoolean("success")){
                    mEvent.setId(json.getInt("event_id"));
                    //tries to recache events now so information is up to date
                    User.getInstance().cacheEvents(getApplicationContext());
                    return true;
                }
                else{
                    message = json.getString("message");
                    return false;
                }
            } catch (MyException e) {
                //even if the re-cache fails, the event was successfully created
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mEventTask = null;
            showProgress(false);

            if(success){
                Intent intent = new Intent(NewEventActivity.this, InviteActivity.class);
                intent.putExtra("eventId", mEvent.getId());
                intent.putExtra("newEvent", true);
                startActivity(intent);
                finish();
            }
            else {
                mNameView.setError(message);
                mNameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mEventTask = null;
            showProgress(false);
        }
    }


}
