package com.bacmaster.app;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bacmaster.app.data.SessionManager;
import com.bacmaster.app.ui.Fonts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lesson reader: the official ministry-book pages as scrollable images,
 * plus a one-tap PDF download of the same pages (the "ملزمة").
 */
public class LessonActivity extends Activity {

    public static final String EXTRA_LESSON_ID = "lesson_id";
    public static final String EXTRA_LESSON_TITLE = "lesson_title";
    public static final String EXTRA_SUBJECT_NAME = "subject_name";
    public static final String EXTRA_SUBJECT_COLOR = "subject_color";
    public static final String EXTRA_CONTENT_PATH = "content_path";

    private static final int REQ_WRITE = 41;

    private String lessonId;
    private String lessonTitle;
    private String contentPath;
    private boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        lessonTitle = getIntent().getStringExtra(EXTRA_LESSON_TITLE);
        String subject = getIntent().getStringExtra(EXTRA_SUBJECT_NAME);
        contentPath = getIntent().getStringExtra(EXTRA_CONTENT_PATH);
        int color = getIntent().getIntExtra(EXTRA_SUBJECT_COLOR, 0xFF0E7C61);

        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{color, darker(color)});
        headerBg.setCornerRadii(new float[]{0, 0, 0, 0,
                dp(26), dp(26), dp(26), dp(26)});
        findViewById(R.id.headerLesson).setBackground(headerBg);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tvTitle)).setText(lessonTitle);
        ((TextView) findViewById(R.id.tvSubtitle)).setText(subject);

        WebView webPages = findViewById(R.id.webPages);
        View emptyView = findViewById(R.id.emptyView);
        Button btnDownload = findViewById(R.id.btnDownload);

        if (contentPath == null || contentPath.isEmpty()) {
            webPages.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.GONE);
        } else {
            renderPages(webPages, contentPath);
            setupReadProgress(webPages);
            btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveBooklet();
                }
            });
        }

        final Button btnComplete = findViewById(R.id.btnComplete);
        done = SessionManager.get(this).isLessonDone(lessonId);
        refreshCompleteButton(btnComplete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done = !done;
                SessionManager.get(LessonActivity.this).setLessonDone(lessonId, done);
                refreshCompleteButton(btnComplete);
            }
        });

        Fonts.apply(findViewById(android.R.id.content));
    }

    private void setupReadProgress(final WebView webPages) {
        final View fill = findViewById(R.id.viewReadProgress);
        final GradientDrawable g = new GradientDrawable();
        g.setColor(0xFFFFFFFF);
        g.setCornerRadius(dp(3));
        fill.setBackground(g);
        final View bar = (View) fill.getParent();
        webPages.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldX, int oldY) {
                float contentHeight = webPages.getContentHeight() * webPages.getScale()
                        - webPages.getHeight();
                float ratio = contentHeight <= 0 ? 0 : scrollY / contentHeight;
                if (ratio > 1f) ratio = 1f;
                int w = bar.getWidth();
                android.widget.LinearLayout.LayoutParams lp =
                        (android.widget.LinearLayout.LayoutParams) fill.getLayoutParams();
                lp.width = (int) (w * ratio);
                fill.setLayoutParams(lp);
            }
        });
    }

    private void renderPages(WebView webPages, String contentPath) {
        String[] files;
        try {
            files = getAssets().list(contentPath);
        } catch (IOException e) {
            files = null;
        }
        if (files == null || files.length == 0) {
            webPages.setVisibility(View.GONE);
            findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
            return;
        }
        Arrays.sort(files);
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>")
                .append("<style>")
                .append("body{margin:0;padding:6px;background:#F4F5FB;}")
                .append("img{width:100%;display:block;margin:0 auto 10px auto;")
                .append("border-radius:6px;background:#FFFFFF;box-shadow:0 1px 3px rgba(0,0,0,0.18);}")
                .append("</style></head><body>");
        for (String file : files) {
            if (!file.endsWith(".webp")) continue;
            html.append("<img src=\"").append(contentPath).append('/').append(file).append("\"/>");
        }
        html.append("</body></html>");
        WebSettings settings = webPages.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        webPages.loadDataWithBaseURL("file:///android_asset/", html.toString(),
                "text/html", "utf-8", null);
    }

    /** Copies the bundled official-book booklet into the device's Downloads folder. */
    private void saveBooklet() {
        if (Build.VERSION.SDK_INT < 29
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE);
            return;
        }
        Toast.makeText(this, "⏳ جاري تحميل الملزمة...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean ok = writeBooklet();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LessonActivity.this,
                                ok ? "✅ نزلت الملزمة في مجلد التنزيلات (Downloads)"
                                        : "❌ حصلت مشكلة أثناء التحميل — جرب تاني",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    /** Renders the bundled lesson pages into a PDF in the device's Downloads folder. */
    private boolean writeBooklet() {
        String fileName = lessonTitle + ".pdf";
        OutputStream out = null;
        List<android.graphics.Bitmap> pages = new ArrayList<>();
        try {
            String[] files = getAssets().list(contentPath);
            if (files == null || files.length == 0) return false;
            Arrays.sort(files);
            for (String f : files) {
                if (!f.endsWith(".webp")) continue;
                InputStream in = getAssets().open(contentPath + "/" + f);
                android.graphics.Bitmap bmp = android.graphics.BitmapFactory.decodeStream(in);
                in.close();
                if (bmp != null) pages.add(bmp);
            }
            if (pages.isEmpty()) return false;
            if (Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                        values);
                if (uri == null) return false;
                out = getContentResolver().openOutputStream(uri);
            } else {
                File dir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                if (!dir.exists() && !dir.mkdirs()) return false;
                out = new FileOutputStream(new File(dir, fileName));
            }
            if (out == null) return false;
            com.bacmaster.app.ui.PdfLite.write(pages, out);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            for (android.graphics.Bitmap bmp : pages) bmp.recycle();
            try {
                if (out != null) out.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQ_WRITE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            saveBooklet();
        }
    }

    private void refreshCompleteButton(Button button) {
        button.setText(done ? "✅ أنهيت الدرس" : "✔ علّم الدرس كمكتمل");
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
