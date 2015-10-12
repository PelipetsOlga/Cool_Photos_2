package com.mobapply.happymoments.dialog;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.activity.AlbumsActivity;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

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
        view.setHintTextColor(getActivity().getResources().getColor(R.color.color_hint));
        view.setPadding(48, 24, 48, 24);

        builder.setTitle(getActivity().getResources().getString(R.string.dialog_title));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = view.getText().toString();
                if (!TextUtils.isEmpty(title)) {
                    createAlbum(title);
                }
            }
        });


        builder.setNegativeButton(getActivity().getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setView(view);
        builder.setCancelable(true);
        Dialog  dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.color_white_bg);
        return dialog;
    }

    private void createAlbum(String title){
        Calendar calendar = Calendar.getInstance();
        Long date = calendar.getTimeInMillis();
        String folderPath = HappyMomentsUtils.getNewAlbumPath(date);
        ContentValues cv = new ContentValues();
        cv.put(PictureProvider.ALBUM_NAME, title);
        cv.put(PictureProvider.ALBUM_DATE, date);
        cv.put(PictureProvider.ALBUM_FOLDER, folderPath);
        cv.put(PictureProvider.ALBUM_COUNT, 0);
        cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
        Uri newUri = getActivity().getContentResolver()
                .insert(PictureProvider.ALBUM_CONTENT_URI, cv);
    };


}
