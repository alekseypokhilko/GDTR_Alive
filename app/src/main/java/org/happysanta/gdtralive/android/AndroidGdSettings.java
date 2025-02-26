package org.happysanta.gdtralive.android;

import static org.happysanta.gdtralive.android.Helpers.getGDActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import org.happysanta.gdtralive.game.api.external.GdSettings;

public class AndroidGdSettings implements GdSettings {

    private static final String LEVEL_ID = "level_id";
    private static final int LEVEL_ID_DEFAULT = 0;

    private static final String PERSPECTIVE_ENABLED = "perspective_enabled";
    private static final boolean PERSPECTIVE_ENABLED_DEFAULT = true;

    private static final String SHADOWS_ENABLED = "shadows_enabled";
    private static final boolean SHADOWS_ENABLED_DEFAULT = true;

    private static final String DRIVER_SPRITE_ENABLED = "driver_sprite_enabled";
    private static final boolean DRIVER_SPRITE_ENABLED_DEFAULT = true;

    private static final String BIKE_SPRITE_ENABLED = "bike_sprite_enabled";
    private static final boolean BIKE_SPRITE_ENABLED_DEFAULT = true;

    private static final String INPUT_OPTION = "input_option";
    private static final int INPUT_OPTION_DEFAULT = 0;

    private static final String SCALE_OPTION = "scale_option";
    private static final int SCALE_OPTION_DEFAULT = 100;

    private static final String LOOK_AHEAD_ENABLED = "look_ahead_enabled";
    private static final boolean LOOK_AHEAD_ENABLED_DEFAULT = true;

    private static final String VIBRATE_ENABLED = "vibrate_enabled";
    private static final boolean VIBRATE_ENABLED_DEFAULT = true;

    private static final String KEYBOARD_IN_MENU_ENABLED = "keyboard_enabled";
    private static final boolean KEYBOARD_IN_MENU_ENABLED_DEFAULT = true;

    private static final String SELECTED_THEME_GUID = "theme_guid";
    private static final String SELECTED_THEME_GUID_DEFAULT = "e46a37c0-69e1-4646-8f9c-b47247586635";

    private static final String NAME = "name";
    public static final String NAME_DEFAULT = "Player";
    private static final String LEVELS_SORT = "level_sort"; // in download list
    private static final int LEVELS_SORT_DEFAULT = 0;

    private static SharedPreferences preferences;

    static {
        preferences = getGDActivity().getSharedPreferences("GDSettings", Context.MODE_PRIVATE);
    }

    public void resetAll() {
        setPerspectiveEnabled(PERSPECTIVE_ENABLED_DEFAULT);
        setShadowsEnabled(SHADOWS_ENABLED_DEFAULT);
        setDriverSpriteEnabled(DRIVER_SPRITE_ENABLED_DEFAULT);
        setBikeSpriteEnabled(BIKE_SPRITE_ENABLED_DEFAULT);
        setLookAheadEnabled(LOOK_AHEAD_ENABLED_DEFAULT);
        setVibrateOnTouchEnabled(VIBRATE_ENABLED_DEFAULT);
        setKeyboardInMenuEnabled(KEYBOARD_IN_MENU_ENABLED_DEFAULT);
        setInputOption(INPUT_OPTION_DEFAULT);
        setScale(SCALE_OPTION_DEFAULT);
        setLevelsSort(LEVELS_SORT_DEFAULT);
        setPlayerName(NAME_DEFAULT);
    }

    public long getLevelId() {
        return preferences.getLong(LEVEL_ID, LEVEL_ID_DEFAULT);
    }

    public void setLevelId(long levelId) {
        setLong(LEVEL_ID, levelId);
    }

    public boolean isPerspectiveEnabled() {
        return preferences.getBoolean(PERSPECTIVE_ENABLED, PERSPECTIVE_ENABLED_DEFAULT);
    }

    public void setPerspectiveEnabled(boolean enabled) {
        setBoolean(PERSPECTIVE_ENABLED, enabled);
    }

    public boolean isShadowsEnabled() {
        return preferences.getBoolean(SHADOWS_ENABLED, SHADOWS_ENABLED_DEFAULT);
    }

    public void setShadowsEnabled(boolean enabled) {
        setBoolean(SHADOWS_ENABLED, enabled);
    }

    public boolean isDriverSpriteEnabled() {
        return preferences.getBoolean(DRIVER_SPRITE_ENABLED, DRIVER_SPRITE_ENABLED_DEFAULT);
    }

    public void setDriverSpriteEnabled(boolean enabled) {
        setBoolean(DRIVER_SPRITE_ENABLED, enabled);
    }

    public boolean isBikeSpriteEnabled() {
        return preferences.getBoolean(BIKE_SPRITE_ENABLED, BIKE_SPRITE_ENABLED_DEFAULT);
    }

    public void setBikeSpriteEnabled(boolean enabled) {
        setBoolean(BIKE_SPRITE_ENABLED, enabled);
    }

    public boolean isLookAheadEnabled() {
        return preferences.getBoolean(LOOK_AHEAD_ENABLED, LOOK_AHEAD_ENABLED_DEFAULT);
    }

    public void setLookAheadEnabled(boolean enabled) {
        setBoolean(LOOK_AHEAD_ENABLED, enabled);
    }

    public boolean isKeyboardInMenuEnabled() {
        return preferences.getBoolean(KEYBOARD_IN_MENU_ENABLED, KEYBOARD_IN_MENU_ENABLED_DEFAULT);
    }

    public void setKeyboardInMenuEnabled(boolean enabled) {
        setBoolean(KEYBOARD_IN_MENU_ENABLED, enabled);
    }

    public String getSelectedThemeGuid() {
        return preferences.getString(SELECTED_THEME_GUID, SELECTED_THEME_GUID_DEFAULT);
    }

    public void setSelectedThemeGuid(String guid) {
        setString(SELECTED_THEME_GUID, guid);
    }

    public boolean isVibrateOnTouchEnabled() {
        return preferences.getBoolean(VIBRATE_ENABLED, VIBRATE_ENABLED_DEFAULT);
    }

    public void setVibrateOnTouchEnabled(boolean enabled) {
        setBoolean(VIBRATE_ENABLED, enabled);
    }

    public int getInputOption() {
        return preferences.getInt(INPUT_OPTION, INPUT_OPTION_DEFAULT);
    }
    public void setInputOption(int value) {
        setInt(INPUT_OPTION, value);
    }

    public int getScale() {
        return preferences.getInt(SCALE_OPTION, SCALE_OPTION_DEFAULT);
    }

    public void setScale(int value) {
        setInt(SCALE_OPTION, value);
    }

    public void setLevelsSort(int type) {
        setInt(LEVELS_SORT, type);
    }

    public String getPlayerName() {
        return preferences.getString(NAME, NAME_DEFAULT);
    }

    public void setPlayerName(String name) {
        setString(NAME, name);
    }

    @Override
    public int getGameSpeed() {
        return 2;
    }

    private static void setLong(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editorApply(editor);
    }

    private static void setInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editorApply(editor);
    }

    private static void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editorApply(editor);
    }

    private static void setString(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editorApply(editor);
    }

    private static void editorApply(SharedPreferences.Editor editor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            editor.apply();
        else
            editor.commit();
    }

}
