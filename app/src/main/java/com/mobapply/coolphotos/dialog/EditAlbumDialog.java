package com.mobapply.coolphotos.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.EditText;

import com.mobapply.coolphotos.R;
import com.mobapply.coolphotos.provider.PictureProvider;


public class EditAlbumDialog extends DialogFragment {
    public static final String EXTRA_ID = "id_extra";
    public static final String EXTRA_TITLE = "title_extra";
    long idAlbum;
    String titleAlbum;

    public void setCallback(UpdateCallback callback) {
        this.callback = callback;
    }

    private UpdateCallback callback;

    public EditAlbumDialog() {
    }

    public interface UpdateCallback {
        void update(String newTitle);
    }

    public static EditAlbumDialog create(long idAlbum, String titleAlbum) {
        EditAlbumDialog dialog = new EditAlbumDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ID, idAlbum);
        bundle.putString(EXTRA_TITLE, titleAlbum);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        Bundle bundle=getArguments();
        this.idAlbum = bundle.getLong(EXTRA_ID);
        this.titleAlbum = bundle.getString(EXTRA_TITLE);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        final EditText view = new EditText(getActivity());
        view.setHint(getActivity().getResources().getString(R.string.dialog_hint));
        if (!TextUtils.isEmpty(titleAlbum)) {
            view.setText(titleAlbum);
        }
        view.setPadding(48, 24, 48, 24);
        builder.setTitle(getActivity().getResources().getString(R.string.dialog_edit));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = view.getText().toString();
                if (!TextUtils.isEmpty(title)) {
                    updateAlbumTitle(title);
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
        Dialog dialog = builder.create();
        dialog.getWindow().getDecorView().setPadding(24, 24, 24, 24);
        return dialog;
    }

    private void updateAlbumTitle(String newTitle) {
        Uri updatedAlbum = ContentUris.withAppendedId(PictureProvider.ALBUM_CONTENT_URI, idAlbum);
        ContentValues cvAlbum = new ContentValues();
        cvAlbum.put(PictureProvider.ALBUM_NAME, newTitle);
        getActivity().getContentResolver().update(updatedAlbum, cvAlbum, null, null);
        if (callback != null) callback.update(newTitle);
    }


}
