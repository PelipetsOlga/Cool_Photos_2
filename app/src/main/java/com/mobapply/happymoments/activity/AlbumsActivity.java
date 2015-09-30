package com.mobapply.happymoments.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;
import com.mobapply.happymoments.adapter.AlbumViewBinder;
import com.mobapply.happymoments.dialog.CreateAlbumDialog;
import com.mobapply.happymoments.menu.NavigationDrawerFragment;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.service.PictureService;

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
    private FloatingActionButton mFab;
    private GridView mGrid;
    private ActionBar actionBar;
    private Cursor cursor;
    private SwitchCompat mSwitchMode;
    private boolean modeConscious;
    private boolean firstStart;
    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        startService();

        setupNavigationDrawer();

        initViews();

        loadSettings();

        showTutorial();

        fillData();
    }

    private void loadSettings(){
        modeConscious = sPref.getBoolean(Constants.MODE_CONSCIOUS, Constants.DEFAULT_MODE_CONSCIOUS);
        firstStart = sPref.getBoolean(Constants.FIRST_START, true);
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
        mGrid = (GridView) findViewById(R.id.gridAlbums);
        mFab = (FloatingActionButton) findViewById(R.id.fab_albums);
        mFab.setOnClickListener(this);
        mSwitchMode = (SwitchCompat) findViewById(R.id.switch_mode);
        sPref = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
    }

    private void fillData() {
        //albums container
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

    private void showTutorial(){
        if(firstStart) {
            startActivity(new Intent(this, TutorialActivity.class));
            firstStart = false;
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(Constants.FIRST_START, firstStart);
            modeConscious = firstStart;
            ed.commit();
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
                stoptService();
                finish();
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
        MenuItem menuItem =menu.findItem(R.id.action_mode);
        mSwitchMode = (SwitchCompat)MenuItemCompat.getActionView(menuItem).findViewById(R.id.actionbar_switch);
        mSwitchMode.setChecked(modeConscious);
        mSwitchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(Constants.MODE_CONSCIOUS, isChecked);
                modeConscious = isChecked;
                ed.commit();

                Toast toast = Toast.makeText(AlbumsActivity.this, modeConscious ? R.string.conscious : R.string.subconscious, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.RIGHT | Gravity.TOP, 50, 2*getStatusBarHeight());
                toast.show();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem selectAlbum = menu.findItem(R.id.action_select_albums);
        selectAlbum.setVisible(!mNavigationDrawerFragment.isDrawerOpen());
        loadSettings();
        mSwitchMode.setChecked(modeConscious);
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
            case R.id.fab_albums:
                CreateAlbumDialog dialog = new CreateAlbumDialog();
                dialog.show(getFragmentManager(), null);
                break;
        }
    }

    private void startService(){
        startService(new Intent(this, PictureService.class));
    }

    private void stoptService(){
        stopService(new Intent(this, PictureService.class));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
