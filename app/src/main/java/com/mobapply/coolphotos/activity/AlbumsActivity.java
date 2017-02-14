package com.mobapply.coolphotos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.adapter.AlbumViewBinder;
import com.mobapply.coolphotos.dialog.CreateAlbumDialog;
import com.mobapply.coolphotos.menu.NavigationDrawerFragment;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.mobapply.coolphotos.service.PictureService;

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
    private SwitchCompat mSwitchMode;
    private boolean modeConscious;
    private boolean firstStart;
    private SharedPreferences sPref;

    public static AlbumsActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        instance = this;

        if (getIntent().getExtras()!= null && getIntent().getBooleanExtra(Constants.EXTRA_STOP_SERVICE, false)){
            stoptService();
            finish();
            return;
        }

        startService();

        setupNavigationDrawer();

        initViews();

        loadSettings();

        showTutorial();

        fillData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constants.EXTRA_STOP_SERVICE, false)){
            stoptService();
            finish();
            return;
        }
    }

    private void adjustHomeButtonLayout() {
        ImageView view = (ImageView) findViewById(android.R.id.home);
        if (view.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            View upView = viewGroup.getChildAt(0);
            if (upView != null && upView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) upView.getLayoutParams();
                layoutParams.width = 20;// **can give your own width**
                upView.setLayoutParams(layoutParams);
            }
        }
    }

    private void loadSettings() {
        modeConscious = sPref.getBoolean(Constants.MODE_CONSCIOUS, Constants.DEFAULT_MODE_CONSCIOUS);
        firstStart = sPref.getBoolean(Constants.FIRST_START, true);
    }

    private void setupNavigationDrawer() {
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
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

    private void showTutorial() {
        if (firstStart) {
            startActivityForResult(new Intent(this, TutorialActivity.class), TUTORIAL_REQUEST_CODE);
            firstStart = false;
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean(Constants.FIRST_START, firstStart);
            modeConscious = firstStart;
            ed.commit();
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
                stoptService();
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
        MenuItem menuItem = menu.findItem(R.id.action_mode);
        mSwitchMode = (SwitchCompat) MenuItemCompat.getActionView(menuItem).findViewById(R.id.actionbar_switch);
        mSwitchMode.setChecked(modeConscious);
        mSwitchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean(Constants.MODE_CONSCIOUS, isChecked);
                modeConscious = isChecked;
                ed.commit();

                Toast toast = Toast.makeText(AlbumsActivity.this, modeConscious ? R.string.conscious : R.string.subconscious, Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundResource(R.drawable.toast_custom);
                toast.setGravity(Gravity.RIGHT | Gravity.TOP, 50, 5 * getStatusBarHeight() / 2);
                TextView text = (TextView) view.findViewById(android.R.id.message);
                text.setTextSize(14);
                text.setTextColor(Color.WHITE);
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
        MenuItem actionMode = menu.findItem(R.id.action_mode);
        actionMode.setVisible(!mNavigationDrawerFragment.isDrawerOpen());
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

    private void startService() {
        startService(new Intent(this, PictureService.class));
    }

    private void stoptService() {
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