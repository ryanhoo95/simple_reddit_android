package com.example.simplereddit.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.simplereddit.R;
import com.example.simplereddit.fragment.HomeFragment;
import com.example.simplereddit.utils.ClickUtils;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // go to home page
        loadHome();
    }

    private void loadHome() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fl_fragment, new HomeFragment())
                .addToBackStack(TAG)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

        // since the app only contains two fragment
        // so we know that backstackcount 1 -> home
        // backstackcount 2 -> my post
        if (backStackCount == 1) {
            // double back to exit app
            ClickUtils.backPressToExit(this);
        } else {
            // pop backstack to go back to home page
            getSupportFragmentManager().popBackStack();
        }
    }
}
