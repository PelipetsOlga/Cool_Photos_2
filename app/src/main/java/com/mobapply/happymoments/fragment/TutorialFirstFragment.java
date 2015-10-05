package com.mobapply.happymoments.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.activity.TutorialActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFirstFragment extends Fragment {


    public TutorialFirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_first, container, false);
        view.findViewById(R.id.button_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialActivity)getActivity()).replaceFragment(new TutorialSecondFragment());
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem =menu.findItem(R.id.action_mode);
        SwitchCompat switchMode = (SwitchCompat) MenuItemCompat.getActionView(menuItem).findViewById(R.id.actionbar_switch);
        switchMode.setEnabled(false);
        menuItem =menu.findItem(R.id.action_select_albums);
        menuItem.setEnabled(false);
        super.onCreateOptionsMenu(menu, inflater);
    }



}
