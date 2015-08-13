package com.mobapply.happymoments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import com.mobapply.happymoments.R;
import com.mobapply.happymoments.provider.PictureProvider;
import com.mobapply.happymoments.utils.HappyMomentsUtils;

import java.util.Calendar;

/**
 * Created by apelipets on 8/10/15.
 */
public class CreateAlbumDialog extends DialogFragment {

    public CreateAlbumDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText view = new EditText(getActivity());
        view.setHint(getActivity().getResources().getString(R.string.dialog_hint));
        view.setPadding(16, 24, 16, 24);
        builder.setTitle(getActivity().getResources().getString(R.string.dialog_title));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newTitle = view.getText().toString();
                if (!TextUtils.isEmpty(newTitle)) {
                    Calendar calendar = Calendar.getInstance();
                    Long date = calendar.getTimeInMillis();
                    String folderPath = HappyMomentsUtils.getNewAlbumPath(date);
                    ContentValues cv = new ContentValues();
                    cv.put(PictureProvider.ALBUM_NAME, newTitle);
                    cv.put(PictureProvider.ALBUM_DATE, date);
                    cv.put(PictureProvider.ALBUM_FOLDER, folderPath);
                    cv.put(PictureProvider.ALBUM_COUNT, 0);
                    cv.put(PictureProvider.ALBUM_IS_PLAY, PictureProvider.PlAY_NOT);
                    Uri newUri = getActivity().getContentResolver()
                            .insert(PictureProvider.ALBUM_CONTENT_URI, cv);



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

        return builder.create();
    }

}
