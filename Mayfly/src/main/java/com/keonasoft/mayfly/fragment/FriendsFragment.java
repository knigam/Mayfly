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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import com.keonasoft.mayfly.activity.AppActivity;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.activity.EventActivity;
import com.keonasoft.mayfly.model.User;

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
    private View mFriendView;
    private View mGroupView;
    private View rootView;
    private Map<String, Integer> friendMap;
    private Context rootContext;

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
        mGroupView = container.findViewById(R.id.groupListView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((AppActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void displayFriends(){

        final ListView FRIENDVIEW = (ListView) rootView.findViewById(R.id.friendListView);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                friendMap = User.getInstance().getFriends(rootContext);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                List<String> friendNames = new ArrayList<String>();
                for (String key: friendMap.keySet()){
                    friendNames.add(key);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, friendNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                FRIENDVIEW.setAdapter(dataAdapter);

                FRIENDVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), EventActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }.execute(null, null, null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final View view) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            view.setVisibility(View.VISIBLE);
            view.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            view.setVisibility(View.VISIBLE);
            view.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            view.setVisibility(show ? View.VISIBLE : View.GONE);
            view.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}