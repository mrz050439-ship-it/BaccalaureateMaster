package com.bacmaster.app.net;

import android.os.Handler;
import android.os.Looper;

import com.bacmaster.app.config.FirebaseConfig;

import org.json.JSONObject;

/** Firebase Authentication over the Identity Toolkit REST API. */
public class FirebaseAuthApi {

    public interface Callback {
        void onSuccess(JSONObject result);
        void onError(String arabicMessage);
    }

    private static final String BASE = "https://identitytoolkit.googleapis.com/v1/accounts:";

    public static void signIn(String email, String password, Callback callback) {
        call(BASE + "signInWithPassword?key=" + FirebaseConfig.API_KEY, email, password, callback);
    }

    public static void signUp(String email, String password, Callback callback) {
        call(BASE + "signUp?key=" + FirebaseConfig.API_KEY, email, password, callback);
    }

    private static void call(final String url, final String email, final String password,
                             final Callback callback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject body = new JSONObject();
                    body.put("email", email);
                    body.put("password", password);
                    body.put("returnSecureToken", true);
                    final JSONObject res = HttpJson.request("POST", url, body, null);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(res);
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(toArabic(e.getMessage()));
                        }
                    });
                }
            }
        }).start();
    }

    private static String toArabic(String raw) {
        if (raw == null) return "حدث خطأ، حاول مرة أخرى";
        if (raw.contains("EMAIL_NOT_FOUND") || raw.contains("INVALID_LOGIN_CREDENTIALS")) {
            return "بيانات الدخول غير صحيحة";
        }
        if (raw.contains("INVALID_PASSWORD")) return "كلمة المرور غير صحيحة";
        if (raw.contains("EMAIL_EXISTS")) return "هذا البريد مسجل بالفعل، سجّل دخولك";
        if (raw.contains("WEAK_PASSWORD")) return "كلمة المرور ضعيفة، استخدم 6 أحرف على الأقل";
        if (raw.contains("INVALID_EMAIL")) return "البريد الإلكتروني غير صحيح";
        return "تعذر الاتصال، تحقق من الإنترنت وحاول مرة أخرى";
    }
}
