package com.mobapply.coolphotos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.fragment.TutorialFirstFragment;

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
        AlbumsActivity.getInstance().onActivityResult(AlbumsActivity.TUTORIAL_REQUEST_CODE, result, intent);
        finish();

    }
}