package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.content.SharedPreferences;

import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ASettingsStorage implements GdSettingsStorage {

    private static SharedPreferences PREFERENCES;
    private static Map<String, Object> CACHE = new ConcurrentHashMap<>();

    static {
        PREFERENCES = getGDActivity().getSharedPreferences("GDSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putLong(key, value);
        editor.apply();
        CACHE.put(key, value);
    }

    @Override
    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putInt(key, value);
        editor.apply();
        CACHE.put(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putBoolean(key, value);
        editor.apply();
        CACHE.put(key, value);
    }

    @Override
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = PREFERENCES.edit();
        editor.putString(key, value);
        editor.apply();
        CACHE.put(key, value);
    }

    @Override
    public long getLong(String key, long defValue) {
        Object value = CACHE.get(key);
        if (value != null) {
            return (long) value;
        }
        return PREFERENCES.getLong(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        Object value = CACHE.get(key);
        if (value != null) {
            return (int) value;
        }
        return PREFERENCES.getInt(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object value = CACHE.get(key);
        if (value != null) {
            return (boolean) value;
        }
        return PREFERENCES.getBoolean(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        Object value = CACHE.get(key);
        if (value != null) {
            return (String) value;
        }
        return PREFERENCES.getString(key, defValue);
    }
}
