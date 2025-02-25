package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdSettings;

public class DesktopGdSettings implements GdSettings {

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

    public void resetAll() {
        setPerspectiveEnabled(PERSPECTIVE_ENABLED_DEFAULT);
        setShadowsEnabled(SHADOWS_ENABLED_DEFAULT);
        setDriverSpriteEnabled(DRIVER_SPRITE_ENABLED_DEFAULT);
        setBikeSpriteEnabled(BIKE_SPRITE_ENABLED_DEFAULT);
        setLookAheadEnabled(LOOK_AHEAD_ENABLED_DEFAULT);
        setVibrateOnTouchEnabled(VIBRATE_ENABLED_DEFAULT);
        setKeyboardInMenuEnabled(KEYBOARD_IN_MENU_ENABLED_DEFAULT);
        setInputOption(INPUT_OPTION_DEFAULT);
        setLevelsSort(LEVELS_SORT_DEFAULT);
        setPlayerName(NAME_DEFAULT);
    }

    public long getLevelId() {
        return 1;
    }

    public void setLevelId(long levelId) {
        setLong(LEVEL_ID, levelId);
    }

    public boolean isPerspectiveEnabled() {
        return true;
    }

    public void setPerspectiveEnabled(boolean enabled) {
        setBoolean(PERSPECTIVE_ENABLED, enabled);
    }

    public boolean isShadowsEnabled() {
        return true;
    }

    public void setShadowsEnabled(boolean enabled) {
        setBoolean(SHADOWS_ENABLED, enabled);
    }

    public boolean isDriverSpriteEnabled() {
        return true;
    }

    public void setDriverSpriteEnabled(boolean enabled) {
        setBoolean(DRIVER_SPRITE_ENABLED, enabled);
    }

    public boolean isBikeSpriteEnabled() {
        return true;
    }

    public void setBikeSpriteEnabled(boolean enabled) {
        setBoolean(BIKE_SPRITE_ENABLED, enabled);
    }

    public boolean isLookAheadEnabled() {
        return false;
    }

    public void setLookAheadEnabled(boolean enabled) {
        setBoolean(LOOK_AHEAD_ENABLED, enabled);
    }

    public boolean isKeyboardInMenuEnabled() {
        return false;
    }

    public void setKeyboardInMenuEnabled(boolean enabled) {
        setBoolean(KEYBOARD_IN_MENU_ENABLED, enabled);
    }

    public String getSelectedThemeGuid() {
        return "preferences.getString(SELECTED_THEME_GUID, SELECTED_THEME_GUID_DEFAULT)";
    }

    public void setSelectedThemeGuid(String guid) {
        setString(SELECTED_THEME_GUID, guid);
    }

    public boolean isVibrateOnTouchEnabled() {
        return false;
    }

    public void setVibrateOnTouchEnabled(boolean enabled) {
        setBoolean(VIBRATE_ENABLED, enabled);
    }

    public int getInputOption() {
        return 0;
    }

    public void setInputOption(int value) {
        setInt(INPUT_OPTION, value);
    }

    public void setLevelsSort(int type) {
        setInt(LEVELS_SORT, type);
    }

    public String getPlayerName() {
        return "preferences.getString(NAME, NAME_DEFAULT)";
    }

    public void setPlayerName(String name) {
        setString(NAME, name);
    }

    @Override
    public int getGameSpeed() {
        return 2;
    }

    private static void setLong(String key, long value) {
    }

    private static void setInt(String key, int value) {
    }

    private static void setBoolean(String key, boolean value) {
    }

    private static void setString(String key, String value) {
    }

}
