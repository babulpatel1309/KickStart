package com.kickstart.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SessionManager : store all primitives local data
 */
/*SessionManager : This is shared preference class to store local primitive data */
public class Prefs {

    private SharedPreferences prefs;

    public Prefs(SharedPreferences preferences) {
        prefs = preferences;
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }


    public void setBooleanDetail(String key, boolean value) {
        Editor objEditor = prefs.edit();
        objEditor.putBoolean(key, value);
        // commit changes
        objEditor.commit();
    }

    public boolean getBooleanDetail(String key) {
        boolean status = prefs.getBoolean(key, false);
        return status;
    }

    public boolean getBooleanDetailTRUE(String key) {
        boolean status = prefs.getBoolean(key, true);
        return status;
    }

    public void setStringDetail(String key, String value) {
        Editor objEditor = prefs.edit();
        objEditor.putString(key, value);
        // commit changes
        objEditor.commit();
    }

    public String getStringDetail(String key) {
        String status = prefs.getString(key, "");
        return status;
    }

    public String getStringDetail(String key, String defValue) {
        String status = prefs.getString(key, defValue);
        return status;
    }

    public void setIntDetail(String key, int value) {
        Editor objEditor = prefs.edit();
        objEditor.putInt(key, value);
        // commit changes
        objEditor.commit();
    }

    public int getIntDetail(String key) {
        int status = prefs.getInt(key, 0);
        return status;
    }

    public int getInt(String key) {
        int status = prefs.getInt(key, -1);
        return status;
    }

    public long getLongDetail(String key) {
        long status = prefs.getLong(key, -1);
        return status;
    }

    public void setLongDetail(String key, long value) {
        Editor objEditor = prefs.edit();
        objEditor.putLong(key, value);
        // commit changes
        objEditor.commit();
    }

}
