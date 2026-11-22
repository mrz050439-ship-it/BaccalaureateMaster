package com.bacmaster.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** Applies the embedded Tajawal font across view trees (framework-only, no support libs). */
public final class Fonts {

    private static Typeface regular;
    private static Typeface bold;

    private Fonts() {
    }

    private static void ensure(Context context) {
        if (regular == null) {
            regular = Typeface.createFromAsset(context.getAssets(), "fonts/tajawal_regular.ttf");
        }
        if (bold == null) {
            bold = Typeface.createFromAsset(context.getAssets(), "fonts/tajawal_bold.ttf");
        }
    }

    public static void apply(View root) {
        ensure(root.getContext());
        applyRecursive(root);
    }

    private static void applyRecursive(View view) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            Typeface current = tv.getTypeface();
            tv.setTypeface(current != null && current.isBold() ? bold : regular);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyRecursive(group.getChildAt(i));
            }
        }
    }
}
