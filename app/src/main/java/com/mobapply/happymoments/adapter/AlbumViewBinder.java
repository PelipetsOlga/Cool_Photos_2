package com.mobapply.happymoments.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.provider.PictureProvider;

import java.io.File;


public class AlbumViewBinder implements SimpleCursorAdapter.ViewBinder {
    private Context ctx;

    public AlbumViewBinder(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (cursor == null)
            return false;
        switch (view.getId()) {
            case R.id.picture:
                AlbumImageView picture = (AlbumImageView) view;
                String fileFirstPicturePath = cursor.getString(cursor.getColumnIndex(PictureProvider.ALBUM_FILE));

                //image empty or first picture
                if (!TextUtils.isEmpty(fileFirstPicturePath)) {
                    File firstPicture = new File(fileFirstPicturePath);
                    if (firstPicture.exists()) {
                        picture.setImageBitmap(BitmapFactory.decodeFile(fileFirstPicturePath));
                    } else {
                        picture.setImageDrawable(ctx.getResources().getDrawable(R.drawable.empty_folder_thumbnai_174dp));
                    }
                } else {
                    picture.setImageDrawable(ctx.getResources().getDrawable(R.drawable.empty_folder_thumbnai_174dp));
                }

                return true;

            case R.id.ic_is_playing:
                ImageView icon = (ImageView) view;
                int isPlay=cursor.getInt(cursor.getColumnIndex(PictureProvider.ALBUM_IS_PLAY));
                if (isPlay==PictureProvider.PLAY){
                    icon.setVisibility(View.VISIBLE);
                }else{
                    icon.setVisibility(View.GONE);
                }
                return true;

            default:
                return false;
        }

    }
}