package com.example.simplereddit.utils;

import android.app.Activity;
import android.os.Handler;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.simplereddit.R;

public class ClickUtils {
    private static final long INTERVAL_2000 = 2000;  //2s

    private static boolean doubleBackToExitPressedOnce = false;

    public static void backPressToExit(FragmentActivity context) {
        if(doubleBackToExitPressedOnce) {
            context.finishAffinity();
            return;
        }

        doubleBackToExitPressedOnce = true;

        Toast.makeText(context, context.getResources().getString(R.string.txt_press_back_again_to_exit),
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, INTERVAL_2000);
    }
}
