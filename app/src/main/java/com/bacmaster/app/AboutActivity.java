package com.bacmaster.app;

import android.app.Activity;
import android.os.Bundle;

import com.bacmaster.app.ui.Fonts;
import android.view.View;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Fonts.apply(findViewById(android.R.id.content));
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
