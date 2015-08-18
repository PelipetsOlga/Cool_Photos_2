package com.mobapply.happymoments.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.activity.FullscreenPictureActivity;
import com.mobapply.happymoments.provider.PictureProvider;

import java.util.Timer;
import java.util.TimerTask;


public class PictureService extends Service {


    public final static String TAG = "PictureService";
    private Handler mHadler;
    private Timer mTimer;
    private PictureTimerTask mTimerTask;
    private Cursor mCursor;
    private SharedPreferences mPref;
    private int period;
    private boolean shuffle;
    private boolean started = false;


    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service started");
        init();
        loadSettings();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service start command");
        if(!started){
            process();
            started = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Log.d(TAG, "service stoped");
        if(mTimer != null) {
            mTimer.cancel();
        }
        if(mCursor!= null && !mCursor.isClosed()) {
            mCursor.close();
        }
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init(){
        mHadler = new Handler();
        mTimer = new Timer();
        mTimerTask = new PictureTimerTask();
        mPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mCursor= getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null, PictureProvider.PICTURE_IS_PLAY + " = " +  PictureProvider.PLAY, null, null);
    }

    private void loadSettings(){
        period = mPref.getInt(Constants.PERIOD_UPDATING, Constants.DEFAULT_PERIOD_UPDATING);
        shuffle = mPref.getBoolean(Constants.SHUFFLE, false);
    }

    private void process(){
        mTimer.schedule(mTimerTask, period*60*1000 , period*60*1000);
    }

    private void showFullscreenPicture(String fileName){
        Intent intent = new Intent(this, FullscreenPictureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_FILE_NAME, fileName);
        startActivity(intent);
    }

    class PictureTimerTask extends TimerTask {

        @Override
        public void run() {
            Log.d(TAG, "service run task");
            if (mCursor == null || mCursor.getCount() == 0){
                return;
            }

            if (!mCursor.moveToNext()){
                if (!mCursor.moveToFirst()){
                  return;
                }
            };

            final String fileName = mCursor.getString(mCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
            showFullscreenPicture(fileName);
        }
    }


}
