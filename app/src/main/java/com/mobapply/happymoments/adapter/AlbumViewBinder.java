package com.mobapply.happymoments.adapter;

import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;

/**
 * Created by apelipets on 8/12/15.
 */
public class AlbumViewBinder implements SimpleCursorAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        return false;
    }
}
