package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bacmaster.app.data.CurriculumData;
import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.model.Models.Track;
import com.bacmaster.app.ui.Fonts;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SessionManager session = SessionManager.get(this);

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        TextView tvEmail = findViewById(R.id.tvEmail);

        String name = session.userName();
        tvGreeting.setText("أهلاً" + (name.isEmpty() ? "" : " يا " + name) + " 👋");
        tvEmail.setText("منهجك كامل من كتب الوزارة — مجانًا وللأبد");

        buildTrackGrid();
        renderContinueCard();
        renderStats();

        findViewById(R.id.bannerAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
        findViewById(R.id.btnAboutNav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logout();
                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                finish();
            }
        });

        Fonts.apply(findViewById(android.R.id.content));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (findViewById(android.R.id.content) != null) {
            renderContinueCard();
            renderStats();
        }
    }

    private void renderContinueCard() {
        final SessionManager session = SessionManager.get(this);
        View card = findViewById(R.id.cardContinue);
        if (!session.hasLastLesson()) {
            card.setVisibility(View.GONE);
            return;
        }
        card.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tvContinueTitle)).setText(session.lastLessonTitle());
        ((TextView) findViewById(R.id.tvContinueSub)).setText(session.lastLessonSubject());
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{session.lastLessonColor(), darker(session.lastLessonColor())});
        bg.setCornerRadius(dp(20));
        card.setBackground(bg);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LessonActivity.class);
                intent.putExtra(LessonActivity.EXTRA_LESSON_ID, session.lastLessonId());
                intent.putExtra(LessonActivity.EXTRA_LESSON_TITLE, session.lastLessonTitle());
                intent.putExtra(LessonActivity.EXTRA_CONTENT_PATH, session.lastLessonContent());
                intent.putExtra(LessonActivity.EXTRA_SUBJECT_NAME, session.lastLessonSubject());
                intent.putExtra(LessonActivity.EXTRA_SUBJECT_COLOR, session.lastLessonColor());
                startActivity(intent);
            }
        });
    }

    private void renderStats() {
        SessionManager session = SessionManager.get(this);
        ((TextView) findViewById(R.id.tvStatsDone))
                .setText(String.valueOf(session.doneLessonsCount()));
        ((TextView) findViewById(R.id.tvStatsFav))
                .setText(String.valueOf(session.favoriteLessons().size()));
    }

    private void buildTrackGrid() {
        LinearLayout grid = findViewById(R.id.gridTracks);
        grid.removeAllViews();
        int margin = dp(6);
        LinearLayout row = null;
        int i = 0;
        for (final Track track : CurriculumData.getTracks()) {
            if (i % 2 == 0) {
                row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                grid.addView(row, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
            }
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            card.setPadding(dp(14), dp(18), dp(14), dp(18));
            card.setBackgroundResource(R.drawable.card_white);
            card.setElevation(dp(2));
            card.setClickable(true);

            TextView emoji = new TextView(this);
            emoji.setText(track.emoji);
            emoji.setTextSize(36);
            card.addView(emoji);

            TextView name = new TextView(this);
            name.setText(track.name);
            name.setTextSize(15);
            name.setTypeface(null, android.graphics.Typeface.BOLD);
            Fonts.apply(card);
            name.setTextColor(getResources().getColor(R.color.text));
            name.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            nameLp.topMargin = dp(8);
            card.addView(name, nameLp);

            View strip = new View(this);
            GradientDrawable stripBg = new GradientDrawable();
            stripBg.setColor(track.color);
            stripBg.setCornerRadius(dp(4));
            strip.setBackground(stripBg);
            LinearLayout.LayoutParams stripLp = new LinearLayout.LayoutParams(dp(48), dp(5));
            stripLp.topMargin = dp(8);
            card.addView(strip, stripLp);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TrackActivity.class);
                    intent.putExtra(TrackActivity.EXTRA_TRACK_ID, track.id);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            cardLp.setMargins(margin, margin, margin, margin);
            row.addView(card, cardLp);
            i++;
        }
    }

    private int darker(int color) {
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] *= 0.75f;
        return android.graphics.Color.HSVToColor(hsv);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
