package com.mobapply.happymoments.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;

public class TutorialActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        findViewById(R.id.button_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem =menu.findItem(R.id.action_mode);
        SwitchCompat switchMode = (SwitchCompat)MenuItemCompat.getActionView(menuItem).findViewById(R.id.actionbar_switch);
        switchMode.setEnabled(false);
        menuItem =menu.findItem(R.id.action_select_albums);
        menuItem.setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }
}
