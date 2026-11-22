package com.bacmaster.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bacmaster.app.data.CurriculumData;
import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.model.Models.Lesson;
import com.bacmaster.app.ui.Fonts;
import com.bacmaster.app.model.Models.Unit;

import java.util.ArrayList;
import java.util.List;

public class CourseActivity extends Activity {

    public static final String EXTRA_TRACK_ID = "track_id";
    public static final String EXTRA_YEAR_ID = "year_id";
    public static final String EXTRA_SUBJECT_ID = "subject_id";
    public static final String EXTRA_SUBJECT_NAME = "subject_name";
    public static final String EXTRA_SUBJECT_COLOR = "subject_color";
    public static final String EXTRA_SUBJECT_EMOJI = "subject_emoji";

    private static final int TYPE_UNIT = 0;
    private static final int TYPE_LESSON = 1;

    private final List<Object> allRows = new ArrayList<>();
    private final List<Object> rows = new ArrayList<>();
    private String yearId;
    private String subjectId;
    private String subjectName;
    private int subjectColor;
    private UnitsAdapter adapter;
    private int lastAnimated = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        yearId = getIntent().getStringExtra(EXTRA_YEAR_ID);
        subjectId = getIntent().getStringExtra(EXTRA_SUBJECT_ID);
        subjectName = getIntent().getStringExtra(EXTRA_SUBJECT_NAME);
        subjectColor = getIntent().getIntExtra(EXTRA_SUBJECT_COLOR, 0xFF0E7C61);
        String subjectEmoji = getIntent().getStringExtra(EXTRA_SUBJECT_EMOJI);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tvTitle)).setText(subjectName);
        ((TextView) findViewById(R.id.tvSubtitle))
                .setText(CurriculumData.getYear(yearId).title);
        if (subjectEmoji != null) {
            ((TextView) findViewById(R.id.tvEmoji)).setText(subjectEmoji);
        }
        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{subjectColor, darker(subjectColor)});
        headerBg.setCornerRadii(new float[]{0, 0, 0, 0,
                dp(26), dp(26), dp(26), dp(26)});
        findViewById(R.id.headerCourse).setBackground(headerBg);

        TextView banner = findViewById(R.id.tvBanner);
        banner.setText(CurriculumData.sourceLabel(subjectId, yearId));
        banner.setVisibility(View.VISIBLE);

        buildAllRows();
        applyFilter("");

        adapter = new UnitsAdapter();
        ListView list = findViewById(R.id.listUnits);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object row = rows.get(position);
                if (!(row instanceof Lesson)) return;
                Lesson lesson = (Lesson) row;
                SessionManager.get(CourseActivity.this).setLastLesson(
                        lesson.id, lesson.title, subjectName, lesson.contentPath, subjectColor);
                Intent intent = new Intent(CourseActivity.this, LessonActivity.class);
                intent.putExtra(LessonActivity.EXTRA_LESSON_ID, lesson.id);
                intent.putExtra(LessonActivity.EXTRA_LESSON_TITLE, lesson.title);
                intent.putExtra(LessonActivity.EXTRA_CONTENT_PATH, lesson.contentPath);
                intent.putExtra(LessonActivity.EXTRA_SUBJECT_NAME, subjectName);
                intent.putExtra(LessonActivity.EXTRA_SUBJECT_COLOR, subjectColor);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        ((EditText) findViewById(R.id.etSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        Fonts.apply(findViewById(android.R.id.content));
        renderProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            renderProgress();
        }
    }

    private void buildAllRows() {
        allRows.clear();
        for (Unit unit : CurriculumData.getUnits(subjectId, yearId)) {
            allRows.add(unit);
            allRows.addAll(unit.lessons);
        }
    }

    private void applyFilter(String query) {
        rows.clear();
        String q = query == null ? "" : query.trim();
        if (q.isEmpty()) {
            rows.addAll(allRows);
            return;
        }
        for (Object row : allRows) {
            if (row instanceof Lesson && ((Lesson) row).title.contains(q)) {
                rows.add(row);
            }
        }
    }

    private void renderProgress() {
        int total = 0;
        int done = 0;
        SessionManager session = SessionManager.get(this);
        for (Object row : allRows) {
            if (row instanceof Lesson) {
                Lesson lesson = (Lesson) row;
                if (!lesson.hasContent()) continue;
                total++;
                if (session.isLessonDone(lesson.id)) done++;
            }
        }
        final int d = done;
        final int t = total;
        ((TextView) findViewById(R.id.tvProgress)).setText(
                t == 0 ? "" : "أنهيت " + d + " من " + t + " درسًا");
        final View fill = findViewById(R.id.viewProgressFill);
        final View bar = (View) fill.getParent();
        bar.post(new Runnable() {
            @Override
            public void run() {
                int w = bar.getWidth();
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) fill.getLayoutParams();
                lp.width = t == 0 ? 0 : (int) (w * (d / (float) t));
                fill.setLayoutParams(lp);
                GradientDrawable g = new GradientDrawable();
                g.setColor(0xFFFFFFFF);
                g.setCornerRadius(dp(6));
                fill.setBackground(g);
            }
        });
    }

    private class UnitsAdapter extends BaseAdapter {
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
            return rows.get(position) instanceof Unit ? TYPE_UNIT : TYPE_LESSON;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == TYPE_LESSON;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            if (getItemViewType(position) == TYPE_UNIT) {
                View view = convertView == null
                        ? inflater.inflate(R.layout.item_unit_header, parent, false) : convertView;
                ((TextView) view.findViewById(R.id.tvUnitTitle))
                        .setText(((Unit) rows.get(position)).title);
                View accent = view.findViewById(R.id.viewAccent);
                GradientDrawable g = new GradientDrawable();
                g.setColor(subjectColor);
                g.setCornerRadius(dp(3));
                accent.setBackground(g);
                Fonts.apply(view);
                return view;
            }
            View view = convertView == null
                    ? inflater.inflate(R.layout.item_lesson, parent, false) : convertView;
            final Lesson lesson = (Lesson) rows.get(position);
            ((TextView) view.findViewById(R.id.tvLessonTitle)).setText(lesson.title);

            TextView number = view.findViewById(R.id.tvNumber);
            number.setText(String.valueOf(lessonNumber(position)));
            GradientDrawable badge = new GradientDrawable();
            badge.setShape(GradientDrawable.RECTANGLE);
            badge.setCornerRadius(dp(12));
            badge.setColor(withAlpha(subjectColor, 0.14f));
            number.setBackground(badge);
            number.setTextColor(subjectColor);

            TextView meta = view.findViewById(R.id.tvLessonMeta);
            meta.setText(lesson.hasContent() ? "📖 محتوى كتاب الوزارة كاملًا" : "⏳ المحتوى يُضاف تباعًا");
            TextView status = view.findViewById(R.id.tvStatus);
            if (SessionManager.get(CourseActivity.this).isLessonDone(lesson.id)) {
                status.setText("✅");
            } else {
                status.setText(lesson.hasContent() ? "📖" : "⏳");
            }

            final TextView star = view.findViewById(R.id.tvStar);
            refreshStar(star, lesson.id);
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SessionManager session = SessionManager.get(CourseActivity.this);
                    boolean fav = !session.isFavorite(lesson.id);
                    session.setFavorite(lesson.id, fav);
                    refreshStar(star, lesson.id);
                    star.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    star.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                                }
                            }).start();
                }
            });
            Fonts.apply(view);

            if (position > lastAnimated) {
                lastAnimated = position;
                view.setAlpha(0f);
                view.setTranslationY(dp(14));
                view.animate().alpha(1f).translationY(0f).setDuration(240)
                        .setStartDelay(Math.min(position, 8) * 30L).start();
            }
            return view;
        }

        private int lessonNumber(int position) {
            int n = 0;
            for (int i = 0; i <= position && i < rows.size(); i++) {
                if (rows.get(i) instanceof Lesson) n++;
            }
            return n;
        }
    }

    private void refreshStar(TextView star, String lessonId) {
        star.setText(SessionManager.get(this).isFavorite(lessonId) ? "⭐" : "☆");
    }

    private int withAlpha(int color, float alpha) {
        int a = Math.round(255 * alpha);
        return (color & 0x00FFFFFF) | (a << 24);
    }

    private int darker(int color) {
        float[] hsv = new float[3];
        android.graphics.Color.colorToHSV(color, hsv);
        hsv[2] *= 0.72f;
        return android.graphics.Color.HSVToColor(hsv);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
