package com.mobapply.happymoments.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.mobapply.happymoments.R;


public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sPref;
    public static final String PERIOD_UPDATING = "period";
    public static final String SHUFFLE = "shuffle";
    private TextView mLabel;
    private SeekBar mSeekBar;
    private SwitchCompat mSwitchShuffle;
    private int period;
    private boolean shuffle;

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
        sPref = getPreferences(MODE_PRIVATE);
        period = sPref.getInt(PERIOD_UPDATING, 0);
        shuffle = sPref.getBoolean(SHUFFLE, false);
    }

    private void initViews(){
        mLabel = (TextView) findViewById(R.id.label);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSwitchShuffle = (SwitchCompat) findViewById(R.id.switch_shuffle);
    }

    private void init(){
        mSeekBar.setProgressDrawable(getResources()
                .getDrawable(R.drawable.progress_bar));
        mSeekBar.setProgress(period);
        mLabel.setText(period + "");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mLabel.setText(progress+"");

                SharedPreferences.Editor ed = sPref.edit();
                ed.putInt(PERIOD_UPDATING, progress);
                period = progress;
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
                ed.putBoolean(SHUFFLE, isChecked);
                shuffle = isChecked;
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
