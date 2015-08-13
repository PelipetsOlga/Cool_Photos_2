package com.mobapply.happymoments.activity;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.SimpleCursorAdapter;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;
import com.mobapply.happymoments.adapter.AlbumViewBinder;
import com.mobapply.happymoments.adapter.PicturesViewBinder;
import com.mobapply.happymoments.dialog.CreateAlbumDialog;
import com.mobapply.happymoments.menu.NavigationDrawerFragment;
import com.mobapply.happymoments.provider.PictureProvider;

public class AlbumsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private FloatingActionButton fab;
    private GridView grid;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        setupNavigationDrawer();
        initViews();
        fillData();
    }

    private void setupNavigationDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getString(R.string.action_home);
        restoreActionBar();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void initViews() {
        grid = (GridView) findViewById(R.id.gridAlbums);
        fab = (FloatingActionButton) findViewById(R.id.fab_albums);
        fab.setOnClickListener(this);
    }

    private void fillData() {
        //albums container
        Cursor cursor = getContentResolver().query(PictureProvider.ALBUM_CONTENT_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.ALBUM_FILE,
                PictureProvider.ALBUM_NAME,
                PictureProvider.ALBUM_COUNT,
                PictureProvider.ALBUM_IS_PLAY};
        int to[] = {R.id.picture, R.id.tv_album_title,
                R.id.tv_album_count, R.id.ic_is_playing};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_album, cursor, from, to);

        adapter.setViewBinder(new AlbumViewBinder(this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PicturesActivity.class);
                intent.putExtra(Constants.EXTRA_ID, id);
                startActivity(intent);
            }
        });
        grid.setAdapter(adapter);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle(mTitle);
                break;
            case 1:
                Intent settingIntent = new Intent();
                settingIntent.setClass(this, SettingsActivity.class);
                startActivity(settingIntent);
                break;
            case 2:
                finish();
                // TODO stop Servise
                break;
        }
    }


    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_albums:
                CreateAlbumDialog dialog = new CreateAlbumDialog();
                dialog.show(getFragmentManager(), null);
                break;
        }
    }
}
