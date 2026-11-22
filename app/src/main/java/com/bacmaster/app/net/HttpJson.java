package com.bacmaster.app.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/** Minimal HTTPS JSON helper on top of HttpURLConnection (framework only). */
public class HttpJson {

    public static JSONObject request(String method, String urlStr, JSONObject body, String idToken)
            throws IOException, JSONException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        try {
            conn.setRequestMethod(method);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            if (idToken != null && !idToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + idToken);
            }
            if (body != null) {
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes(Charset.forName("UTF-8")));
                os.close();
            }
            int code = conn.getResponseCode();
            InputStream is = code < 400 ? conn.getInputStream() : conn.getErrorStream();
            String resp = is == null ? "" : readAll(is);
            if (code >= 400) {
                String message = resp;
                try {
                    message = new JSONObject(resp).getJSONObject("error").optString("message", resp);
                } catch (Exception ignore) {
                    // keep raw response
                }
                throw new IOException(message);
            }
            return resp.isEmpty() ? new JSONObject() : new JSONObject(resp);
        } finally {
            conn.disconnect();
        }
    }

    private static String readAll(InputStream is) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) out.write(buf, 0, n);
        is.close();
        return new String(out.toByteArray(), Charset.forName("UTF-8"));
    }
}
