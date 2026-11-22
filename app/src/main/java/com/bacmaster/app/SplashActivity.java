package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.ui.Fonts;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Fonts.apply(findViewById(android.R.id.content));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Class<?> next = SessionManager.get(SplashActivity.this).isLoggedIn()
                        ? MainActivity.class : WelcomeActivity.class;
                startActivity(new Intent(SplashActivity.this, next));
                finish();
            }
        }, 1400);
    }
}
