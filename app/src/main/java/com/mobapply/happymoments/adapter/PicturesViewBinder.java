package com.mobapply.happymoments.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.provider.PictureProvider;

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
                picture.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(PictureProvider.PICTURE_FILE))));
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
