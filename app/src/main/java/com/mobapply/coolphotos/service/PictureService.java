package com.mobapply.coolphotos.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.activity.AlbumsActivity;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Random;


public class PictureService extends Service {


    public final static String TAG = "PictureService";
    private Handler mHandler;
    private Handler mHandler2;
    private LayoutInflater mInflater;
    private int statusBarHeight;
    private Cursor mCursor;
    private Random mRandom;
    private SharedPreferences mPref;
    private boolean mDataValid;
    private int period;
    private int periodMinutes;
    private boolean shuffle;
    private boolean modeConscious;
    private long showTime;
    private boolean started = false;
    private final ChangeObserver mChangeObserver = new ChangeObserver();
    private NotificationManager nm;
    private boolean firstShown = false;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "service started");
        init();
        loadSettings();
        sendNotif();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service start command");
        if (!started) {
            process();
            started = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Log.d(TAG, "service stoped");
        if (mHandler != null) {
            mHandler.removeCallbacks(mPictureRunnable);
        }
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        cancelNotif();
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init() {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mRandom = new Random();
        mHandler = new Handler();
        mHandler2 = new Handler();
        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        statusBarHeight = getStatusBarHeight();
        mPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        mCursor = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null, PictureProvider.PICTURE_IS_PLAY + " = " + PictureProvider.PLAY, null, null);
        if (mChangeObserver != null) mCursor.registerContentObserver(mChangeObserver);
        mDataValid = true;

    }

    private void loadSettings() {
        period = mPref.getInt(Constants.PERIOD_UPDATING, Constants.DEFAULT_PERIOD_UPDATING);
        shuffle = mPref.getBoolean(Constants.SHUFFLE, Constants.DEFAULT_SHUFFLE);
        modeConscious = mPref.getBoolean(Constants.MODE_CONSCIOUS, Constants.DEFAULT_MODE_CONSCIOUS);
        showTime = modeConscious ? Constants.SHOW_TIME_CONSCIOUS :  Constants.SHOW_TIME_SUBCONSCIOUS;
        periodMinutes = period * 60 * 1000;
    }

    private void process() {
        mHandler.postDelayed(mPictureRunnable, periodMinutes);
    }

    private void showFullscreenPicture(String fileName) {
        final Toast toast = new Toast(getApplicationContext());
        final View view = mInflater.inflate(R.layout.toast_fullscreen_picture, null);
        final ImageView fullPicture = (ImageView)view.findViewById(R.id.full_picture);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        try {
            Picasso.with(this).load(new File(fileName)).into(fullPicture, new Callback() {
                @Override
                public void onSuccess() {
                    toast.show();

                    mHandler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, showTime);
                }

                @Override
                public void onError() {
                }
            });
        }catch(Throwable t){
            t.printStackTrace();
        }
    }

    void sendNotif() {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launch_status_24dp)
                        .setOngoing(true)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.running));

        Intent resultIntent = new Intent(this, AlbumsActivity.class);
        resultIntent.putExtra(Constants.EXTRA_STOP_SERVICE, true);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);


        nm.notify(1, mBuilder.build());
    }

    void cancelNotif() {
        nm.cancel(1);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    private Runnable mPictureRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "service run task");

            loadSettings();

            if (!mDataValid) {
                onContentChanged();
            }

            if (mCursor == null || mCursor.getCount() == 0 || !mDataValid) {
                mHandler.postDelayed(mPictureRunnable, periodMinutes);
                return;
            }

            if (shuffle) {
                int position = mRandom.nextInt(mCursor.getCount());
                mCursor.moveToPosition(position);
            } else {
                if (!mCursor.moveToNext()) {
                    if (!mCursor.moveToFirst()) {
                        mHandler.postDelayed(mPictureRunnable, periodMinutes);
                        return;
                    }
                }
            }

            final String fileName = mCursor.getString(mCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
            showFullscreenPicture(fileName);
            mHandler.postDelayed(mPictureRunnable, periodMinutes);
        }
    };

    protected void onContentChanged() {
        if (mCursor != null && !mCursor.isClosed()) {
            mDataValid = mCursor.requery();
            if(mDataValid && !firstShown && mCursor.moveToFirst() &&  mCursor.getCount() == 1){
                firstShown = true;
                final String fileName = mCursor.getString(mCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
                mHandler.postDelayed(new Runnable() {
                                         @Override
                                         public void run() {
                                             showFullscreenPicture(fileName);

                                         }
                                     },2000);
            }
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
