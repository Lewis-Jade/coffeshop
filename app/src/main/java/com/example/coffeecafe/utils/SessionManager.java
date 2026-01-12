package com.example.coffeecafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.coffeecafe.config.SupabaseClient;

public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;

    private SessionManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUserSession(String userId, String email, String token, boolean isAdmin) {
        editor.putString(Constants.PREF_USER_ID, userId);
        editor.putString(Constants.PREF_USER_EMAIL, email);
        editor.putString(Constants.PREF_USER_TOKEN, token);
        editor.putBoolean(Constants.PREF_IS_ADMIN, isAdmin);
        editor.apply();
        
        // Set token in Supabase client
        SupabaseClient.getInstance().setAuthToken(token);
    }

    public String getUserId() {
        return prefs.getString(Constants.PREF_USER_ID, null);
    }

    public String getUserEmail() {
        return prefs.getString(Constants.PREF_USER_EMAIL, null);
    }

    public String getUserToken() {
        return prefs.getString(Constants.PREF_USER_TOKEN, null);
    }

    public boolean isAdmin() {
        return prefs.getBoolean(Constants.PREF_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return getUserToken() != null && getUserId() != null;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
        SupabaseClient.getInstance().setAuthToken(null);
    }

    public void restoreSession() {
        String token = getUserToken();
        if (token != null) {
            SupabaseClient.getInstance().setAuthToken(token);
        }
    }
}
