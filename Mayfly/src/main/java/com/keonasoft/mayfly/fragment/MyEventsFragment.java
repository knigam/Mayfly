package com.keonasoft.mayfly.fragment;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keonasoft.mayfly.R;

/**
 * A fragment for "My Events" page.
 */
public class MyEventsFragment extends EventsFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    protected final boolean CREATOR = true;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyEventsFragment newInstance(int sectionNumber) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lists, container, false);
        rootContext = rootView.getContext();
        displayEvents(ATTENDING, CREATOR);

        return rootView;
    }
}