package com.mobapply.coolphotos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullscreenPictureActivity extends Activity {
    private Handler handler;
    private ImageView fullPicture;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(null);

        hide();

        setContentView(R.layout.activity_fullscreen_picture);

        parseIntent();

        handler = new Handler();

        fullPicture = (ImageView) findViewById(R.id.full_picture);
        try {
             Picasso.with(this).load(new File(picturePath)).into(fullPicture, new Callback() {
                 @Override
                 public void onSuccess() {
                     handler.postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             FullscreenPictureActivity.this.finish();
                         }
                     }, Constants.SHOW_TIME);
                 }

                 @Override
                 public void onError() {
                     FullscreenPictureActivity.this.finish();
                 }
             });
        }catch(Throwable t){
            t.printStackTrace();
            finish();
        }
    }

    private void parseIntent() {
        Intent intent = getIntent();
        picturePath = intent.getStringExtra(Constants.EXTRA_FILE_NAME);
    }

    @Override
    protected void onDestroy() {
        fullPicture.setImageBitmap(null);
        super.onDestroy();
    }

    private void hideBars() {

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void hide(){
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION// hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
