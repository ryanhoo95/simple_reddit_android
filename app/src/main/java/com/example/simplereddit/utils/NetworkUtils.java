package com.example.simplereddit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.simplereddit.R;
import com.example.simplereddit.utils.customDialog.SimpleDialog;

import timber.log.Timber;

public class NetworkUtils {

    public static boolean isConnectionAvailable(Context context, boolean showAlertDialog) {
        if (isNetworkConnected(context) && isInternetAvailable(context)) {
            return true;
        } else if (showAlertDialog){
            // show an alert dialog
            new SimpleDialog(context)
                    .setIcon(R.drawable.ic_error)
                    .setMessage(context.getString(R.string.txt_no_internet))
                    .show();

            return false;
        } else {
            return false;
        }
    }

    // check whether device has network connection
    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if(activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            }
        }
        catch (NullPointerException e) {
            Timber.e(e);
            return false;
        }

        return false;
    }

    // ping with google
    private static boolean isInternetAvailable(Context context) {
        boolean reachable = false;

        String host = "google.com";

        String cmd = "ping -c 1 " + host;
        try {
            Process p1 = Runtime.getRuntime().exec(cmd);
            int returnVal = p1.waitFor();
            reachable = (returnVal == 0); // success == 0
        } catch (Exception e) {
            Timber.d(e);
        }

        return reachable;
    }
}
