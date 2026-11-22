package com.bacmaster.app.net;

import com.bacmaster.app.config.FirebaseConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Firestore over the REST API: the generic document helpers the app uses to
 * sync courses, lessons and student progress in later phases.
 */
public class FirestoreApi {

    private static String base() {
        return "https://firestore.googleapis.com/v1/projects/" + FirebaseConfig.PROJECT_ID
                + "/databases/(default)/documents";
    }

    /** Reads one document by path, e.g. "courses/arabic_g2". */
    public static JSONObject getDocument(String path, String idToken)
            throws IOException, JSONException {
        return HttpJson.request("GET", base() + "/" + path, null, idToken);
    }

    /** Lists documents of a collection. */
    public static JSONObject listDocuments(String collection, String idToken)
            throws IOException, JSONException {
        return HttpJson.request("GET", base() + "/" + collection, null, idToken);
    }
}
