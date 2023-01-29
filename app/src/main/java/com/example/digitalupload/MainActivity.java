package com.example.digitalupload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Bundle;
import android.os.Handler;

import com.example.digitalupload.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity{

    private final int PERMISSION_REQUEST_CODE = 100;
    private boolean keep = true;
    private final int DELAY = 2250;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        if (fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
        }

    }

    public void fadeTransition(Activity activity){
        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this);

    }

}