package com.mobapply.happymoments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mobapply.happymoments.R;

/**
 * Created by apelipets on 8/10/15.
 */
public class NewAlbumDialog extends DialogFragment {

    public NewAlbumDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        EditText view = new EditText(getActivity());
        view.setHint(getActivity().getResources().getString(R.string.dialog_hint));
        view.setPadding(16,24,16,24);
        builder.setTitle(getActivity().getResources().getString(R.string.dialog_title));
        builder.setPositiveButton(getActivity().getResources().getString(R.string.btn_save), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

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
