package com.mobapply.happymoments.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.fragment.TutorialFirstFragment;

/**
 * Created by apelipets on 10/5/15.
 */
public class TutorialActivity  extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        replaceFragment(new TutorialFirstFragment());

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void returnResult(final int result){
        final Intent intent = new Intent();
        //setResult(result, intent);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                AlbumsActivity.getInstance().onActivityResult(AlbumsActivity.TUTORIAL_REQUEST_CODE, result, intent);

            }
        }, 100);
        finish();

    }
}