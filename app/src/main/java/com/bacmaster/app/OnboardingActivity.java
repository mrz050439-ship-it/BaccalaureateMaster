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

/**
 * First-run track picker: the student picks their Baccalaureate track once and the
 * home screen adapts to their subjects. Skippable and changeable later.
 */
public class OnboardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        buildTrackGrid();

        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });

        Fonts.apply(findViewById(android.R.id.content));
    }

    private void buildTrackGrid() {
        LinearLayout grid = findViewById(R.id.gridTracks);
        grid.removeAllViews();
        int margin = dp(7);
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
            card.setPadding(dp(14), dp(20), dp(14), dp(20));
            card.setBackgroundResource(R.drawable.card_white);
            card.setElevation(dp(2));
            card.setClickable(true);

            TextView emoji = new TextView(this);
            emoji.setText(track.emoji);
            emoji.setTextSize(40);
            GradientDrawable badge = new GradientDrawable();
            badge.setShape(GradientDrawable.RECTANGLE);
            badge.setCornerRadius(dp(18));
            badge.setColor((track.color & 0x00FFFFFF) | 0x24000000);
            emoji.setBackground(badge);
            emoji.setPadding(dp(12), dp(12), dp(12), dp(12));
            card.addView(emoji);

            TextView name = new TextView(this);
            name.setText(track.name);
            name.setTextSize(15);
            name.setTypeface(null, android.graphics.Typeface.BOLD);
            name.setTextColor(getResources().getColor(R.color.text));
            name.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            nameLp.topMargin = dp(10);
            card.addView(name, nameLp);

            TextView desc = new TextView(this);
            desc.setText(trackDescription(track.id));
            desc.setTextSize(12);
            desc.setTextColor(getResources().getColor(R.color.text_secondary));
            desc.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            descLp.topMargin = dp(4);
            card.addView(desc, descLp);

            View strip = new View(this);
            GradientDrawable stripBg = new GradientDrawable();
            stripBg.setColor(track.color);
            stripBg.setCornerRadius(dp(4));
            strip.setBackground(stripBg);
            LinearLayout.LayoutParams stripLp = new LinearLayout.LayoutParams(dp(48), dp(5));
            stripLp.topMargin = dp(10);
            card.addView(strip, stripLp);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionManager.get(OnboardingActivity.this).setPreferredTrack(track.id);
                    goHome();
                }
            });

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            cardLp.setMargins(margin, margin, margin, margin);
            row.addView(card, cardLp);

            // staggered entrance
            card.setAlpha(0f);
            card.setTranslationY(dp(18));
            card.animate().alpha(1f).translationY(0f).setDuration(300)
                    .setStartDelay(120L + i * 90L).start();
            i++;
        }
    }

    private String trackDescription(String trackId) {
        if ("medicine".equals(trackId)) return "دكتور، صيدلي، باحث علمي";
        if ("engineering".equals(trackId)) return "مهندس، مبرمج، محلل بيانات";
        if ("business".equals(trackId)) return "محاسب، اقتصادي، رائد أعمال";
        return "أديب، فنان، مصمم";
    }

    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
