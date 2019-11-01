package com.example.simplereddit.utils.customDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.example.simplereddit.R;

import timber.log.Timber;

public class SimpleDialog {
    private Context context;
    private boolean cancelable;
    private AlertDialog alertDialog;
    private ImageView ivIcon;
    private TextView tvMessage;
    private AppCompatButton btnAction;

    public SimpleDialog(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            // inflate view
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.dialog_simple, null);
            ivIcon = view.findViewById(R.id.iv_icon);
            tvMessage = view.findViewById(R.id.tv_message);
            btnAction = view.findViewById(R.id.btn_action);

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

            // by default, dismiss dialog when button is clicked
            btnAction.setOnClickListener(view1 -> dismiss());
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

    public SimpleDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public SimpleDialog setIcon(int icon) {
        ivIcon.setImageResource(icon);
        return this;
    }

    public SimpleDialog setAction(String text, View.OnClickListener clickListener) {
        if (!TextUtils.isEmpty(text)) {
            btnAction.setText(text);
        }

        if (clickListener != null) {
            btnAction.setOnClickListener(clickListener);
        }
        return this;
    }
}
