package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuMode;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackParams;
import org.happysanta.gdtralive.game.api.util.Consumer;

public class MenuData {
    private MenuMode menuMode;
    private GameMode gameMode;
    private long lastTrackTime;
    private String trackName;
    private String trackGuid;
    private int selectedLevel;
    private int selectedTrack;
    private int selectedLeague;
    private int newSelectedLevel;
    private int newSelectedTrack;
    private int newSelectedLeague;
    private int newUnlockedLevelCount;
    private int newUnlockedTrackCount;
    private int newUnlockedLeagueCount;
    private TrackRecord recording;
    private Theme theme;
    private Mod mod;
    private String value;
    private TrackParams trackRef;
    private Consumer<Object> handler;

    public MenuData() {
    }

    public MenuData(TrackRecord trackRecord, String name) {
        this.recording = trackRecord;
        this.value = name;
    }

    public MenuData(Mod mod, String name) {
        this.mod = mod;
        this.value = name;
    }

    public MenuData(Theme theme, String name) {
        this.theme = theme;
        this.value = name;
    }

    public MenuData(String value) {
        this.value = value;
    }

    public MenuData(TrackParams track) {
        this.trackRef = track;
    }

    public MenuData(Consumer<Object> handler) {
        this.handler = handler;
    }

    public static MenuData mainMenu() {
        MenuData menuData = new MenuData();
        menuData.setMenuMode(MenuMode.MAIN);
        return menuData;
    }

    public MenuMode getMenuMode() {
        return menuMode;
    }

    public void setMenuMode(MenuMode menuMode) {
        this.menuMode = menuMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public long getLastTrackTime() {
        return lastTrackTime;
    }

    public void setLastTrackTime(long lastTrackTime) {
        this.lastTrackTime = lastTrackTime;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getTrackGuid() {
        return trackGuid;
    }

    public void setTrackGuid(String trackGuid) {
        this.trackGuid = trackGuid;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public int getSelectedTrack() {
        return selectedTrack;
    }

    public void setSelectedTrack(int selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public int getSelectedLeague() {
        return selectedLeague;
    }

    public void setSelectedLeague(int selectedLeague) {
        this.selectedLeague = selectedLeague;
    }

    public int getNewSelectedLevel() {
        return newSelectedLevel;
    }

    public void setNewSelectedLevel(int newSelectedLevel) {
        this.newSelectedLevel = newSelectedLevel;
    }

    public int getNewSelectedTrack() {
        return newSelectedTrack;
    }

    public void setNewSelectedTrack(int newSelectedTrack) {
        this.newSelectedTrack = newSelectedTrack;
    }

    public int getNewSelectedLeague() {
        return newSelectedLeague;
    }

    public void setNewSelectedLeague(int newSelectedLeague) {
        this.newSelectedLeague = newSelectedLeague;
    }

    public int getNewUnlockedLevelCount() {
        return newUnlockedLevelCount;
    }

    public void setNewUnlockedLevelCount(int newUnlockedLevelCount) {
        this.newUnlockedLevelCount = newUnlockedLevelCount;
    }

    public int getNewUnlockedTrackCount() {
        return newUnlockedTrackCount;
    }

    public void setNewUnlockedTrackCount(int newUnlockedTrackCount) {
        this.newUnlockedTrackCount = newUnlockedTrackCount;
    }

    public int getNewUnlockedLeagueCount() {
        return newUnlockedLeagueCount;
    }

    public void setNewUnlockedLeagueCount(int newUnlockedLeagueCount) {
        this.newUnlockedLeagueCount = newUnlockedLeagueCount;
    }

    public TrackParams getTrackRef() {
        return trackRef;
    }

    public TrackRecord getRecording() {
        return recording;
    }

    public Mod getMod() {
        return mod;
    }

    public Theme getTheme() {
        return theme;
    }

    public String getValue() {
        return value;
    }

    public Consumer<Object> getHandler() {
        return handler;
    }

    public void setHandler(Consumer<Object> handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "MenuData{" +
                "menuMode=" + menuMode +
                ", gameMode=" + gameMode +
                ", lastTrackTime=" + lastTrackTime +
                ", trackName='" + trackName + '\'' +
                ", trackGuid='" + trackGuid + '\'' +
                ", selectedLevel=" + selectedLevel +
                ", selectedTrack=" + selectedTrack +
                ", selectedLeague=" + selectedLeague +
                ", newSelectedLevel=" + newSelectedLevel +
                ", newSelectedTrack=" + newSelectedTrack +
                ", newSelectedLeague=" + newSelectedLeague +
                ", newUnlockedLevelCount=" + newUnlockedLevelCount +
                ", newUnlockedTrackCount=" + newUnlockedTrackCount +
                ", newUnlockedLeagueCount=" + newUnlockedLeagueCount +
                '}';
    }
}
