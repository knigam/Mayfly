package com.keonasoft.mayfly.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.Event;
import com.keonasoft.mayfly.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventActivity extends Activity {

    private int eventId;
    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getIntExtra("eventId", -1);
        mEvent = new Event(eventId);
        final String URI = getString(R.string.conn) + getString(R.string.event_show) + "/" + eventId + ".json";

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                mEvent = mEvent.getEvent(URI);
                if(mEvent == null)
                    return false;
                else
                    return true;
            }
            @Override
            protected void onPostExecute(final Boolean success){
                if(success){
                    setContentView(R.layout.activity_event);
                    TextView name = (TextView) findViewById(R.id.eventNameTextView);
                    TextView description = (TextView) findViewById(R.id.eventDescriptionTextView);
                    TextView startTime = (TextView) findViewById(R.id.eventStartTimeTextView);
                    TextView endTime = (TextView) findViewById(R.id.eventEndTimeTextView);
                    TextView location = (TextView) findViewById(R.id.eventLocationTextView);
                    LinearLayout minMaxLayout = (LinearLayout) findViewById(R.id.minMaxLayout);
                    ToggleButton eventAttendingToggleButton = (ToggleButton) findViewById(R.id.eventAttendingToggleButton);
                    ListView usersAttendingListView = (ListView) findViewById(R.id.usersAttendingListView);

                    name.setText(mEvent.getName());
                    description.setText(mEvent.getDescription());
                    startTime.setText(mEvent.getStartTime());
                    endTime.setText(mEvent.getEndTime());
                    location.setText(mEvent.getLocation());

                    //Determine if the min and max fields are applicable
                    if(mEvent.getMin() != -1) {
                        TextView min = new TextView(EventActivity.this);
                        min.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float)0.5));
                        min.setText("min: " + mEvent.getMin());
                        minMaxLayout.addView(min);
                    }
                    if(mEvent.getMax() != -1) {
                        TextView max = new TextView(EventActivity.this);
                        max.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float)0.5));
                        max.setText("max: " + mEvent.getMax());
                        minMaxLayout.addView(max);
                    }

                    //Determine if the attending toggle should be checked or not
                    if(mEvent.getAttending())
                        eventAttendingToggleButton.setChecked(true);

                    //Populate list of attending users
                    List<String> userNames = new ArrayList<String>();
                    List<Integer> userIds = new ArrayList<Integer>();

                    for (Integer key: mEvent.getUsersAttending().keySet()){
                        userIds.add(key);
                        userNames.add(mEvent.getUsersAttending().get(key));
                    }
                    final List<Integer> EVENT_IDS = userIds;

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EventActivity.this,
                            android.R.layout.simple_list_item_1, userNames);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    usersAttendingListView.setAdapter(dataAdapter);

//                    usersAttendingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            int eventId = EVENT_IDS.get(position);
//                            Intent intent = new Intent(EventActivity.this, EventActivity.class);
//                            intent.putExtra("eventId", eventId);
//                            startActivity(intent);
//                        }
//                    });
                }
                else {
                    Toast.makeText(EventActivity.this, "Can't connect to network", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute(null, null, null);
    }

    /**
     * On click listener for the Mayfly login button
     * @param view
     */
    public void toggleAttendingEvent(View view){
        final ToggleButton eventAttendingToggleButton = (ToggleButton) findViewById(R.id.eventAttendingToggleButton);
        final String URI = getString(R.string.conn) + getString(R.string.invite_update);

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject result = new JSONObject();

                try {
                    result.put("event_id", mEvent.getId());
                    result.put("attending", eventAttendingToggleButton.isChecked());
                } catch (JSONException e) {
                    return false;
                }

                try {
                    result = HttpHelper.httpPost(URI, result);
                } catch (Exception e) {
                    return false;
                }
                try {
                    if(result.getBoolean("success")){
                        try {
                            User.getInstance().cacheEvents(EventActivity.this);
                        } catch (MyException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                } catch (JSONException e) {
                    return false;
                }
            }
            @Override
            protected void onPostExecute(final Boolean success){
                if(success){
                    finish();
                    startActivity(getIntent());
                }
                else {
                    eventAttendingToggleButton.setChecked(mEvent.getAttending());
                    Toast.makeText(EventActivity.this, "Can't update attending status", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    /**
     * Deletes the current event
     */
    public void deleteEvent(){
        final String URI = getString(R.string.conn) + getString(R.string.event_destroy);
        if(!mEvent.getCreator()){
            Toast.makeText(EventActivity.this, "Only the owner of the event can do that", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<Void, Void, Boolean>(){
            String message = getString(R.string.error_network);
            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject result = new JSONObject();

                try {
                    result.put("event_id", mEvent.getId());
                } catch (JSONException e) {
                    return false;
                }

                try {
                    result = HttpHelper.httpPost(URI, result);
                } catch (Exception e) {
                    return false;
                }
                try {
                    if(result.getBoolean("success")){
                        try {
                            User.getInstance().cacheEvents(EventActivity.this);
                        } catch (MyException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                    else {
                        message = result.getString("message");
                        return false;
                    }
                } catch (JSONException e) {
                    return false;
                }
            }
            @Override
            protected void onPostExecute(final Boolean success){
                if(success){
                    finish();
                }
                else {
                    Toast.makeText(EventActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_invite) {
            Intent intent = new Intent(EventActivity.this, InviteActivity.class);
            intent.putExtra("eventId", mEvent.getId());
            intent.putExtra("newEvent", false);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete){
            deleteEvent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
