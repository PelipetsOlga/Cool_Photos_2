package com.mobapply.happymoments.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.dialog.NewAlbumDialog;
import com.mobapply.happymoments.menu.NavigationDrawerFragment;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

public class AlbumsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {



    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getString(R.string.action_home);
        restoreActionBar();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        FloatingActionButton fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAlbumDialog dialog=new NewAlbumDialog();
                dialog.show(getFragmentManager(),null);
            }
        });

        //albums container
        Cursor cursor = getContentResolver().query(PictureProvider.ALBUM_CONTENT_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = { PictureProvider.ALBUM_NAME, PictureProvider.ALBUM_COUNT };
        int to[] = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, from, to);

        GridView grid = (GridView) findViewById(R.id.gridView);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(), PicturesActivity.class);
                intent.putExtra(HappyMomentsUtils.EXTRA_ID, id);
                startActivity(intent);
            }
        });
        grid.setAdapter(adapter);





    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_albums:
                //TODO
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
