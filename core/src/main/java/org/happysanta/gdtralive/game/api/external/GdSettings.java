package org.happysanta.gdtralive.game.api.external;

public interface GdSettings {

    void resetAll();
    long getLevelId() ;
    void setLevelId(long levelId);
    boolean isRecordingEnabled();
    void setRecordingEnabled(boolean enabled);
    boolean isGhostEnabled();
    void setGhostEnabled(boolean enabled);
    boolean isGodModeEnabled();
    void setGodModeEnabled(boolean enabled);
    boolean isPerspectiveEnabled();
    void setPerspectiveEnabled(boolean enabled);
    boolean isShadowsEnabled();
    void setShadowsEnabled(boolean enabled);
    boolean isDriverSpriteEnabled();
    void setDriverSpriteEnabled(boolean enabled);
    boolean isBikeSpriteEnabled();
    void setBikeSpriteEnabled(boolean enabled);
    boolean isLookAheadEnabled();
    void setLookAheadEnabled(boolean enabled);
    boolean isKeyboardInMenuEnabled();
    void setKeyboardInMenuEnabled(boolean enabled);
    String getSelectedTheme();
    void setSelectedTheme(String guid);
    boolean isVibrateOnTouchEnabled();
    void setVibrateOnTouchEnabled(boolean enabled);
    int getInputOption();
    void setInputOption(int value);
    int getScale();
    void setScale(int value);
    void setLevelsSort(int type);
    String getPlayerName();
    void setPlayerName(String name);

    int getGameSpeed();
}
