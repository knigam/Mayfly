package com.keonasoft.mayfly.fragment;



import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.activity.AppActivity;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.activity.EventActivity;
import com.keonasoft.mayfly.helper.HttpHelper;
import com.keonasoft.mayfly.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment for "Friends" page.
 */
public class FriendsFragment extends android.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private Map<Integer, String> friendMap;
    private Context rootContext;

    //Layout Views
    private TextView friendSearch;
    private Button friendSearchBtn;
    private Button friendAddBtn;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendsFragment newInstance(int sectionNumber) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_friends, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Friends!");*/
        rootContext = rootView.getContext();
        TabHost tabHost = (TabHost) rootView.findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Friends");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Friends");
        tabHost.addTab(tabSpec);
        displayFriends();

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec("Groups");
        tabSpec2.setContent(R.id.tab2);
        tabSpec2.setIndicator("Groups");
        tabHost.addTab(tabSpec2);

        friendSearch = (TextView) rootView.findViewById(R.id.friendSearchTextView);
        friendSearchBtn = (Button) rootView.findViewById(R.id.friendSearchButton);
        friendSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriends();
            }
        });
        friendAddBtn = (Button) rootView.findViewById(R.id.friendAddButton);
        friendAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AppActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
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
                                User.getInstance().cacheFriends(rootContext);
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
                        Toast.makeText(rootContext, FRIEND_EMAIL + "added successfully", Toast.LENGTH_SHORT).show();
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

    protected void displayFriends(){

        final ListView FRIENDVIEW = (ListView) rootView.findViewById(R.id.friendListView);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                try {
                    friendMap = User.getInstance().getFriends(rootContext);
                } catch (MyException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                List<String> friendNames = new ArrayList<String>();
                List<Integer> friendIds = new ArrayList<Integer>();

                for (Integer key: friendMap.keySet()){
                    friendNames.add(friendMap.get(key));
                    friendIds.add(key);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, friendNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                FRIENDVIEW.setAdapter(dataAdapter);

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