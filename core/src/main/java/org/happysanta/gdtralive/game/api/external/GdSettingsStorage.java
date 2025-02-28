package org.happysanta.gdtralive.game.api.external;

public interface GdSettingsStorage {
    void setLong(String key, long value);

    void setInt(String key, int value);

    void setBoolean(String key, boolean value);

    void setString(String key, String value);

    long getLong(String key, long defValue);

    int getInt(String key, int defValue);

    boolean getBoolean(String key, boolean defValue);

    String getString(String key, String defValue);
}
