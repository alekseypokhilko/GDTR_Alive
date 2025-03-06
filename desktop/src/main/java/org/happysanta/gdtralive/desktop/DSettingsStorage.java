package org.happysanta.gdtralive.desktop;

import org.happysanta.gdtralive.game.api.external.GdSettingsStorage;

import java.util.HashMap;
import java.util.Map;

public class DSettingsStorage implements GdSettingsStorage {

    private final Map<String, Object> settings = new HashMap<>();

    @Override
    public void setLong(String key, long value) {
        settings.put(key, value);
    }

    @Override
    public void setInt(String key, int value) {
        settings.put(key, value);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        settings.put(key, value);
    }

    @Override
    public void setString(String key, String value) {
        settings.put(key, value);
    }

    @Override
    public long getLong(String key, long defValue) {
        Object o = settings.get(key);
        if (o == null) {
            return defValue;
        } else {
            return (Long) o;
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        Object o = settings.get(key);
        if (o == null) {
            return defValue;
        } else {
            return (Integer) o;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Object o = settings.get(key);
        if (o == null) {
            return defValue;
        } else {
            return (Boolean) o;
        }
    }

    @Override
    public String getString(String key, String defValue) {
        Object o = settings.get(key);
        if (o == null) {
            return defValue;
        } else {
            return (String) o;
        }
    }
}
