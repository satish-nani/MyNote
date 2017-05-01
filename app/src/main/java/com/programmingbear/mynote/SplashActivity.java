package com.programmingbear.mynote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by satish on 4/3/2017.
 */

public class SplashActivity extends Activity {

    /*CoordinatorLayout coordinatorLayout;
    private static int SPLASH_TIME_OUT = 3000;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.layout);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashActivity.this, WelcomeScreen.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);*/

        Intent intent = new Intent(this, WelcomeScreen.class);
        startActivity(intent);
        finish();
    }

}