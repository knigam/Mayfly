package com.keonasoft.mayfly.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InviteActivity extends Activity {

    Context context;
    private Map<Integer, String> friendMap;
    private int eventId;
    List<String> friendNames;
    List<Integer> friendIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        eventId = intent.getIntExtra("eventId", -1);
        context = getApplicationContext();

        setContentView(R.layout.activity_invite);
        TabHost tabHost = (TabHost) this.findViewById(R.id.inviteTabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Friends");
        tabSpec.setContent(R.id.inviteFriendTab);
        tabSpec.setIndicator("Friends");
        tabHost.addTab(tabSpec);
        displayFriends();

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("Groups");
        tabSpec2.setContent(R.id.inviteGroupTab);
        tabSpec2.setIndicator("Groups");
        tabHost.addTab(tabSpec2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invite, menu);
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

    public void inviteButtonOnClick(View v){
        final String URI = getString(R.string.conn) + getString(R.string.invite_create);
        JSONArray users = new JSONArray();
        ListView friendView = (ListView) findViewById(R.id.inviteFriendListView);
        SparseBooleanArray checkedItems = friendView.getCheckedItemPositions();
        if(checkedItems != null){
            for(int i = 0; i < checkedItems.size(); i++){
                if(checkedItems.valueAt(i)){
                    int position = checkedItems.keyAt(i);
                    users.put(friendIds.get(position));
                }
            }
        }
        final JSONArray USERS = users;
        new AsyncTask<Void,Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params){
                JSONObject result = new JSONObject();
                try {
                    result.put("event_id", eventId);
                    result.put("users", USERS);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                try {
                    result = HttpHelper.httpPost(URI, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                try {
                    if(result.getBoolean("success"))
                        return true;
                    else
                        return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            @Override
            protected void onPostExecute(Boolean success){
                if(success){
                    Intent intent = new Intent(InviteActivity.this, EventActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(context, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    private void displayFriends(){

        final ListView FRIENDVIEW = (ListView) findViewById(R.id.inviteFriendListView);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                try {
                    friendMap = User.getInstance().getFriends(context);
                } catch (MyException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                friendNames = new ArrayList<String>();
                friendIds = new ArrayList<Integer>();

                for (Integer key: friendMap.keySet()){
                    friendNames.add(friendMap.get(key));
                    friendIds.add(key);
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InviteActivity.this,
                        android.R.layout.simple_list_item_multiple_choice, friendNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                FRIENDVIEW.setAdapter(dataAdapter);
            }
        }.execute(null, null, null);
    }
}
