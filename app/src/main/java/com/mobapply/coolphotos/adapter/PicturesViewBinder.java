package com.mobapply.coolphotos.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Set;


public class PicturesViewBinder implements SimpleCursorAdapter.ViewBinder {
    private Set<Long> setPictures;
    private Context ctx;

    public PicturesViewBinder(Context ctx) {
        this.ctx = ctx;
    }

    public Set<Long> getSetPictures() {
        return setPictures;
    }

    public void setSetPictures(Set<Long> setPictures) {
        this.setPictures = setPictures;
    }


    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (cursor == null)
            return false;

        switch (view.getId()){
            case R.id.picture:
                SquireImageView picture = (SquireImageView) view;
                String pathName = cursor.getString(cursor.getColumnIndex(PictureProvider.PICTURE_FILE));
                Picasso.with(ctx).load(new File(pathName)).into(picture);
              //  picture.setImageBitmap(BitmapFactory.decodeFile(pathName));
                return true;
            case R.id.selecting_picture:
                if (setPictures == null)
                    return true;
                ImageView selecting = (ImageView) view;
                long id = cursor.getLong(columnIndex);
                if (setPictures.contains(id)) {
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
