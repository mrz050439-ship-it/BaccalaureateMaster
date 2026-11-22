package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.ui.Fonts;

/**
 * Simple local welcome: the student types their name once and the whole app is open.
 * No account, no activation codes, no Firebase — everything works offline.
 */
public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        final EditText etName = findViewById(R.id.etName);
        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(WelcomeActivity.this, "اكتب اسمك الأول 😊", Toast.LENGTH_SHORT).show();
                    return;
                }
                SessionManager.get(WelcomeActivity.this).login(name, "", "", "", false);
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });

        Fonts.apply(findViewById(android.R.id.content));
    }
}
