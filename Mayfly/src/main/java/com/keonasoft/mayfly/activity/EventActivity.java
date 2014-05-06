package com.keonasoft.mayfly.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.model.Event;

import org.w3c.dom.Text;

public class EventActivity extends Activity {

    private int eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getIntExtra("eventId", -1);
        event = new Event(eventId);
        final String URI = getString(R.string.conn) + getString(R.string.event_show) + "/" + eventId + ".json";

        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... params) {
                event = event.getEvent(URI);
                if(event == null)
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
                    TextView time = (TextView) findViewById(R.id.eventTimeTextView);
                    TextView location = (TextView) findViewById(R.id.eventLocationTextView);
                    LinearLayout minMaxLayout = (LinearLayout) findViewById(R.id.minMaxLayout);

                    name.setText(event.getName());
                    description.setText(event.getDescription());
                    time.setText(event.getTime());
                    location.setText(event.getLocation());
                    if(event.getMin() != -1) {
                        TextView min = new TextView(EventActivity.this);
                        min.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float)0.5));
                        min.setText("min: " + event.getMin());
                        minMaxLayout.addView(min);
                    }
                    if(event.getMin() != -1) {
                        TextView max = new TextView(EventActivity.this);
                        max.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, (float)0.5));
                        max.setText("max: " + event.getMax());
                        minMaxLayout.addView(max);
                    }
                }
                else {
                    Toast.makeText(EventActivity.this, "Can't connect to network", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute(null, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.event, menu);
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
}
