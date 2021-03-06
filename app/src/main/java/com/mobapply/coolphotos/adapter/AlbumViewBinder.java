package com.mobapply.coolphotos.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Set;


public class AlbumViewBinder implements SimpleCursorAdapter.ViewBinder {
    private Context ctx;
    private Set<Long> setAlbums;

    public AlbumViewBinder(Context ctx) {
        this.ctx = ctx;
    }

    public Set<Long> getSetAlbums() {
        return setAlbums;
    }

    public void setSetAlbums(Set<Long> setAlbums) {
        this.setAlbums = setAlbums;
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (cursor == null)
            return false;
        switch (view.getId()) {
            case R.id.tv_album_count:
                TextView countTextView  = (TextView) view;
                int count = cursor.getInt(cursor.getColumnIndex(PictureProvider.ALBUM_COUNT));
                if(count == 0){
                    countTextView.setText(R.string.empty_album);
                } else{
                    countTextView.setText(count + " " + ctx.getString(R.string.moments_count));
                }
                return true;
            case R.id.picture:
                AlbumImageView picture = (AlbumImageView) view;
                String fileFirstPicturePath = cursor.getString(cursor.getColumnIndex(PictureProvider.ALBUM_FILE));

                //image empty or first picture
                if (!TextUtils.isEmpty(fileFirstPicturePath)) {
                    File firstPicture = new File(fileFirstPicturePath);
                    if (firstPicture.exists()) {
                        Picasso.with(ctx).load(firstPicture).into(picture);
                      //  picture.setImageBitmap(BitmapFactory.decodeFile(fileFirstPicturePath));
                    } else {
                        picture.setImageDrawable(ctx.getResources().getDrawable(R.drawable.empty_small));
                    }
                } else {
                    picture.setImageDrawable(ctx.getResources().getDrawable(R.drawable.empty_small));
                }

                return true;

            case R.id.ic_is_playing:
                ImageView icon = (ImageView) view;
                int isPlay = cursor.getInt(cursor.getColumnIndex(PictureProvider.ALBUM_IS_PLAY));
                if (isPlay == PictureProvider.PLAY) {
                    icon.setVisibility(View.VISIBLE);
                } else {
                    icon.setVisibility(View.GONE);
                }
                return true;

            case R.id.selecting_album:
                if (setAlbums == null)
                    return true;
                ImageView selecting = (ImageView) view;
                long id = cursor.getLong(columnIndex);
                if (setAlbums.contains(id)) {
                    selecting.setVisibility(View.VISIBLE);
                } else {
                    selecting.setVisibility(View.GONE);
                }
                return true;
            default:
                return false;
        }
    }
}