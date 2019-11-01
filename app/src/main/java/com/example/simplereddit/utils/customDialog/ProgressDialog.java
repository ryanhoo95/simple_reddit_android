package com.example.simplereddit.utils.customDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.example.simplereddit.R;

import timber.log.Timber;

public class ProgressDialog {
    private Context context;
    private boolean cancelable;
    private AlertDialog alertDialog;

    public ProgressDialog(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            // inflate view
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.dialog_progress, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            // set view to alert dialog builder
            alertDialogBuilder.setView(view);

            // set whether the dialog can be cancelled on touch
            alertDialogBuilder.setCancelable(cancelable);

            // create alert dialog
            alertDialog = alertDialogBuilder.create();

            // make transparent background
            if (alertDialog.getWindow() != null) {
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void show() {
        if (alertDialog != null && !alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;

        if (alertDialog != null) {
            alertDialog.setCancelable(cancelable);
        }
    }

    public boolean isCancelable() {
        return cancelable;
    }
}
