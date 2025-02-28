package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;

public class ASettingsStorage implements GdSettingsStorage {

    private static SharedPreferences preferences;

    static {
        preferences = getGDActivity().getSharedPreferences("GDSettings", Context.MODE_PRIVATE);
    }

    @Override
    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editorApply(editor);
    }

    @Override
    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editorApply(editor);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editorApply(editor);
    }

    @Override
    public void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editorApply(editor);
    }

    @Override
    public long getLong(String key, long defValue) {
        return preferences.getLong(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    private void editorApply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            editor.apply();
        else
            editor.commit();
    }
}
