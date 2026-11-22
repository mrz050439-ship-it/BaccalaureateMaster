package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bacmaster.app.data.CurriculumData;
import com.bacmaster.app.ui.Fonts;
import com.bacmaster.app.model.Models.GradeYear;
import com.bacmaster.app.model.Models.Track;

public class TrackActivity extends Activity {

    public static final String EXTRA_TRACK_ID = "track_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        final Track track = CurriculumData.getTrack(getIntent().getStringExtra(EXTRA_TRACK_ID));
        if (track == null) {
            finish();
            return;
        }

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tvTrackEmoji)).setText(track.emoji);
        ((TextView) findViewById(R.id.tvTrackTitle)).setText(track.name);

        LinearLayout header = findViewById(R.id.headerTrack);
        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{track.color, darker(track.color)});
        header.setBackground(headerBg);

        LinearLayout container = findViewById(R.id.layoutYears);
        for (final GradeYear year : CurriculumData.getYears()) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(16), dp(16), dp(16), dp(16));
            card.setBackgroundResource(R.drawable.card_white);
            card.setElevation(dp(2));
            card.setAlpha(year.contentReady ? 1f : 0.6f);

            TextView title = new TextView(this);
            title.setText(year.title);
            title.setTextSize(17);
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            title.setTextColor(getResources().getColor(R.color.text));
            card.addView(title);

            TextView subtitle = new TextView(this);
            subtitle.setText(year.subtitle);
            subtitle.setTextSize(13);
            subtitle.setTextColor(getResources().getColor(R.color.text_secondary));
            LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            subLp.topMargin = dp(4);
            card.addView(subtitle, subLp);

            TextView chip = new TextView(this);
            chip.setText(year.contentReady ? "متاح الآن" : "قريبًا");
            chip.setTextSize(11);
            chip.setTypeface(null, android.graphics.Typeface.BOLD);
            chip.setTextColor(getResources().getColor(
                    year.contentReady ? R.color.success : R.color.text_secondary));
            chip.setBackgroundResource(R.drawable.chip_bg);
            chip.setPadding(dp(10), dp(4), dp(10), dp(4));
            LinearLayout.LayoutParams chipLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            chipLp.topMargin = dp(10);
            card.addView(chip, chipLp);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!year.contentReady) {
                        Toast.makeText(TrackActivity.this,
                                "منهج الصف الثالث لم يصدر بعد — سيُضاف فور نزوله",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(TrackActivity.this, SubjectsActivity.class);
                    intent.putExtra(SubjectsActivity.EXTRA_TRACK_ID, track.id);
                    intent.putExtra(SubjectsActivity.EXTRA_YEAR_ID, year.id);
                    startActivity(intent);
                }
            });

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cardLp.setMargins(0, 0, 0, dp(12));
            container.addView(card, cardLp);
        }
        Fonts.apply(findViewById(android.R.id.content));
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
