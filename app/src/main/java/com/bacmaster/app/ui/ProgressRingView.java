package com.bacmaster.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/** Circular progress ring with a percentage label in the middle (framework-only). */
public class ProgressRingView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arc = new RectF();
    private float progress = 0f;
    private int ringColor = 0xFF0E7C61;

    public ProgressRingView(Context context) {
        super(context);
        init();
    }

    public ProgressRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeCap(Paint.Cap.ROUND);
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    /** p from 0 to 1; color is the ring (subject) color. */
    public void setProgress(float p, int color) {
        progress = Math.max(0f, Math.min(1f, p));
        ringColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;
        float stroke = w * 0.13f;
        trackPaint.setStrokeWidth(stroke);
        fillPaint.setStrokeWidth(stroke);
        trackPaint.setColor((ringColor & 0x00FFFFFF) | 0x2A000000);
        fillPaint.setColor(ringColor);
        float pad = stroke / 2f;
        arc.set(pad, pad, w - pad, h - pad);
        canvas.drawArc(arc, 0, 360, false, trackPaint);
        if (progress > 0f) {
            canvas.drawArc(arc, -90, progress * 360f, false, fillPaint);
        }
        int percent = Math.round(progress * 100f);
        textPaint.setColor(ringColor);
        textPaint.setTextSize(w * 0.26f);
        textPaint.setFakeBoldText(true);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float cy = h / 2f - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(percent + "%", w / 2f, cy, textPaint);
    }
}
