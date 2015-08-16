package com.mobapply.happymoments.activity;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;
import com.mobapply.happymoments.adapter.HeaderImageView;
import com.mobapply.happymoments.adapter.PicturesViewBinder;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

import java.io.File;
import java.util.Calendar;

public class PicturesActivity extends AppCompatActivity implements View.OnClickListener {

    private long idAlbum;
    private long countPictures;
    private String titleAlbum;
    private Uri uriAlbum;
    private CoordinatorLayout container;
    private RelativeLayout empty;
    private GridView grid;
    private ImageView eyescreamImageView;
    private ProgressBar progressBar;
    private Uri albumUri;
    private String albumPath, picturePath;
    private boolean selectMenu = false;
    private FrameLayout frameHeader;
    private LinearLayout llIsPlaying;
    private HeaderImageView headerPicture;
    private TextView albumTitle, albumTvCount;
    private int albumCount;
    private FloatingActionButton fab;
    private int isPlaying = PictureProvider.PlAY_NOT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        initViews();

        parseIntent();

        restoreActionBar();

        updateLayout();

        init();

        fillData();

        updateFAB();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        idAlbum = intent.getLongExtra(Constants.EXTRA_ID, 0);
        countPictures = intent.getIntExtra(Constants.EXTRA_COUNT, 0);
        titleAlbum = intent.getStringExtra(Constants.EXTRA_TITLE);
    }

    private void updateLayout() {
        if (countPictures > 0) {
            getSupportActionBar().setTitle("");
            container.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle(titleAlbum);
            container.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        if (idAlbum > 0) {
            uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        }
        albumUri = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        Cursor albumCursor = getContentResolver().query(albumUri, null, null, null, null);
        if (albumCursor.moveToFirst()) {
            albumPath = albumCursor.getString(albumCursor.getColumnIndex(PictureProvider.ALBUM_FOLDER));
        }
    }

    private void updateFAB() {
        if (isPlaying == PictureProvider.PlAY_NOT) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_36dp));
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_36dp));
        }
    }

    private void fillData() {
        //header
        refreshHeader();

        //pictures container
        Cursor cursor = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                PictureProvider.PICTURE_ALBUM_ID + "=" + idAlbum + " and "
                        + PictureProvider.PICTURE_IS_MAIN + "=" + PictureProvider.NOT_MAIN,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.PICTURE_FILE};
        int to[] = {R.id.picture};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_picture, cursor, from, to);


        adapter.setViewBinder(new PicturesViewBinder(this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                View cell = adapter.getView(position, null, null);
                ImageView selecting = (ImageView) cell.findViewById(R.id.selecting_picture);
                selecting.setVisibility(View.INVISIBLE);
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
            fab.setVisibility(View.GONE);
        } else {
            frameHeader.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
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
            headerPicture.setImageBitmap(BitmapFactory.decodeFile(firstPicturePath));
        }
    }


    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        setTitle("");
        actionBar.setIcon(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void initViews() {
        container = (CoordinatorLayout) findViewById(R.id.container);
        empty = (RelativeLayout) findViewById(R.id.empty);
        grid = (GridView) findViewById(R.id.gridPictures);
        eyescreamImageView = (ImageView) findViewById(R.id.eyescream_image);
        eyescreamImageView.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        fab = (FloatingActionButton) findViewById(R.id.fab_pictures);
        fab.setOnClickListener(this);

        frameHeader = (FrameLayout) findViewById(R.id.header);
        headerPicture = (HeaderImageView) findViewById(R.id.header_picture);
        albumTitle = (TextView) findViewById(R.id.tv_album_title);
        albumTvCount = (TextView) findViewById(R.id.tv_album_count);
        llIsPlaying = (LinearLayout) findViewById(R.id.ll_is_playing);
    }

    private void selectPictute() {
        // choose photo from gallery
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intentGallery,
                        getResources().getString(R.string.chooser_gallery)),
                Constants.REQUEST_CODE_GALLERY);
    }

    private void capturePicture() {
        Intent intentCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        File f = HappyMomentsUtils.generateCaptureFile(albumPath);
        picturePath = f.getAbsolutePath();
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intentCapture, Constants.REQUEST_CODE_PHOTO);
    }

    ;

    private void playPauseAlbum() {
        if (isPlaying == PictureProvider.PLAY) {
            isPlaying = PictureProvider.PlAY_NOT;
        } else {
            isPlaying = PictureProvider.PLAY;
        }
        updateFAB();

        ContentValues cvPlayPause = new ContentValues();
        if (isPlaying == PictureProvider.PLAY) {
            cvPlayPause.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PLAY);
        } else {
            cvPlayPause.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
        }
        Uri uriAlbumPlayStop = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        getContentResolver().update(uriAlbumPlayStop, cvPlayPause, null, null);

        refreshHeader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pictures, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem selectPictures = menu.findItem(R.id.action_select_pictures);
        selectPictures.setVisible(countPictures > 0);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_add_from_gallery:
                if (!selectMenu) {
                    selectMenu = true;
                    selectPictute();
                }
                break;

            case R.id.action_capture:
                if (!selectMenu) {
                    selectMenu = true;
                    capturePicture();
                }
                break;

            case R.id.action_select_pictures:
                if (!selectMenu) {
                    Intent intentSelectPictures = new Intent(this, SelectPicturesActivity.class);
                    intentSelectPictures.putExtra(Constants.EXTRA_ID, idAlbum);
                    intentSelectPictures.putExtra(Constants.EXTRA_COUNT, countPictures);
                    intentSelectPictures.putExtra(Constants.EXTRA_TITLE, titleAlbum);

                    startActivity(intentSelectPictures);
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
            case R.id.fab_pictures:
                playPauseAlbum();
                break;
            case R.id.eyescream_image:
                selectPictute();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        selectMenu = false;
        if (requestCode == Constants.REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                AddPictureAsyncTask task = new AddPictureAsyncTask();
                task.execute(picturePath, this, picturePath);
            }
        } else if (requestCode == Constants.REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImageUri = data.getData();
                String selectedImagePath = HappyMomentsUtils.getImagePath(selectedImageUri, this);
                File pictureFile = HappyMomentsUtils.generateCaptureFile(albumPath);

                AddPictureAsyncTask task = new AddPictureAsyncTask();
                task.execute(selectedImagePath, this, pictureFile.getAbsolutePath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class AddPictureAsyncTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Object... param) {
            String oldPicturePath = (String) param[0];
            AppCompatActivity ctx = (AppCompatActivity) param[1];
            String newPicturePath = (String) param[2];

            HappyMomentsUtils.rotateAndSaveCapture(oldPicturePath, ctx, newPicturePath);

            Uri uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
            Cursor albumQuery = getContentResolver().query(uriAlbum, null, null, null, null);
            int countAlbum = 0;
            boolean isFirstPicture = false;
            if (albumQuery.moveToFirst()) {
                countAlbum = albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_COUNT));
                ContentValues cvAlbum = new ContentValues();
                if (countAlbum == 0) {
                    // add first picture to empty album
                    cvAlbum.put(PictureProvider.ALBUM_FILE, newPicturePath);
                    isFirstPicture = true;
                }
                // change count of pictures in the album
                cvAlbum.put(PictureProvider.ALBUM_COUNT, countAlbum + 1);
                getContentResolver().update(uriAlbum, cvAlbum, null, null);
            }

            Calendar calendar = Calendar.getInstance();
            Long date = calendar.getTimeInMillis();
            ContentValues cv = new ContentValues();
            cv.put(PictureProvider.PICTURE_ALBUM_ID, idAlbum);
            cv.put(PictureProvider.PICTURE_DATE, date);
            cv.put(PictureProvider.PICTURE_FILE, newPicturePath);
            if (isFirstPicture) {
                cv.put(PictureProvider.PICTURE_IS_MAIN, PictureProvider.MAIN);
            } else {
                cv.put(PictureProvider.PICTURE_IS_MAIN, PictureProvider.NOT_MAIN);
            }
            cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
            Uri newUri = getContentResolver()
                    .insert(PictureProvider.PICTURE_CONTENT_URI, cv);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            countPictures++;
            invalidateOptionsMenu();
            updateLayout();
            refreshHeader();
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
        }
    }
}
