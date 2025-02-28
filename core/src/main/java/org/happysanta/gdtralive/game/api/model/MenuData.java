package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.api.GameMode;
import org.happysanta.gdtralive.game.api.MenuMode;
import org.happysanta.gdtralive.game.api.dto.Theme;
import org.happysanta.gdtralive.game.api.dto.TrackReference;

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
    private String fileName;
    private TrackReference trackRef;

    public MenuData() {
    }

    public MenuData(TrackRecord trackRecord) {
        this.recording = trackRecord;
    }

    public MenuData(Mod mod, String name) {
        this.mod = mod;
        this.fileName = name;
    }

    public MenuData(Theme theme, String name) {
        this.theme = theme;
        this.fileName = name;
    }

    public MenuData(String name) {
        this.fileName = name;
    }

    public MenuData(TrackReference track) {
        this.trackRef = track;
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

    public TrackReference getTrackRef() {
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

    public String getFileName() {
        return fileName;
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
