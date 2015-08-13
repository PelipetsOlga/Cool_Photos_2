package com.mobapply.happymoments.activity;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.mobapply.happymoments.Constants;
import com.mobapply.happymoments.R;
import com.mobapply.happymoments.adapter.HeaderImageView;
import com.mobapply.happymoments.adapter.PicturesViewBinder;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

import java.io.File;
import java.util.Calendar;

public class PicturesActivity extends AppCompatActivity implements View.OnClickListener {

    private Uri uriAlbum;
    private GridView grid;
    private ProgressBar progressBar;
    private long idAlbum;
    private Uri albumUri;
    private String albumPath, picturePath;
    private boolean selectMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        restoreActionBar();
        initViews();

        Intent intent = getIntent();
        idAlbum = intent.getLongExtra(Constants.EXTRA_ID, 0);
        if (idAlbum > 0) {
            uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        }
        albumUri = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        Cursor albumCursor = getContentResolver().query(albumUri, null, null, null, null);
        if (albumCursor.moveToFirst()) {
            albumPath = albumCursor.getString(albumCursor.getColumnIndex(PictureProvider.ALBUM_FOLDER));
        }

        fillData();
    }

    private void fillData() {
        //pictures container
        Cursor cursor = getContentResolver().query(PictureProvider.PICTURE_CONTENT_URI, null,
                PictureProvider.PICTURE_ALBUM_ID + "=" + idAlbum,
                null, null);
        startManagingCursor(cursor);

        String from[] = {PictureProvider.PICTURE_FILE};
        int to[] = {R.id.picture};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item_picture, cursor, from, to);
      //  View v = getLayoutInflater().inflate(R.layout.header_pictures, null);
        //((HeaderImageView)v.findViewById(R.id.header_picture)).setImageDrawable(getResources().getDrawable(R.drawable.ic_exit_brown_24dp)););
        //grid.addHeaderView(v);

        adapter.setViewBinder(new PicturesViewBinder());
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                View cell = adapter.getView(position,null,null);
                ImageView selecting= (ImageView) cell.findViewById(R.id.selecting_picture);
                selecting.setVisibility(View.VISIBLE);
            }
        });
        grid.setAdapter(adapter);
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
        grid = (GridView) findViewById(R.id.gridPictures);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_pictures);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pictures, menu);
        return true;
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
                break;

            case R.id.action_capture:
                if (!selectMenu) {
                    selectMenu = true;
                    Intent intentCapture = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = HappyMomentsUtils.generateCaptureFile(albumPath);
                    picturePath = f.getAbsolutePath();
                    intentCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intentCapture, Constants.REQUEST_CODE_PHOTO);
                }
                break;

            case R.id.action_select_pictures:
                if (!selectMenu) {
                    //TODO
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
                //TODO
                break;
        }
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

            Calendar calendar = Calendar.getInstance();
            Long date = calendar.getTimeInMillis();
            ContentValues cv = new ContentValues();
            cv.put(PictureProvider.PICTURE_ALBUM_ID, idAlbum);
            cv.put(PictureProvider.PICTURE_DATE, date);
            cv.put(PictureProvider.PICTURE_FILE, newPicturePath);
            cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
            Uri newUri = getContentResolver()
                    .insert(PictureProvider.PICTURE_CONTENT_URI, cv);



            Uri uriAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
            Cursor albumQuery=getContentResolver().query(uriAlbum, null, null, null, null);
            if (albumQuery.moveToFirst()){
                ContentValues cvAlbum=new ContentValues();
                int countAlbum=albumQuery.getInt(albumQuery.getColumnIndex(PictureProvider.ALBUM_COUNT));
                if (countAlbum==0){
                    // add first picture to empty album
                    cvAlbum.put(PictureProvider.ALBUM_FILE, newPicturePath );
                }
                // change count of pictures in the album
                cvAlbum.put(PictureProvider.ALBUM_COUNT, countAlbum+1);
                getContentResolver().update(uriAlbum,cvAlbum, null, null);
            }



            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            progressBar.setVisibility(View.GONE);
        }
    }
}
