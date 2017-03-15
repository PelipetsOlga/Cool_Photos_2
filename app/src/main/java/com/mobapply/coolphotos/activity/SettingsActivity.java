package com.mobapply.coolphotos.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;

import java.util.Formatter;
import java.util.Locale;


public class SettingsActivity extends AppCompatActivity {


    private SharedPreferences sPref;
    private SeekBar seekPeriod;
    private SeekBar seekDuration;
    private TextView mDelay;
    private TextView mDuration;
    private SwitchCompat mSwitchShuffle;
    private SwitchCompat mSwitchMode;
    private LinearLayout llDuration;
    private int period;
    private long duration;
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

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle(getResources().getString(R.string.action_settings));
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

    }

    private void loadSettings() {
        sPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        period = sPref.getInt(Constants.PERIOD_UPDATING, Constants.DEFAULT_PERIOD_UPDATING);
        shuffle = sPref.getBoolean(Constants.SHUFFLE, Constants.DEFAULT_SHUFFLE);
        modeConscious = sPref.getBoolean(Constants.MODE_CONSCIOUS, Constants.DEFAULT_MODE_CONSCIOUS);
        duration = sPref.getLong(Constants.DURATION, Constants.SHOW_TIME_CONSCIOUS);
    }

    private void initViews() {
        mDelay = (TextView) findViewById(R.id.tv_delay);
        mDuration = (TextView) findViewById(R.id.tv_duration);
        seekPeriod = (SeekBar) findViewById(R.id.seekbar);
        seekDuration = (SeekBar) findViewById(R.id.sb_duration);
        mSwitchShuffle = (SwitchCompat) findViewById(R.id.switch_shuffle);
        mSwitchMode = (SwitchCompat) findViewById(R.id.switch_mode);
        llDuration = (LinearLayout) findViewById(R.id.ll_duration);
    }

    private void setDurationSeekBar(long value) {
        seekDuration.setProgress((int) value);
        mDuration.setText(String.format("%.1f sec", (double) duration / 1000));
    }

    private void init() {
        setDurationSeekBar(duration);
        seekDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < Constants.SHOW_TIME_CONSCIOUS_MIN) {
                    duration = Constants.SHOW_TIME_CONSCIOUS_MIN;
                } else {
                    duration = progress;
                }
                setDurationSeekBar(duration);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putLong(Constants.DURATION, duration);
                ed.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekPeriod.setProgress(period - 1);
        mDelay.setText(period + " " + (period > 1 ? getString(R.string.minutes) : getString(R.string.minute)));
        seekPeriod.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                period = progress + 1;
                mDelay.setText(period + " " + (period > 1 ? getString(R.string.minutes) : getString(R.string.minute)));
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

                if (isChecked) {
                    animateIn(llDuration);
                } else {
                    animateOut(llDuration);
                }
            }
        });
    }

    private void animateOut(final View view) {
        view.animate().alpha(0.0f)
                .setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
    }

    private void animateIn(final View view) {
        view.animate().alpha(1.0f)
                .setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
