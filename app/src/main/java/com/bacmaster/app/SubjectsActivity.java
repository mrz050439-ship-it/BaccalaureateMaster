package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bacmaster.app.data.CurriculumData;
import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.ui.Fonts;
import com.bacmaster.app.model.Models.Lesson;
import com.bacmaster.app.model.Models.Subject;
import com.bacmaster.app.model.Models.Track;
import com.bacmaster.app.model.Models.GradeYear;
import com.bacmaster.app.model.Models.Unit;

import java.util.ArrayList;
import java.util.List;

public class SubjectsActivity extends Activity {

    public static final String EXTRA_TRACK_ID = "track_id";
    public static final String EXTRA_YEAR_ID = "year_id";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_SUBJECT = 1;

    private final List<Object> rows = new ArrayList<>();
    private int lastAnimated = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);
        final String trackId = getIntent().getStringExtra(EXTRA_TRACK_ID);
        final String yearId = getIntent().getStringExtra(EXTRA_YEAR_ID);
        Track track = CurriculumData.getTrack(trackId);
        GradeYear year = CurriculumData.getYear(yearId);
        if (track == null || year == null) {
            finish();
            return;
        }

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tvTitle)).setText(year.title);
        ((TextView) findViewById(R.id.tvSubtitle)).setText("مسار " + track.name);

        buildRows(trackId, yearId);

        ListView list = findViewById(R.id.listSubjects);
        list.setAdapter(new SubjectsAdapter());
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object row = rows.get(position);
                if (!(row instanceof Subject)) return;
                Subject subject = (Subject) row;
                Intent intent = new Intent(SubjectsActivity.this, CourseActivity.class);
                intent.putExtra(CourseActivity.EXTRA_TRACK_ID, trackId);
                intent.putExtra(CourseActivity.EXTRA_YEAR_ID, yearId);
                intent.putExtra(CourseActivity.EXTRA_SUBJECT_ID, subject.id);
                intent.putExtra(CourseActivity.EXTRA_SUBJECT_NAME, subject.name);
                intent.putExtra(CourseActivity.EXTRA_SUBJECT_COLOR, subject.color);
                intent.putExtra(CourseActivity.EXTRA_SUBJECT_EMOJI, subject.emoji);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void buildRows(String trackId, String yearId) {
        rows.clear();
        int lastSection = -1;
        for (Subject subject : CurriculumData.getSubjects(trackId, yearId)) {
            if (subject.section != lastSection) {
                rows.add(CurriculumData.sectionTitle(subject.section, yearId));
                lastSection = subject.section;
            }
            rows.add(subject);
        }
    }

    private int[] subjectProgress(String subjectId, String yearId) {
        int total = 0;
        int done = 0;
        SessionManager session = SessionManager.get(this);
        for (Unit unit : CurriculumData.getUnits(subjectId, yearId)) {
            for (Lesson lesson : unit.lessons) {
                if (!lesson.hasContent()) continue;
                total++;
                if (session.isLessonDone(lesson.id)) done++;
            }
        }
        return new int[]{done, total};
    }

    private class SubjectsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rows.size();
        }

        @Override
        public Object getItem(int position) {
            return rows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return rows.get(position) instanceof String ? TYPE_HEADER : TYPE_SUBJECT;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == TYPE_SUBJECT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            if (getItemViewType(position) == TYPE_HEADER) {
                TextView header = (TextView) (convertView == null
                        ? inflater.inflate(R.layout.item_section_header, parent, false)
                        : convertView);
                header.setText((String) rows.get(position));
                Fonts.apply(header);
                return header;
            }
            View view = convertView == null
                    ? inflater.inflate(R.layout.item_subject, parent, false) : convertView;
            Subject subject = (Subject) rows.get(position);
            ((TextView) view.findViewById(R.id.tvName)).setText(subject.name);

            TextView emoji = view.findViewById(R.id.tvEmoji);
            emoji.setText(subject.emoji);
            GradientDrawable badge = new GradientDrawable();
            badge.setShape(GradientDrawable.RECTANGLE);
            badge.setCornerRadius(dp(14));
            badge.setColor(withAlpha(subject.color, 0.14f));
            emoji.setBackground(badge);

            TextView note = view.findViewById(R.id.tvNote);
            if (subject.note != null && !subject.note.isEmpty()
                    && !subject.note.contains("درجة")) {
                note.setText(subject.note);
                note.setVisibility(View.VISIBLE);
            } else {
                note.setVisibility(View.GONE);
            }

            TextView progress = view.findViewById(R.id.tvProgress);
            int[] p = subjectProgress(subject.id, getIntent().getStringExtra(EXTRA_YEAR_ID));
            if (p[1] > 0) {
                progress.setText("📖 " + p[1] + " درسًا من كتاب الوزارة"
                        + (p[0] > 0 ? " — ✅ " + p[0] + " مكتمل" : ""));
                progress.setVisibility(View.VISIBLE);
            } else {
                progress.setVisibility(View.GONE);
            }

            ((TextView) view.findViewById(R.id.tvChip)).setText(
                    subject.countsInTotal ? "100 درجة" : "نجاح ورسوب");
            Fonts.apply(view);

            if (position > lastAnimated) {
                lastAnimated = position;
                view.setAlpha(0f);
                view.setTranslationY(dp(14));
                view.animate().alpha(1f).translationY(0f).setDuration(280)
                        .setStartDelay(Math.min(position, 8) * 40L).start();
            }
            return view;
        }
    }

    private int withAlpha(int color, float alpha) {
        int a = Math.round(255 * alpha);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
