package org.happysanta.gdtralive.game.api.model;

import org.happysanta.gdtralive.game.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ModEntity {

    private long id = 0;
    private String guid;
    private String name;
    private String author;
    private List<Integer> trackCountsByLevel = new ArrayList<>();
    private List<Integer> unlockedTracksByLevel = new ArrayList<>();
    private int selectedTrack = 0;
    private int selectedLevel = 0;
    private int selectedLeague = 0;
    private int unlockedLevels = 0;
    private int unlockedLeagues = 0;

    private long addedTs;
    private long installedTs;
    private boolean isDefault;
    private long apiId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTracksCount(int level) {
        //todo refactoring related methods
        return this.trackCountsByLevel.get(level);
    }

    public String getLevelTrackCounts() {
        return Utils.toJson(trackCountsByLevel);
    }

    public int getLevelsCount() {
        return trackCountsByLevel.size();
    }

    public void setTrackCountsByLevel(String counts) {
        this.trackCountsByLevel = Utils.parseIntList(counts);
    }

    public void setTrackCountsByLevel(List<Integer> counts) {
        this.trackCountsByLevel = counts;
    }

    public int getUnlockedTracksCount(int level) {
        return unlockedTracksByLevel.get(level);
    }

    public String getUnlockedTracksString() {
        return Utils.toJson(unlockedTracksByLevel);
    }

    public void setUnlockedTracks(String counts) {
        try {
            this.unlockedTracksByLevel = Utils.parseIntList(counts);
            if (unlockedTracksByLevel.isEmpty()) {
                resetUnlockedTracksToDefaults(trackCountsByLevel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.unlockedTracksByLevel = Utils.parseIntList(String.format("[%s]", counts));
            if (unlockedTracksByLevel.isEmpty()) {
                resetUnlockedTracksToDefaults(trackCountsByLevel);
            }
        }
    }

    public void unlockAllTracks() {
        for (int i = 0; i < trackCountsByLevel.size(); i++) {
            unlockedTracksByLevel.set(i, trackCountsByLevel.get(i) - 1);
        }
    }

    public void setUnlockedTracks(int level, int value) {
        unlockedTracksByLevel.set(level, value);
    }

    public int getSelectedTrack() {
        return selectedTrack;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public int getSelectedLeague() {
        return selectedLeague;
    }

    public void setSelectedTrack(int selectedTrack) {
        this.selectedTrack = selectedTrack;
    }

    public void setSelectedLevel(int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }

    public void setSelectedLeague(int selectedLeague) {
        this.selectedLeague = selectedLeague;
    }

    public int getUnlockedLevels() {
        return unlockedLevels;
    }

    public int getUnlockedLeagues() {
        return unlockedLeagues;
    }

    public void setUnlockedLevels(int unlockedLevels) {
        this.unlockedLevels = unlockedLevels;
    }

    public void setUnlockedLeagues(int unlockedLeagues) {
        this.unlockedLeagues = unlockedLeagues;
    }

    public long getAddedTs() {
        return addedTs;
    }

    public void setAddedTs(long addedTs) {
        this.addedTs = addedTs;
    }

    public long getInstalledTs() {
        return installedTs;
    }

    public void setInstalledTs(long installedTs) {
        this.installedTs = installedTs;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public long getApiId() {
        return apiId;
    }

    public void setApiId(long apiId) {
        this.apiId = apiId;
    }

    public void initIfClear() {
        boolean clear = true;
        for (Integer i : unlockedTracksByLevel) {
            if (i > 0) {
                clear = false;
                break;
            }
        }
        if (unlockedLevels == 0 && clear) {
            setUnlockedLeagues(0);
            setUnlockedLevels(1);
            resetUnlockedTracksToDefaults(trackCountsByLevel);
        }
    }

    public void resetUnlockedTracksToDefaults(List<Integer> trackCounts) {
        unlockedTracksByLevel.clear();
        for (int i = 0; i < trackCounts.size(); i++) {
            unlockedTracksByLevel.add(-1);
        }
        try {
            unlockedTracksByLevel.set(0, 0);
            unlockedTracksByLevel.set(1, 0);
        } catch (Exception ignore) { //if levels count > 1
        }
    }
}
