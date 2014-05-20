package com.keonasoft.mayfly.activity;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FriendsActivity extends ActionBarActivity {

    private Map<Integer, String> friendMap;

    List<String> friendNames;
    List<Integer> friendIds;

    //Layout Views
    private TextView friendSearch;
    private Button friendSearchBtn;
    private Button friendAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        friendNames = new ArrayList<String>();
        friendIds = new ArrayList<Integer>();

        displayFriends();

        friendSearch = (TextView) findViewById(R.id.friendSearchTextView);
        friendSearchBtn = (Button) findViewById(R.id.friendSearchButton);
        friendSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriends();
            }
        });
        friendAddBtn = (Button) findViewById(R.id.friendAddButton);
        friendAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.friendListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.friends, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
//            case R.id.renameFriend:
//                renameFriend(friendIds.get(info.position));
//                return true;
            case R.id.action_delete:
                System.out.println(friendIds.size());
                deleteFriend(friendIds.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    protected void searchFriends(){
        //TODO
    }

    protected void addFriend(){
        final String FRIEND_EMAIL = friendSearch.getText().toString();
        final String URI = getString(R.string.conn) + getString(R.string.friends_create);
        View focusView = null;
        boolean cancel = false;

        if(TextUtils.isEmpty(FRIEND_EMAIL)){
            friendSearch.setError(getString(R.string.error_field_required));
            focusView = friendSearch;
            cancel = true;
        }

        if(cancel)
            focusView.requestFocus();
        else {
            new AsyncTask<Void, Void, Boolean>(){
                String message = getString(R.string.error_network);

                @Override
                protected Boolean doInBackground(Void... params){
                    JSONObject result = new JSONObject();
                    try {
                        result.put("email", FRIEND_EMAIL);
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
                        if(result.getBoolean("success")){
                            try {
                                User.getInstance().cacheFriends(getApplicationContext());
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
                        e.printStackTrace();
                        return false;
                    }
                }
                @Override
                protected void onPostExecute(final Boolean success){
                    if(success){
                        friendSearch.setText("");
                        Toast.makeText(FriendsActivity.this, FRIEND_EMAIL + "added successfully", Toast.LENGTH_SHORT).show();
                        displayFriends();
                    }
                    else {
                        friendSearch.setError(message);
                        friendSearch.requestFocus();
                    }
                }
            }.execute(null, null, null);
        }
    }

    protected void deleteFriend(final int friendId){
        final String URI = getString(R.string.conn) + getString(R.string.friends_destroy);

        new AsyncTask<Void, Void, Boolean>(){
            String message = getString(R.string.error_network);

            @Override
            protected Boolean doInBackground(Void... params){
                JSONObject result = new JSONObject();
                try {
                    result.put("id", friendId);
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
                    if(result.getBoolean("success")){
                        try {
                            User.getInstance().cacheFriends(getApplicationContext());
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
                    e.printStackTrace();
                    return false;
                }
            }
            @Override
            protected void onPostExecute(final Boolean success){
                if(success){
                    displayFriends();
                }
                else {
                    Toast.makeText(FriendsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(null, null, null);
    }

    protected void displayFriends(){

        final ListView FRIENDVIEW = (ListView) findViewById(R.id.friendListView);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                try {
                    friendMap = User.getInstance().getFriends(getApplicationContext());
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FriendsActivity.this,
                        android.R.layout.simple_list_item_1, friendNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                FRIENDVIEW.setAdapter(dataAdapter);

                registerForContextMenu(FRIENDVIEW);
                FRIENDVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //TODO change this to show friend info
                    }
                });
            }
        }.execute(null, null, null);
    }
}