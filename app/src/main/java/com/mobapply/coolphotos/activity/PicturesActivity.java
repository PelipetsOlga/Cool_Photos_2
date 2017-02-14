package com.mobapply.coolphotos.activity;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.adapter.HeaderImageView;
import com.mobapply.coolphotos.adapter.PicturesViewBinder;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.mobapply.coolphotos.utils.HappyMomentsUtils;
import com.squareup.picasso.Picasso;

import net.yazeed44.imagepicker.model.ImageEntry;
import net.yazeed44.imagepicker.util.Picker;

import java.io.File;
import java.util.ArrayList;
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

    private boolean isAddPicture = false;

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

        addPictureForEmpty();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        idAlbum = intent.getLongExtra(Constants.EXTRA_ID, 0);
        countPictures = intent.getIntExtra(Constants.EXTRA_COUNT, 0);
        titleAlbum = intent.getStringExtra(Constants.EXTRA_TITLE);
        isAddPicture = intent.getBooleanExtra(Constants.EXTRA_ADD_PICTURE, false);
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

    private void addPictureForEmpty(){
        if(isAddPicture){
            selectPicture();
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

        String from[] = {PictureProvider.PICTURE_FILE_PREVIEW};
        int to[] = {R.id.picture};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_picture, cursor, from, to);


        adapter.setViewBinder(new PicturesViewBinder(this));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            Picasso.with(this).load(new File(firstPicturePath)).into(headerPicture);
            //headerPicture.setImageBitmap(BitmapFactory.decodeFile(firstPicturePath));
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



    private void selectPicture(){
        new Picker.Builder(this,new MyPickListener(),R.style.MIP_theme)
                .setPickMode(Picker.PickMode.MULTIPLE_IMAGES)
                .build()
                .startActivity();
    }

    private class MyPickListener implements Picker.PickListener
    {
        @Override
        public void onPickedSuccessfully(final ArrayList<ImageEntry> images)
        {
            selectMenu = false;
            AddPicturesAsyncTask task = new AddPicturesAsyncTask();
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, PicturesActivity.this, images);
        }

        @Override
        public void onCancel(){
            selectMenu = false;
        }
    }

//    private void selectPicture() {
//        // choose photo from gallery
//        Intent intentGallery = new Intent();
//        intentGallery.setType("image/*");
//        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
//        allowMultiple(intentGallery);
//        startActivityForResult(
//                Intent.createChooser(intentGallery,
//                        getResources().getString(R.string.chooser_gallery)),
//                Constants.REQUEST_CODE_GALLERY);
//    }

//    @SuppressLint("NewApi")
//    private void allowMultiple(Intent intent){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        }
//    }

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

        //update album
        ContentValues cvPlayPause = new ContentValues();
        if (isPlaying == PictureProvider.PLAY) {
            cvPlayPause.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PLAY);
        } else {
            cvPlayPause.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
        }
        Uri uriAlbumPlayStop = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        getContentResolver().update(uriAlbumPlayStop, cvPlayPause, null, null);

        //update album's pictures
        cvPlayPause = new ContentValues();
        if (isPlaying == PictureProvider.PLAY) {
            cvPlayPause.put(PictureProvider.PICTURE_IS_PLAY, PictureProvider.PLAY);
        } else {
            cvPlayPause.put(PictureProvider.PICTURE_IS_PLAY, PictureProvider.PlAY_NOT);
        }
        getContentResolver().
                update(PictureProvider.PICTURE_CONTENT_URI, cvPlayPause,
                        PictureProvider.PICTURE_ALBUM_ID + " = " + idAlbum, null);


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
                    selectPicture();
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
                selectPicture();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        Cursor albumQuery = getContentResolver().query(uriAlbum, null, null, null, null);
        if (albumQuery.moveToFirst()) {
            countPictures = albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_COUNT));
        }
        refreshHeader();
        updateLayout();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        selectMenu = false;
        if (requestCode == Constants.REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File previewFile = HappyMomentsUtils.generatePreviewFile(albumPath);
                AddPictureAsyncTask task = new AddPictureAsyncTask();
                task.execute(picturePath, this, picturePath, previewFile.getAbsolutePath());
            }
        } else if (requestCode == Constants.REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) && (data.getData()== null))
                {
                    ClipData clipdata = data.getClipData();
                    for (int i=0; i<clipdata.getItemCount();i++) {
                        Uri selectedImageUri = clipdata.getItemAt(i).getUri();
                        String selectedImagePath = HappyMomentsUtils.getImagePath(selectedImageUri, this);
                        File pictureFile = HappyMomentsUtils.generateCaptureFile(albumPath);
                        File previewFile = HappyMomentsUtils.generatePreviewFile(albumPath);

                        AddPictureAsyncTask task = new AddPictureAsyncTask();
                        task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectedImagePath, this, pictureFile.getAbsolutePath(), previewFile.getAbsolutePath());
                    }
                } else{
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = HappyMomentsUtils.getImagePath(selectedImageUri, this);
                    File pictureFile = HappyMomentsUtils.generateCaptureFile(albumPath);
                    File previewFile = HappyMomentsUtils.generatePreviewFile(albumPath);

                    AddPictureAsyncTask task = new AddPictureAsyncTask();
                    task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, selectedImagePath, this, pictureFile.getAbsolutePath(), previewFile.getAbsolutePath());

                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if( items > columns ){
            x = items/columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
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
            String previewPath = (String) param[3];

            boolean done =  HappyMomentsUtils.rotateAndSaveCapture(oldPicturePath, ctx, newPicturePath);

            if (!done){
                return false;
            }

            HappyMomentsUtils.generatePreview(newPicturePath, previewPath);

            Uri uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
            Cursor albumQuery = getContentResolver().query(uriAlbum, null, null, null, null);
            int countAlbum = 0;
            int isPlayingAlbum=PictureProvider.PlAY_NOT;
            boolean isFirstPicture = false;
            if (albumQuery.moveToFirst()) {
                isPlayingAlbum=albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_IS_PLAY));
                countAlbum = albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_COUNT));
                ContentValues cvAlbum = new ContentValues();
                if (countAlbum == 0) {
                    // add first picture to empty album
                    cvAlbum.put(PictureProvider.ALBUM_FILE, newPicturePath);
                    cvAlbum.put(PictureProvider.ALBUM_FILE_PREVIEW, previewPath);
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
            cv.put(PictureProvider.PICTURE_IS_PLAY, isPlayingAlbum);
            cv.put(PictureProvider.PICTURE_FILE, newPicturePath);
            cv.put(PictureProvider.PICTURE_FILE_PREVIEW, previewPath);
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
            if (aBoolean) {
                countPictures++;
                invalidateOptionsMenu();
                updateLayout();
                refreshHeader();
            }else{
                Toast.makeText(PicturesActivity.this, R.string.error_picture, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
        }
    }


    private class AddPicturesAsyncTask extends AsyncTask<Object, Void, Boolean> {

        private ArrayList<ImageEntry> images;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Object... param) {
            AppCompatActivity ctx = (AppCompatActivity) param[0];
            images = (ArrayList<ImageEntry>) param[1];

            for (ImageEntry image : images) {
                String selectedImagePath = image.path;
                File pictureFile = HappyMomentsUtils.generateCaptureFile(albumPath);
                File previewFile = HappyMomentsUtils.generatePreviewFile(albumPath);

                String oldPicturePath  = image.path;
                String newPicturePath = pictureFile.getAbsolutePath();
                String previewPath = previewFile.getAbsolutePath();

                boolean done = HappyMomentsUtils.rotateAndSaveCapture(oldPicturePath, ctx, newPicturePath);

                if (!done) {
                    break;
                }

                HappyMomentsUtils.generatePreview(newPicturePath, previewPath);

                Uri uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
                Cursor albumQuery = getContentResolver().query(uriAlbum, null, null, null, null);
                int countAlbum = 0;
                int isPlayingAlbum = PictureProvider.PlAY_NOT;
                boolean isFirstPicture = false;
                if (albumQuery.moveToFirst()) {
                    isPlayingAlbum = albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_IS_PLAY));
                    countAlbum = albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_COUNT));
                    ContentValues cvAlbum = new ContentValues();
                    if (countAlbum == 0) {
                        // add first picture to empty album
                        cvAlbum.put(PictureProvider.ALBUM_FILE, newPicturePath);
                        cvAlbum.put(PictureProvider.ALBUM_FILE_PREVIEW, previewPath);
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
                cv.put(PictureProvider.PICTURE_IS_PLAY, isPlayingAlbum);
                cv.put(PictureProvider.PICTURE_FILE, newPicturePath);
                cv.put(PictureProvider.PICTURE_FILE_PREVIEW, previewPath);
                if (isFirstPicture) {
                    cv.put(PictureProvider.PICTURE_IS_MAIN, PictureProvider.MAIN);
                } else {
                    cv.put(PictureProvider.PICTURE_IS_MAIN, PictureProvider.NOT_MAIN);
                }
                cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
                Uri newUri = getContentResolver()
                        .insert(PictureProvider.PICTURE_CONTENT_URI, cv);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            if (aBoolean) {
                countPictures = countPictures + images.size();
                invalidateOptionsMenu();
                updateLayout();
                refreshHeader();
            }else{
                Toast.makeText(PicturesActivity.this, R.string.error_picture, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
        }
    }
}
