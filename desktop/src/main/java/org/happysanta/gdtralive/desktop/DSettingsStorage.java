package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;

public class DSettingsStorage implements GdSettingsStorage {
    @Override
    public void setLong(String key, long value) {

    }

    @Override
    public void setInt(String key, int value) {

    }

    @Override
    public void setBoolean(String key, boolean value) {

    }

    @Override
    public void setString(String key, String value) {

    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public int getInt(String key, int defValue) {
        if ("scale_option".equals(key)) {
            return 230;
        }
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if ("look_ahead_enabled".equals(key)) {
            return false;
        }
        return true;
    }

    @Override
    public String getString(String key, String defValue) {
        return "";
    }
}
