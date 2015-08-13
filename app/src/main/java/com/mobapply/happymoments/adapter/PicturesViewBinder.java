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


public class PicturesViewBinder implements SimpleCursorAdapter.ViewBinder{



    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (cursor == null)
            return false;

        SquireImageView picture = (SquireImageView)view;
                picture.setImageBitmap(BitmapFactory.decodeFile(cursor.getString(cursor.getColumnIndex(PictureProvider.PICTURE_FILE))));
        return true;
    }
}
