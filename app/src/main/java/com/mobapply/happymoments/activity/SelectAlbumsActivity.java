package com.mobapply.happymoments.activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;
import com.mobapply.happymoments.adapter.AlbumViewBinder;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

import java.util.HashSet;
import java.util.Set;

public class SelectAlbumsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private String titleActivity;
    private GridView mGrid;
    private Cursor cursor;
    private TextView tvTitle;
    private boolean showActions = false;
    private int counter = 0;
    private Set<Long> setAlbums = new HashSet<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_albums);
        restoreActionBar();

        mGrid = (GridView) findViewById(R.id.gridAlbums);

        fillData();

    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        titleActivity = getResources().getString(R.string.title_select_albums_activity);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_select_albums, null);
        tvTitle = (TextView) mCustomView.findViewById(R.id.title_text);
        tvTitle.setText(titleActivity);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void fillData() {
        //albums container
        cursor = getContentResolver().query(PictureProvider.ALBUM_CONTENT_URI, null, null,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.ALBUM_FILE,
                PictureProvider.ALBUM_NAME,
                PictureProvider.ALBUM_COUNT,
                PictureProvider.ALBUM_IS_PLAY,
                PictureProvider.ALBUM_ID};
        int to[] = {R.id.picture, R.id.tv_album_title,
                R.id.tv_album_count, R.id.ic_is_playing, R.id.selecting_album};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_album, cursor, from, to);
        AlbumViewBinder binder = new AlbumViewBinder(this);
        binder.setSetAlbums(setAlbums);
        adapter.setViewBinder(binder);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor) adapter.getItem(position);
                ImageView selecting = (ImageView) view.findViewById(R.id.selecting_album);
                int count = item.getInt(item.getColumnIndex(PictureProvider.ALBUM_COUNT));

                if (setAlbums.contains(id)) {
                    //remove from set and decrease counter, deselect item
                    setAlbums.remove(id);
                    counter = counter - count;
                    selecting.setVisibility(View.GONE);
                } else {
                    //add to set and increase counter, select item
                    counter = counter + count;
                    setAlbums.add(id);
                    selecting.setVisibility(View.VISIBLE);
                }
                if (setAlbums.size() == 0) {
                    tvTitle.setText(titleActivity);
                    showActions = false;
                } else {
                    tvTitle.setText("(" + counter + ")");
                    showActions = true;
                }
                invalidateOptionsMenu();
            }

        });
        mGrid.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_album, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.select_albums_menu_group, showActions);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.ab_play_albums:
                //TODO
                break;

            case R.id.ab_pause_albums:
                //TODO
                break;

            case R.id.ab_delete_albums:
                for (long id : setAlbums) {
                    Uri uriDeletedAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, id);
                    Cursor albumCursor = getContentResolver().query(uriDeletedAlbum, null, null, null, null);
                    albumCursor.moveToFirst();
                    String albumPath = albumCursor.getString(albumCursor.getColumnIndex(PictureProvider.ALBUM_FOLDER));
                    if (HappyMomentsUtils.deleteDirectory(albumPath)) {

                        getContentResolver().delete(uriDeletedAlbum, null, null);
                        Cursor cursorDeletedPictures = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                                PictureProvider.PICTURE_ALBUM_ID + "=" + id, null, null);
                        while (cursorDeletedPictures.moveToNext()) {
                            int idDeletedPicture = cursorDeletedPictures.getInt(cursorDeletedPictures.getColumnIndex(PictureProvider.PICTURE_ID));
                            Uri uriDeletedPicture = ContentUris.withAppendedId(PictureProvider.PICTURE_CONTENT_URI, idDeletedPicture);
                            getContentResolver().delete(uriDeletedPicture, null, null);
                        }
                    }

                }
                counter = 0;
                setAlbums.clear();
                showActions = false;
                invalidateOptionsMenu();
                tvTitle.setText(titleActivity);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
