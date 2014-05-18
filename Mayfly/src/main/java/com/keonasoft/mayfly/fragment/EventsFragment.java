package com.keonasoft.mayfly.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.keonasoft.mayfly.MyException;
import com.keonasoft.mayfly.activity.AppActivity;
import com.keonasoft.mayfly.R;
import com.keonasoft.mayfly.activity.EventActivity;
import com.keonasoft.mayfly.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * A fragment for "Notifications" page.
 */
public class EventsFragment extends android.app.Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    protected static final String ARG_SECTION_NUMBER = "section_number";

    protected View rootView;
    protected Map<Integer, String> eventMap;
    protected Context rootContext;
    protected final boolean ATTENDING = false;
    protected final boolean CREATOR = false;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EventsFragment newInstance(int sectionNumber) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        rootContext = rootView.getContext();
        displayEvents(ATTENDING, CREATOR);

        return rootView;
    }

    public void displayEvents(final boolean ATTENDING, final boolean CREATOR){

        final ListView LISTVIEW = (ListView) rootView.findViewById(R.id.section_List);

        new AsyncTask<Void, Void, Boolean>(){
            protected Boolean doInBackground(Void... params) {
                try {
                    eventMap = User.getInstance().getEvents(rootContext, ATTENDING, CREATOR);
                } catch (MyException e) {
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                List<String> eventNames = new ArrayList<String>();
                List<Integer> eventIds = new ArrayList<Integer>();

                for (Integer key: eventMap.keySet()){
                    eventIds.add(key);
                    eventNames.add(eventMap.get(key));
                }
                final List<Integer> EVENT_IDS = eventIds;

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, eventNames);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                LISTVIEW.setAdapter(dataAdapter);

                LISTVIEW.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int eventId = EVENT_IDS.get(position);
                        Intent intent = new Intent(getActivity(), EventActivity.class);
                        intent.putExtra("eventId", eventId);
                        startActivity(intent);
                    }
                });
            }
        }.execute(null, null, null);
    }
}
