package com.bacmaster.app.model;

import java.util.ArrayList;
import java.util.List;

/** Plain data models for the Baccalaureate Master curriculum tree. */
public class Models {

    public static class Track {
        public final String id;
        public final String name;
        public final String emoji;
        public final int color;

        public Track(String id, String name, String emoji, int color) {
            this.id = id;
            this.name = name;
            this.emoji = emoji;
            this.color = color;
        }
    }

    public static class GradeYear {
        public final String id;
        public final String title;
        public final String subtitle;
        public final boolean contentReady;

        public GradeYear(String id, String title, String subtitle, boolean contentReady) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.contentReady = contentReady;
        }
    }

    public static class Subject {
        public static final int SECTION_MAIN = 0;
        public static final int SECTION_SPECIALIZATION = 1;
        public static final int SECTION_NON_COUNTED = 2;

        public final String id;
        public final String name;
        public final int section;
        public final boolean countsInTotal;
        public final String note;
        public String emoji = "📚";
        public int color = 0xFF0E7C61;

        public Subject(String id, String name, int section, boolean countsInTotal, String note) {
            this.id = id;
            this.name = name;
            this.section = section;
            this.countsInTotal = countsInTotal;
            this.note = note;
        }
    }

    public static class Lesson {
        public final String id;
        public final String title;
        /** Asset folder with the official book pages (page_01.jpg, ...), or null when not added yet. */
        public final String contentPath;

        public Lesson(String id, String title, String contentPath) {
            this.id = id;
            this.title = title;
            this.contentPath = contentPath;
        }

        public boolean hasContent() {
            return contentPath != null && !contentPath.isEmpty();
        }
    }

    public static class Unit {
        public final String id;
        public final String title;
        public final List<Lesson> lessons = new ArrayList<>();

        public Unit(String id, String title) {
            this.id = id;
            this.title = title;
        }
    }
}
