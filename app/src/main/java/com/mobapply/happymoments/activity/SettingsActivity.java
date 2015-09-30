package com.mobapply.happymoments.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import android.widget.TextView;


import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class SettingsActivity extends AppCompatActivity {


    private SharedPreferences sPref;
    private DiscreteSeekBar mSeekBar;
    private SwitchCompat mSwitchShuffle;
    private SwitchCompat mSwitchMode;
    private int period;
    private boolean shuffle;
    private boolean modeConscious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupActionBar();

        initViews();

        loadSettings();

        init();
    }

    private void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.action_settings));
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    private void loadSettings(){
        sPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        period = sPref.getInt(Constants.PERIOD_UPDATING, Constants.DEFAULT_PERIOD_UPDATING);
        shuffle = sPref.getBoolean(Constants.SHUFFLE, Constants.DEFAULT_SHUFFLE);
        modeConscious = sPref.getBoolean(Constants.MODE_CONSCIOUS, Constants.DEFAULT_MODE_CONSCIOUS);
    }

    private void initViews(){
        mSeekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        mSwitchShuffle = (SwitchCompat) findViewById(R.id.switch_shuffle);
        mSwitchMode = (SwitchCompat) findViewById(R.id.switch_mode);
    }

    private void init(){
        mSeekBar.setProgress(period);
        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt(Constants.PERIOD_UPDATING, i);
                period = i;
                ed.commit();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {

            }
        });

        mSwitchShuffle.setChecked(shuffle);
        mSwitchShuffle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(Constants.SHUFFLE, isChecked);
                shuffle = isChecked;
                ed.commit();
            }
        });

        mSwitchMode.setChecked(modeConscious);
        mSwitchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(Constants.MODE_CONSCIOUS, isChecked);
                modeConscious = isChecked;
                ed.commit();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // TODO: add notify service
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
