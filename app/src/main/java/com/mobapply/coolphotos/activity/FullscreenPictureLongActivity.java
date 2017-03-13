package com.mobapply.coolphotos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullscreenPictureLongActivity extends AppCompatActivity {
    private ImageView fullPicture;
    private String picturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreActionBar();
        setContentView(R.layout.activity_fullscreen_picture);
        parseIntent();

        fullPicture = (ImageView) findViewById(R.id.full_picture);
        try {
            Picasso.with(this).load(new File(picturePath)).into(fullPicture);
        } catch (Throwable t) {
            t.printStackTrace();
            finish();
        }
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
