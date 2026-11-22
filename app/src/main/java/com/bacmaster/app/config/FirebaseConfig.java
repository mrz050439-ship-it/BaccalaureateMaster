package com.bacmaster.app.config;

/**
 * Firebase REST configuration. The app talks to Firebase over plain HTTPS
 * (Identity Toolkit + Firestore REST), no Firebase SDK and no google-services.json.
 * Paste your web API key and project id here to switch from demo mode to live mode.
 */
public class FirebaseConfig {

    public static final String API_KEY = "PASTE_FIREBASE_WEB_API_KEY";
    public static final String PROJECT_ID = "PASTE_FIREBASE_PROJECT_ID";

    public static boolean isConfigured() {
        return API_KEY != null && !API_KEY.startsWith("PASTE_")
                && PROJECT_ID != null && !PROJECT_ID.startsWith("PASTE_");
    }
}
