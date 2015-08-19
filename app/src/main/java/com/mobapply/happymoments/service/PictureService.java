package com.mobapply.happymoments.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.activity.FullscreenPictureActivity;
import com.mobapply.happymoments.provider.PictureProvider;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class PictureService extends Service {


    public final static String TAG = "PictureService";
    private Handler mHandler;
    //private Timer mTimer;
    private Cursor mCursor;
    private Random mRandom;
    private SharedPreferences mPref;
    private boolean mDataValid;
    private int period;
    private boolean shuffle;
    private boolean started = false;
    private final  ChangeObserver mChangeObserver = new ChangeObserver();


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
//        if(mTimer != null) {
//            mTimer.cancel();
//        }
        if(mHandler!= null) {
            mHandler.removeCallbacks(mPictureRunnable);
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
        mRandom = new Random();
        mHandler = new Handler();
        //mTimer = new Timer();
        mPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mCursor= getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null, PictureProvider.PICTURE_IS_PLAY + " = " + PictureProvider.PLAY, null, null);
        if (mChangeObserver != null) mCursor.registerContentObserver(mChangeObserver);
        mDataValid = true;

    }

    private void loadSettings(){
        period = mPref.getInt(Constants.PERIOD_UPDATING, Constants.DEFAULT_PERIOD_UPDATING);
        shuffle = mPref.getBoolean(Constants.SHUFFLE, false);
    }

    private void process(){
        //mTimer.schedule(mTimerTask, period*60*1000 , period*60*1000);
        //TODO
        mHandler.postDelayed(mPictureRunnable, period*60*100);
    }

    private void showFullscreenPicture(String fileName){
        Intent intent = new Intent(this, FullscreenPictureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.EXTRA_FILE_NAME, fileName);
        startActivity(intent);
    }



    private Runnable mPictureRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "service run task");

            loadSettings();

            if (!mDataValid){
                onContentChanged();
            }

            if (mCursor == null || mCursor.getCount() == 0||!mDataValid ){
                //TODO
                mHandler.postDelayed(mPictureRunnable, period*60*100);
                return;
            }

            if (shuffle){
                int position = mRandom.nextInt(mCursor.getCount());
                mCursor.moveToPosition(position);
            }else {
                if (!mCursor.moveToNext()) {
                    if (!mCursor.moveToFirst()) {
                        mHandler.postDelayed(mPictureRunnable, period*60*100);
                        return;
                    }
                };
            }

            final String fileName = mCursor.getString(mCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
            showFullscreenPicture(fileName);

            //TODO
            mHandler.postDelayed(mPictureRunnable, period*60*100);
        }
    };

    protected void onContentChanged() {
        if ( mCursor != null && !mCursor.isClosed()) {
            mDataValid = mCursor.requery();
        }
    }

    private class ChangeObserver extends ContentObserver {
        public ChangeObserver() {
            super(new Handler());
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    }


}
