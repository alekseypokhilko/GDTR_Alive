package org.happysanta.gdtralive.game;

import org.happysanta.gdtralive.game.api.external.GdSettings;
import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;

public class GdSettingsImpl implements GdSettings {

    private static final String LEVEL_ID = "level_id";
    private static final int LEVEL_ID_DEFAULT = 0;

    private static final String PERSPECTIVE_ENABLED = "perspective_enabled";
    private static final boolean PERSPECTIVE_ENABLED_DEFAULT = true;

    private static final String RECORDING_ENABLED = "perspective_enabled";
    private static final boolean RECORDING_ENABLED_DEFAULT = false;

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
    private static final boolean LOOK_AHEAD_ENABLED_DEFAULT = false;

    private static final String VIBRATE_ENABLED = "vibrate_enabled";
    private static final boolean VIBRATE_ENABLED_DEFAULT = true;

    private static final String KEYBOARD_IN_MENU_ENABLED = "keyboard_enabled";
    private static final boolean KEYBOARD_IN_MENU_ENABLED_DEFAULT = true;

    private static final String SELECTED_THEME = "theme_guid";
    private static final String SELECTED_THEME_DEFAULT = "e46a37c0-69e1-4646-8f9c-b47247586635";

    private static final String NAME = "name";
    public static final String NAME_DEFAULT = "Player";
    private static final String LEVELS_SORT = "level_sort"; // in download list
    private static final int LEVELS_SORT_DEFAULT = 0;

    private final GdSettingsStorage storage;

    public GdSettingsImpl(GdSettingsStorage storage) {
        this.storage = storage;
    }

    public void resetAll() {
        setRecordingEnabled(RECORDING_ENABLED_DEFAULT);
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
        return storage.getLong(LEVEL_ID, LEVEL_ID_DEFAULT);
    }

    public void setLevelId(long levelId) {
        storage.setLong(LEVEL_ID, levelId);
    }

    public boolean isPerspectiveEnabled() {
        return storage.getBoolean(PERSPECTIVE_ENABLED, PERSPECTIVE_ENABLED_DEFAULT);
    }

    public void setPerspectiveEnabled(boolean enabled) {
        storage.setBoolean(PERSPECTIVE_ENABLED, enabled);
    }

    public boolean isRecordingEnabled() {
        return storage.getBoolean(RECORDING_ENABLED, RECORDING_ENABLED_DEFAULT);
    }

    public void setRecordingEnabled(boolean enabled) {
        storage.setBoolean(RECORDING_ENABLED, enabled);
    }

    public boolean isShadowsEnabled() {
        return storage.getBoolean(SHADOWS_ENABLED, SHADOWS_ENABLED_DEFAULT);
    }

    public void setShadowsEnabled(boolean enabled) {
        storage.setBoolean(SHADOWS_ENABLED, enabled);
    }

    public boolean isDriverSpriteEnabled() {
        return storage.getBoolean(DRIVER_SPRITE_ENABLED, DRIVER_SPRITE_ENABLED_DEFAULT);
    }

    public void setDriverSpriteEnabled(boolean enabled) {
        storage.setBoolean(DRIVER_SPRITE_ENABLED, enabled);
    }

    public boolean isBikeSpriteEnabled() {
        return storage.getBoolean(BIKE_SPRITE_ENABLED, BIKE_SPRITE_ENABLED_DEFAULT);
    }

    public void setBikeSpriteEnabled(boolean enabled) {
        storage.setBoolean(BIKE_SPRITE_ENABLED, enabled);
    }

    public boolean isLookAheadEnabled() {
        return storage.getBoolean(LOOK_AHEAD_ENABLED, LOOK_AHEAD_ENABLED_DEFAULT);
    }

    public void setLookAheadEnabled(boolean enabled) {
        storage.setBoolean(LOOK_AHEAD_ENABLED, enabled);
    }

    public boolean isKeyboardInMenuEnabled() {
        return storage.getBoolean(KEYBOARD_IN_MENU_ENABLED, KEYBOARD_IN_MENU_ENABLED_DEFAULT);
    }

    public void setKeyboardInMenuEnabled(boolean enabled) {
        storage.setBoolean(KEYBOARD_IN_MENU_ENABLED, enabled);
    }

    public String getSelectedTheme() {
        return storage.getString(SELECTED_THEME, SELECTED_THEME_DEFAULT);
    }

    public void setSelectedTheme(String guid) {
        storage.setString(SELECTED_THEME, guid);
    }

    public boolean isVibrateOnTouchEnabled() {
        return storage.getBoolean(VIBRATE_ENABLED, VIBRATE_ENABLED_DEFAULT);
    }

    public void setVibrateOnTouchEnabled(boolean enabled) {
        storage.setBoolean(VIBRATE_ENABLED, enabled);
    }

    public int getInputOption() {
        return storage.getInt(INPUT_OPTION, INPUT_OPTION_DEFAULT);
    }
    public void setInputOption(int value) {
        storage.setInt(INPUT_OPTION, value);
    }

    public int getScale() {
        return storage.getInt(SCALE_OPTION, SCALE_OPTION_DEFAULT);
    }

    public void setScale(int value) {
        storage.setInt(SCALE_OPTION, value);
    }

    public void setLevelsSort(int type) {
        storage.setInt(LEVELS_SORT, type);
    }

    public String getPlayerName() {
        return storage.getString(NAME, NAME_DEFAULT);
    }

    public void setPlayerName(String name) {
        storage.setString(NAME, name);
    }

    @Override
    public int getGameSpeed() {
        return 2;
    }
}
