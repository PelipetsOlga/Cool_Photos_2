package com.mobapply.coolphotos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.adapter.AlbumViewBinder;
import com.mobapply.coolphotos.dialog.CreateAlbumDialog;
import com.mobapply.coolphotos.menu.NavigationDrawerFragment;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.mobapply.coolphotos.service.PictureService;
import com.mobapply.coolphotos.utils.CoolPhotosUtils;

public class AlbumsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    public static final int TUTORIAL_REQUEST_CODE = 1;

    private static AlbumsActivity instance;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    private FloatingActionButton mFab;
    private GridView mGrid;
    private ActionBar actionBar;
    private Cursor cursor;
    private SharedPreferences sPref;
    private RelativeLayout empty;
    private ImageView eyescreamImageView;

    public static AlbumsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        instance = this;

//        if (getIntent().getExtras() != null && getIntent().getBooleanExtra(Constants.EXTRA_STOP_SERVICE, false)) {
//            stopService();
//            finish();
//            return;
//        }

        startService();
        setupNavigationDrawer();
        initViews();
        loadSettings();
        fillData();
        updateEmptyView();
        CoolPhotosUtils.addAdView(this);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (intent.getBooleanExtra(Constants.EXTRA_STOP_SERVICE, false)) {
//            stopService();
//            finish();
//            return;
//        }
//    }

    private void loadSettings() {
    }

    private void setupNavigationDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getResources().getString(R.string.action_home);
        restoreActionBar();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void initViews() {
        mGrid = (GridView) findViewById(R.id.gridAlbums);
        mFab = (FloatingActionButton) findViewById(R.id.fab_albums);
        mFab.setOnClickListener(this);
        sPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        empty = (RelativeLayout) findViewById(R.id.empty);
        eyescreamImageView = (ImageView) findViewById(R.id.eyescream_image);
        eyescreamImageView.setOnClickListener(this);
    }

    private void fillData() {
        cursor = getContentResolver().query(PictureProvider.ALBUM_CONTENT_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.ALBUM_FILE_PREVIEW,
                PictureProvider.ALBUM_NAME,
                PictureProvider.ALBUM_COUNT,
                PictureProvider.ALBUM_IS_PLAY};
        int to[] = {R.id.picture, R.id.tv_album_title,
                R.id.tv_album_count, R.id.ic_is_playing};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_album, cursor, from, to);

        adapter.setViewBinder(new AlbumViewBinder(this));
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor) adapter.getItem(position);
                int count = item.getInt(item.getColumnIndex(PictureProvider.ALBUM_COUNT));
                String title = item.getString(item.getColumnIndex(PictureProvider.ALBUM_NAME));
                Intent intent = new Intent(getApplicationContext(), PicturesActivity.class);
                intent.putExtra(Constants.EXTRA_ID, id);
                intent.putExtra(Constants.EXTRA_COUNT, count);
                intent.putExtra(Constants.EXTRA_TITLE, title);
                startActivity(intent);
            }
        });
        mGrid.setAdapter(adapter);
    }

    private void updateEmptyView() {
        if (mGrid.getAdapter().getCount() == 0) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TUTORIAL_REQUEST_CODE:
                    CreateAlbumDialog dialog = new CreateAlbumDialog();
                    dialog.show(getFragmentManager(), null);
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                getSupportActionBar().setTitle(mTitle);
                break;
            case 1:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case 2:
                Intent intentEstimate = new Intent(Intent.ACTION_VIEW);
                intentEstimate.setData(Uri
                        .parse("market://details?id=com.mobapply.coolphotos"));
                startActivity(intentEstimate);
                break;
            case 3:
                stopService();
                finish();
                break;
        }
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEmptyView();
        invalidateOptionsMenu();
        actionBar.setTitle(mTitle);
    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem selectAlbum = menu.findItem(R.id.action_select_albums);
        if (mGrid.getAdapter().getCount() == 0) {
            selectAlbum.setVisible(false);
        } else {
            selectAlbum.setVisible(!mNavigationDrawerFragment.isDrawerOpen());
        }
        loadSettings();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_albums:
                Intent intentSelect = new Intent(this, SelectAlbumsActivity.class);
                startActivity(intentSelect);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eyescream_image:
            case R.id.fab_albums:
                CreateAlbumDialog dialog = new CreateAlbumDialog();
                dialog.show(getFragmentManager(), null);
                break;
        }
    }

    private void startService() {
        startService(new Intent(this, PictureService.class));
    }

    private void stopService() {
        stopService(new Intent(this, PictureService.class));
    }

}
