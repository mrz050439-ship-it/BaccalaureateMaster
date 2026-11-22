package com.bacmaster.app.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/** Local session: login state, favorites, last opened lesson and lesson progress. */
public class SessionManager {

    private static final String PREFS = "bacmaster_session";
    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager get(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("logged_in", false);
    }

    public boolean isDemo() {
        return prefs.getBoolean("demo", false);
    }

    public String userName() {
        return prefs.getString("name", "");
    }

    public String userEmail() {
        return prefs.getString("email", "");
    }

    public String idToken() {
        return prefs.getString("id_token", "");
    }

    public String userId() {
        return prefs.getString("user_id", "");
    }

    public void login(String name, String email, String idToken, String userId, boolean demo) {
        prefs.edit()
                .putBoolean("logged_in", true)
                .putBoolean("demo", demo)
                .putString("name", name)
                .putString("email", email)
                .putString("id_token", idToken)
                .putString("user_id", userId)
                .apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    public int doneLessonsCount() {
        return prefs.getStringSet("done_lessons", new HashSet<String>()).size();
    }

    public boolean isLessonDone(String lessonId) {
        return prefs.getStringSet("done_lessons", new HashSet<String>()).contains(lessonId);
    }

    public void setLessonDone(String lessonId, boolean done) {
        Set<String> set = new HashSet<>(prefs.getStringSet("done_lessons", new HashSet<String>()));
        if (done) set.add(lessonId); else set.remove(lessonId);
        prefs.edit().putStringSet("done_lessons", set).apply();
    }

    // ---- Favorites ----

    public boolean isFavorite(String lessonId) {
        return prefs.getStringSet("fav_lessons", new HashSet<String>()).contains(lessonId);
    }

    public void setFavorite(String lessonId, boolean fav) {
        Set<String> set = new HashSet<>(prefs.getStringSet("fav_lessons", new HashSet<String>()));
        if (fav) set.add(lessonId); else set.remove(lessonId);
        prefs.edit().putStringSet("fav_lessons", set).apply();
    }

    public Set<String> favoriteLessons() {
        return prefs.getStringSet("fav_lessons", new HashSet<String>());
    }

    // ---- Last opened lesson ("continue where you stopped") ----

    public void setLastLesson(String lessonId, String lessonTitle, String subjectName,
                              String contentPath, int color) {
        prefs.edit()
                .putString("last_lesson_id", lessonId)
                .putString("last_lesson_title", lessonTitle)
                .putString("last_lesson_subject", subjectName)
                .putString("last_lesson_content", contentPath)
                .putInt("last_lesson_color", color)
                .apply();
    }

    public boolean hasLastLesson() {
        return prefs.contains("last_lesson_id");
    }

    public String lastLessonId() { return prefs.getString("last_lesson_id", ""); }
    public String lastLessonTitle() { return prefs.getString("last_lesson_title", ""); }
    public String lastLessonSubject() { return prefs.getString("last_lesson_subject", ""); }
    public String lastLessonContent() { return prefs.getString("last_lesson_content", ""); }
    public int lastLessonColor() { return prefs.getInt("last_lesson_color", 0xFF4338CA); }
}
