package com.mobapply.coolphotos.dialog;

import android.content.ContentUris;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.mobapply.coolphotos.Constants;
import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.activity.PicturesActivity;
import com.mobapply.coolphotos.provider.PictureProvider;
import com.mobapply.coolphotos.utils.CoolPhotosUtils;

import java.util.Calendar;


public class CreateAlbumDialog extends DialogFragment {

    public CreateAlbumDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        final EditText view = new EditText(getActivity());
        view.setHint(getActivity().getResources().getString(R.string.dialog_hint));
        //        view.setHintTextColor(getActivity().getResources().getColor(R.color.color_hint));
        view.setPadding(48, 24, 48, 24);
        builder.setTitle(getActivity().getResources().getString(R.string.dialog_title));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = view.getText().toString();
                if (!TextUtils.isEmpty(title)) {
                    long id = createAlbum(title);
                    showPicturesPage(id, 0, title);
                }
            }
        });
        builder.setNegativeButton(getActivity().getResources().getString(R.string.btn_cancel),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        Dialog  dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawableResource(R.color.color_white_bg);
        dialog.getWindow().getDecorView().setPadding(24, 24, 24, 24);
        return dialog;
    }

    private long createAlbum(String title){
        Calendar calendar = Calendar.getInstance();
        Long date = calendar.getTimeInMillis();
        String folderPath = CoolPhotosUtils.getNewAlbumPath(date);
        ContentValues cv = new ContentValues();
        cv.put(PictureProvider.ALBUM_NAME, title);
        cv.put(PictureProvider.ALBUM_DATE, date);
        cv.put(PictureProvider.ALBUM_FOLDER, folderPath);
        cv.put(PictureProvider.ALBUM_COUNT, 0);
        cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
        Uri newUri = getActivity().getContentResolver()
                .insert(PictureProvider.ALBUM_CONTENT_URI, cv);
        return ContentUris.parseId(newUri);
    };

    private void showPicturesPage(long id, int count, String title){
        Intent intent = new Intent(getActivity(), PicturesActivity.class);
        intent.putExtra(Constants.EXTRA_ID, id);
        intent.putExtra(Constants.EXTRA_COUNT, count);
        intent.putExtra(Constants.EXTRA_TITLE, title);
        intent.putExtra(Constants.EXTRA_ADD_PICTURE, true);
        startActivity(intent);
    }
}
