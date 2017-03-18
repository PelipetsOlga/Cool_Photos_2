package com.mobapply.coolphotos.activity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.adapter.HeaderImageView;
import com.mobapply.coolphotos.adapter.PicturesViewBinder;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.mobapply.coolphotos.utils.CoolPhotosUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectPicturesActivity extends AppCompatActivity implements View.OnClickListener {

    private long idAlbum;
    private int countPictures;
    private String titleAlbum;
    private GridView grid;
    private FrameLayout frameHeader;
    private HeaderImageView headerPicture;
    private TextView albumTitle, albumTvCount;
    private LinearLayout llIsPlaying;
    private ActionBar actionBar;
    private String titleActivity;
    private TextView tvTitle;
    private boolean showActions = false;
    private Set<Long> setPictures = new HashSet<Long>();
    private int albumCount;
    private int isPlaying;
    private Cursor cursor;
    private long idHeaderPicture = 0;
    private ImageView selectingHeader;
    private SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pictures);

        initViews();
        CoolPhotosUtils.addAdView(SelectPicturesActivity.this);
        parseIntent();
        restoreActionBar();
        fillData();
    }

    private void initViews() {
        grid = (GridView) findViewById(R.id.gridPictures);

        frameHeader = (FrameLayout) findViewById(R.id.header);
        selectingHeader = (ImageView) findViewById(R.id.selecting_header);
        headerPicture = (HeaderImageView) findViewById(R.id.header_picture);
        albumTitle = (TextView) findViewById(R.id.tv_album_title);
        albumTvCount = (TextView) findViewById(R.id.tv_album_count);
        llIsPlaying = (LinearLayout) findViewById(R.id.ll_is_playing);
    }

    private void parseIntent() {
        Intent intent = getIntent();
        idAlbum = intent.getLongExtra(Constants.EXTRA_ID, 0);
        Uri albumUri = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        Cursor c = getContentResolver().query(albumUri, null, null, null, null);
        c.moveToFirst();
        countPictures = c.getInt(c.getColumnIndex(PictureProvider.ALBUM_COUNT));
        titleAlbum = intent.getStringExtra(Constants.EXTRA_TITLE);
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        titleActivity = getResources().getString(R.string.title_select_pictures_activity);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_select, null);
        tvTitle = (TextView) mCustomView.findViewById(R.id.title_text);
        tvTitle.setText(titleActivity);
        mCustomView.findViewById(R.id.title_drop_down).setOnClickListener(this);
        actionBar.setCustomView(mCustomView);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    private void fillData() {
        //header
        refreshHeader();

        //pictures container
        cursor = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                PictureProvider.PICTURE_ALBUM_ID + "=" + idAlbum + " and "
                        + PictureProvider.PICTURE_IS_MAIN + "=" + PictureProvider.NOT_MAIN,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.PICTURE_FILE_PREVIEW, PictureProvider.PICTURE_ID};
        int to[] = {R.id.picture, R.id.selecting_picture};
        adapter = new SimpleCursorAdapter(this,
                R.layout.item_picture, cursor, from, to);

        PicturesViewBinder binder = new PicturesViewBinder(this);
        binder.setSetPictures(setPictures);
        adapter.setViewBinder(binder);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Cursor item = (Cursor) adapter.getItem(position);
                ImageView selecting = (ImageView) view.findViewById(R.id.selecting_picture);

                if (setPictures.contains(id)) {
                    //remove from set and decrease counter, deselect item
                    setPictures.remove(id);
                    selecting.setVisibility(View.GONE);
                } else {
                    //add to set and increase counter, select item
                    setPictures.add(id);
                    selecting.setVisibility(View.VISIBLE);
                }
                if (setPictures.size() == 0) {
                    tvTitle.setText(titleActivity);
                    showActions = false;
                } else {
                    tvTitle.setText("(" + setPictures.size() + ")");
                    showActions = true;
                }
                invalidateOptionsMenu();
            }

        });
        grid.setAdapter(adapter);
    }

    private void refreshHeader() {
        Cursor albumCursor = getContentResolver().query(PictureProvider.ALBUM_CONTENT_URI, null,
                PictureProvider.ALBUM_ID + "=" + idAlbum,
                null, null);
        albumCursor.moveToFirst();
        albumCount = albumCursor.getInt(albumCursor.getColumnIndex(PictureProvider.ALBUM_COUNT));
        if (albumCount == 0) {
            frameHeader.setVisibility(View.GONE);

        } else {
            frameHeader.setVisibility(View.VISIBLE);
            frameHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (setPictures.contains(idHeaderPicture)) {
                        //remove from set and decrease counter, deselect item
                        setPictures.remove(idHeaderPicture);
                        selectingHeader.setVisibility(View.GONE);
                    } else {
                        //add to set and increase counter, select item
                        setPictures.add(idHeaderPicture);
                        selectingHeader.setVisibility(View.VISIBLE);
                    }
                    if (setPictures.size() == 0) {
                        tvTitle.setText(titleActivity);
                        showActions = false;
                    } else {
                        tvTitle.setText("(" + setPictures.size() + ")");
                        showActions = true;
                    }
                    invalidateOptionsMenu();
                }
            });
            //albumCursor.moveToFirst();
            albumTitle.setText(albumCursor.getString(albumCursor.getColumnIndex(PictureProvider.ALBUM_NAME)));
            albumTvCount.setText(new Integer(albumCount).toString());
            isPlaying = albumCursor.getInt(albumCursor.getColumnIndex(PictureProvider.ALBUM_IS_PLAY));
            if (isPlaying == PictureProvider.PLAY) {
                llIsPlaying.setVisibility(View.VISIBLE);
            } else {
                llIsPlaying.setVisibility(View.INVISIBLE);
            }
            String firstPicturePath = albumCursor.getString(albumCursor.getColumnIndex(PictureProvider.ALBUM_FILE));
            Picasso.with(this).load(new File(firstPicturePath)).into(headerPicture);
            //headerPicture.setImageBitmap(BitmapFactory.decodeFile(firstPicturePath));

            Cursor pictureHeaderCursor = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                    PictureProvider.PICTURE_ALBUM_ID + "=" + idAlbum + " and "
                            + PictureProvider.PICTURE_IS_MAIN + "=" + PictureProvider.MAIN,
                    null, null);
            if (pictureHeaderCursor.moveToFirst()) {
                idHeaderPicture = pictureHeaderCursor.getLong(pictureHeaderCursor.getColumnIndex(PictureProvider.PICTURE_ID));

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_pictures, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.setGroupVisible(R.id.select_pictures_menu_group, showActions);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.ab_share_pictures:
                ArrayList<Uri> list = new ArrayList<>();
                for (long id : setPictures) {
                    Uri uriSharedPicture = ContentUris.withAppendedId(PictureProvider.PICTURE_CONTENT_URI, id);
                    Cursor pictureCursor = getContentResolver().query(uriSharedPicture, null, null, null, null);
                    pictureCursor.moveToFirst();
                    String picturePath = pictureCursor.getString(pictureCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
                    if (!TextUtils.isEmpty(picturePath)) {
                        File sharedFile = new File(picturePath);
                        Uri contentUri = Uri.fromFile(sharedFile);
                        if (contentUri != null) {
                            list.add(contentUri);
                        }
                    }
                }
                if (list.size() == 0) break;
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.setType("image/jpeg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list);
                startActivity(Intent.createChooser(shareIntent, "Share"));
                break;

            case R.id.ab_delete_pictures:
                boolean refactorHeader = setPictures.contains(idHeaderPicture);

                for (long id : setPictures) {
                    Uri uriDeletedPicture = ContentUris.withAppendedId(PictureProvider.PICTURE_CONTENT_URI, id);
                    Cursor pictureCursor = getContentResolver().query(uriDeletedPicture, null, null, null, null);
                    pictureCursor.moveToFirst();
                    String picturePath = pictureCursor.getString(pictureCursor.getColumnIndex(PictureProvider.PICTURE_FILE));
                    new File(picturePath).delete();
                    String previewPath = pictureCursor.getString(pictureCursor.getColumnIndex(PictureProvider.PICTURE_FILE_PREVIEW));
                    new File(previewPath).delete();
                    getContentResolver().delete(uriDeletedPicture, null, null);
                    countPictures = countPictures - 1;
                }

                setPictures.clear();
                showActions = false;
                invalidateOptionsMenu();

                tvTitle.setText(titleActivity);
                albumTvCount.setText(Integer.toString(countPictures));

                Uri updatedAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
                ContentValues cvAlbum = new ContentValues();
                cvAlbum.put(PictureProvider.ALBUM_COUNT, countPictures);

                if (refactorHeader) {
                    //get first picture, set it as main
                    Cursor lastPictures = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                            PictureProvider.PICTURE_ALBUM_ID + "=" + idAlbum,
                            null, null);
                    if (lastPictures.moveToLast()) {
                        int idNewMainPicture = lastPictures.getInt(lastPictures.getColumnIndex(PictureProvider.PICTURE_ID));
                        ContentValues newMainCV = new ContentValues();
                        newMainCV.put(PictureProvider.PICTURE_IS_MAIN, PictureProvider.MAIN);
                        Uri uriNewMainPicture = ContentUris.withAppendedId(PictureProvider.PICTURE_CONTENT_URI, idNewMainPicture);
                        getContentResolver().update(uriNewMainPicture, newMainCV, null, null);

                        //write new file_name to album
                        String newBigFile = lastPictures.getString(lastPictures.getColumnIndex(PictureProvider.PICTURE_FILE));
                        String newPreviewFile = lastPictures.getString(lastPictures.getColumnIndex(PictureProvider.PICTURE_FILE_PREVIEW));
                        cvAlbum.put(PictureProvider.ALBUM_FILE, newBigFile);
                        cvAlbum.put(PictureProvider.ALBUM_FILE_PREVIEW, newPreviewFile);
                    }
                }
                getContentResolver().update(updatedAlbum, cvAlbum, null, null);
                if (refactorHeader) {
                    refreshHeader();
                    selectingHeader.setVisibility(View.GONE);
                }

                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_drop_down:
                PopupMenu popupMenu = new PopupMenu(this, v);
                popupMenu.inflate(R.menu.menu_popup);
                if (setPictures.isEmpty()) {
                    popupMenu.getMenu().findItem(R.id.action_clear).setVisible(false);
                } else {
                    popupMenu.getMenu().findItem(R.id.action_clear).setVisible(true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.action_select_all:
                                setPictures.add(idHeaderPicture);
                                selectingHeader.setVisibility(View.VISIBLE);
                                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                                    long id = cursor.getLong(cursor.getColumnIndex(PictureProvider.ALBUM_ID));
                                    setPictures.add(id);
                                }
                                adapter.notifyDataSetChanged();
                                tvTitle.setText("(" + setPictures.size() + ")");
                                showActions = true;
                                invalidateOptionsMenu();
                                return true;
                            case R.id.action_clear:
                                tvTitle.setText(titleActivity);
                                setPictures.clear();
                                selectingHeader.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                                tvTitle.setText(titleActivity);
                                showActions = false;
                                invalidateOptionsMenu();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
                break;
        }
    }
}
