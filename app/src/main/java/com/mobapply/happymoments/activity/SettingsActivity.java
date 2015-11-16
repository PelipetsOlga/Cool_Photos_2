package com.mobapply.happymoments.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.widget.CompoundButton;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class SettingsActivity extends AppCompatActivity {


    private SharedPreferences sPref;
    private SeekBar mSeekBar;
    private TextView mDelay;
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
        mDelay = (TextView)findViewById(R.id.tv_delay);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSwitchShuffle = (SwitchCompat) findViewById(R.id.switch_shuffle);
        mSwitchMode = (SwitchCompat) findViewById(R.id.switch_mode);
    }


    private void init(){
        mSeekBar.setProgress(period-1);
        mDelay.setText(period + " " + getString(R.string.tv_min));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                period = progress+1;
                mDelay.setText(period + " " + getString(R.string.tv_min));
                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt(Constants.PERIOD_UPDATING, period);
                ed.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
